package nxt;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONStreamAware;

import nxt.crypto.Crypto;
import nxt.db.Db;
import nxt.db.DerivedDbTable;
import nxt.util.JSON;
import nxt.util.Listener;
import nxt.util.Listeners;
import nxt.util.Logger;
import nxt.util.ThreadPool;

/**
 * 
 * @author clark
 * 
 * 2017年12月11日 上午9:29:33
 * 
 */
public final class BlockchainProcessorImpl implements BlockchainProcessor{
	/**
	 * block cache mb 
	 */
	private static final int BLOCKCACHEMB = Nxt.getIntProperty("nxt.blockCacheMB",40);
	/**
	 * gpu verification
	 */
	private static final boolean oclVerify = Nxt.getBooleanProperties("burst.oclVerify");
	/**
	 * thread to verify 
	 */
	private static final int oclThreshold = Nxt.getIntProperty("nxt.oclThreshold",50);
	/**
	 * thread to wait 
	 */
	private static final int oclWaitThreshold = Nxt.getIntProperty("nxt.oclWaitThreshold",2000);
	/**
	 * semaphore to synchronize
	 */
	private static final Semaphore gpuUsage = new Semaphore(2);
	
	private static final BlockchainProcessorImpl me = new BlockchainProcessorImpl();
	
	
	private volatile boolean isScanning ;
	
	private volatile boolean forceScan = Nxt.getBooleanProperties("nxt.forceScan");
	
	private static BlockchainImpl blockchain = BlockchainImpl.getInstance();
	
	public void forceScanAtStart(){
		forceScan = true;
	}
	
	public boolean isScannning(){
		return isScanning;
	}
	
	public static BlockchainProcessorImpl getInstance(){
		return me;
	}
	
	private static final Map<Long,Block> blockCache = new HashMap<>();
	private static final Map<Long,Long> reverseCache = new HashMap<>();
	private static final List<Long> unverified = new LinkedList<>();
	private static int blockCacheSize = 0 ;
	
	/**
	 * listener of block of blockchain
	 */
	private final Listeners<Block,Event> blockListeners = new Listeners<>();
	
	private final boolean trimDerivedTables = Nxt.getBooleanProperties("nxt.trimDerivedTables");
	
	private List<DerivedDbTable> derivedTables = new CopyOnWriteArrayList<>();
	
	private volatile int lastTrimHeight;
	private Long lastDownloaded = 0L;
	private volatile Peer lastBlockchainFeeder;
	private volatile int lastBlockchainFeederHeight;
	private volatile boolean getMoreBlocks = true;
	
	private BlockchainProcessorImpl(){
		
		blockListeners.addListener(new Listener<Block>() {
			@Override
			public void notify(Block t) {
				// synchronize log Message when the block height can be divided by 5000 .
				if(t.getHeight() % 5000 == 0){
					Logger.logMessage("processed block " + t.getHeight());
				}
			}
		}, BlockchainProcessor.Event.BLOCK_SCANED);
		
		blockListeners.addListener(new Listener<Block>() {
			@Override
			public void notify(Block t) {
				// block pushed log message if the block height can be divided by 5000  
				if(t.getHeight() % 5000 == 0){
					Logger.logMessage("processed block " + t.getHeight());
				}
			}
		}, BlockchainProcessor.Event.BLOCK_PUSHED);
		
		// for hard fork 
		if(trimDerivedTables){
			blockListeners.addListener(new Listener<Block>() {
				@Override
				public void notify(Block t) {
					// at least rollback 1440 height .
					if(t.getHeight() % 1440 == 0){
						lastTrimHeight = Math.max(t.getHeight() - Constants.MAX_ROLLBACK, 0);
						if(lastTrimHeight > 0){
							for(DerivedDbTable dtable : derivedTables){
								dtable.trim(lastTrimHeight);
							}
						}
					}
				}
			}, Event.AFTER_BLOCK_APPLY);
		}
		
		blockListeners.addListener(new Listener<Block>() {
			
			@Override
			public void notify(Block t) {
				Logger.logMessage("scan end at height : " + t.getHeight());
			}
			
		}, Event.RESCAN_END);
		
		ThreadPool.runBeforeStart(new Runnable() {
			@Override
			public void run() {
				addGenesisBlock();
				if(forceScan){
					scan(0);
				}
			}
			
		}, false);
		
		ThreadPool.scheduleThread("GetMoreBlocks", getMoreBlocksThread, 2);
		
	}
	
