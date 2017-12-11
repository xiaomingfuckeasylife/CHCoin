package nxt;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;

import nxt.db.DerivedDbTable;
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
				
			}
			
		}, false);
		
	}
	
	private void addGenesisBlock(){
		
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