	private static volatile boolean hasMoreBlocks = true;
	/**
	 * get block from other peer
	 */
	private Runnable getMoreBlocksThread = new Runnable() {
		
		private JSONStreamAware stream ;
		
		{
			JSONObject obj = new JSONObject();
			obj.put("requestType", "getCumulativeDifficuty");
			stream = JSON.prepareRequest(obj);
		}
		private boolean peerHasMore;
		
		@Override
		public void run() {
			
			if(!hasMoreBlocks){
				return ;
			}
			
			Peer peer = Peers.getBlockFeederPeer();
			if(peer == null){
				return;
			}
			
			JSONObject response = peer.send(stream);
			if (response == null) {
				return;
			}
			BigInteger curCumulativeDifficulty = blockchain.getLastBlock().getCumulativeDifficulty();
			String peerCumulativeDifficulty = (String) response.get("cumulativeDifficulty");
			if (peerCumulativeDifficulty == null) {
				return;
			}
			BigInteger betterCumulativeDifficulty = new BigInteger(peerCumulativeDifficulty);
			if (betterCumulativeDifficulty.compareTo(curCumulativeDifficulty) < 0) {
				return;
			}
			if (response.get("blockchainHeight") != null) {
				lastBlockchainFeeder = peer;
				lastBlockchainFeederHeight = ((Long) response.get("blockchainHeight")).intValue();
			}
			if (betterCumulativeDifficulty.equals(curCumulativeDifficulty)) {
				return;
			}

			long commonBlockId = Genesis.GENESIS_BLOCK_ID;

			if (blockchain.getLastBlock().getId() != Genesis.GENESIS_BLOCK_ID) {
				commonBlockId = getCommonMilestoneBlockId(peer);
			}
			if (commonBlockId == 0 || !peerHasMore) {
				return;
			}

			commonBlockId = getCommonBlockId(peer, commonBlockId);
			if (commonBlockId == 0 || !peerHasMore) {
				return;
			}

			final Block commonBlock = BlockDb.findBlock(commonBlockId);
			if (commonBlock == null || blockchain.getHeight() - commonBlock.getHeight() >= 720) {
				return;
			}
			
			long currentBlockId = (lastDownloaded == 0 ? commonBlockId : lastDownloaded);
			if(commonBlock.getHeight() < blockchain.getLastBlock().getHeight()) { // fork point
				currentBlockId = commonBlockId;
			}
			else {
				synchronized (blockCache) {
					long checkBlockId = currentBlockId;
					while (checkBlockId != blockchain.getLastBlock().getId()) {
						if (blockCache.get(checkBlockId) == null) {
							currentBlockId = blockchain.getLastBlock().getId();
							break;
						}
						checkBlockId = blockCache.get(checkBlockId).getPreviousBlockId();
					}
				}
			}
			
			
			
		}
		
		private long getCommonBlockId(Peer peer, long commonBlockId) {

			while (true) {
				JSONObject request = new JSONObject();
				request.put("requestType", "getNextBlockIds");
				request.put("blockId", Convert.toUnsignedLong(commonBlockId));
				JSONObject response = peer.send(JSON.prepareRequest(request));
				if (response == null) {
					return 0;
				}
				JSONArray nextBlockIds = (JSONArray) response.get("nextBlockIds");
				if (nextBlockIds == null || nextBlockIds.size() == 0) {
					return 0;
				}
				// prevent overloading with blockIds
				if (nextBlockIds.size() > 1440) {
					Logger.logDebugMessage("Obsolete or rogue peer " + peer.getPeerAddress() + " sends too many nextBlockIds, blacklisting");
					peer.blacklist();
					return 0;
				}
				
				for (Object nextBlockId : nextBlockIds) {
					long blockId = Convert.parseUnsignedLong((String) nextBlockId);
					if (! BlockDb.hasBlock(blockId)) {
						return commonBlockId;
					}
					commonBlockId = blockId;
				}
			}

		}
		public long getCommonMilestoneBlockId(Peer peer){

			String lastMilestoneBlockId = null;

			while (true) {
				JSONObject milestoneBlockIdsRequest = new JSONObject();
				milestoneBlockIdsRequest.put("requestType", "getMilestoneBlockIds");
				if (lastMilestoneBlockId == null) {
					milestoneBlockIdsRequest.put("lastBlockId", blockchain.getLastBlock().getStringId());
				} else {
					milestoneBlockIdsRequest.put("lastMilestoneBlockId", lastMilestoneBlockId);
				}

				JSONObject response = peer.send(JSON.prepareRequest(milestoneBlockIdsRequest));
				if (response == null) {
					return 0;
				}
				JSONArray milestoneBlockIds = (JSONArray) response.get("milestoneBlockIds");
				if (milestoneBlockIds == null) {
					return 0;
				}
				if (milestoneBlockIds.isEmpty()) {
					return Genesis.GENESIS_BLOCK_ID;
				}
				// prevent overloading with blockIds
				if (milestoneBlockIds.size() > 20) {
					Logger.logDebugMessage("Obsolete or rogue peer " + peer.getPeerAddress() + " sends too many milestoneBlockIds, blacklisting");
					peer.blacklist();
					return 0;
				}
				if (Boolean.TRUE.equals(response.get("last"))) {
					peerHasMore = false;
				}
				for (Object milestoneBlockId : milestoneBlockIds) {
					long blockId = Convert.parseUnsignedLong((String) milestoneBlockId);
					if (BlockDb.hasBlock(blockId)) {
						if (lastMilestoneBlockId == null && milestoneBlockIds.size() > 1) {
							peerHasMore = false;
						}
						return blockId;
					}
					lastMilestoneBlockId = (String) milestoneBlockId;
				}
			}
		}
	};
	
	
	
	
	/**
	 * scan from height 
	 * @param height
	 */
	private static void scan(int height){
		// TODO
	}
	
	private void addGenesisBlock(){
		if(BlockDb.hasBlock(Genesis.GENESIS_BLOCK_ID)){
			Logger.logMessage("Genesis already in database");
			BlockImpl block = BlockDb.findLastBlock();
			this.blockchain.setLastBlock(block);
			int height = block.getHeight();
			Logger.logMessage("Last block height:" + height);
			return ;
		}
		Logger.logMessage("Genesis block not in database , starting from scratch");
		try{
			// calculate payLoadhash . 
			List<TransactionImpl> transactions = new ArrayList<>();
			MessageDigest md = Crypto.sha256();
			for(int i=0;i<transactions.size();i++){
				// get transaction bytes
				md.update(transactions.get(i).getBytes());
			}
			
			// get genesis blockAts
			ByteBuffer bf = ByteBuffer.allocate(0);
			bf.order(ByteOrder.LITTLE_ENDIAN);
			byte[] ats = bf.array();
			
			BlockImpl genesisBlock = new BlockImpl();
			genesisBlock.setVersion(-1);
			genesisBlock.setTimestamp(0);;
			genesisBlock.setPreviousBlockId(0);
			genesisBlock.setTotalAmountNQT(0);
			genesisBlock.setTotalFeeNQT(0);
			// every transaction is 128 byte 
			genesisBlock.setPayloadLength(transactions.size() * 128);
			genesisBlock.setPayloadHash(md.digest());
			genesisBlock.setGeneratorPublicKey(Genesis.CREATOR_PUBLIC_KEY);
			// blank
			genesisBlock.setGenerationSignature(new byte[32]);
			genesisBlock.setBlockSignature(Genesis.GENESIS_BLOCK_SIGNATURE);
			genesisBlock.setPreviousBlockHash(null);
			genesisBlock.setBlockTransactions(transactions);
			genesisBlock.setNonce(0);
			genesisBlock.setBlockATs(ats);
			genesisBlock.setPrevious(null);
			addBlock(genesisBlock);
		}catch(Exception ex){
			throw new RuntimeException(ex.getMessage(),ex);
		}
	}
	
	private void addBlock(BlockImpl genesisBlock){
		
		try(Connection conn = Db.getConnection();){
			BlockDb.saveBlock(conn, genesisBlock);
			this.blockchain.setLastBlock(genesisBlock);
		}catch(Exception ex){
			throw new RuntimeException(ex.getMessage(), ex);
		}
		
	}
	
	@Override
	public boolean addListener(Listener<Block> listener, Event eventType) {
		return false;
	}

	@Override
	public boolean removeListener(Listener<Block> listener, Event eventType) {
		return false;
	}
	
	/**
	 * TO-DO
	 * @param publicKey
	 * @param secretPhrase
	 * @param nonce
	 */
	public void generatorBlock(byte[] publicKey,String secretPhrase,long nonce){
		
	}
}
