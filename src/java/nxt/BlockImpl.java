package nxt;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.json.simple.JSONObject;

/**
 * 
 * @author clark
 * 
 * 2017年12月12日 上午10:45:18
 * 
 */
public class BlockImpl implements Block {
	
	
 	private int version;
    public List<TransactionImpl> getBlockTransactions() {
		return blockTransactions;
	}

	public byte[] getBlockATs() {
		return blockATs;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

	public void setPreviousBlockId(long previousBlockId) {
		this.previousBlockId = previousBlockId;
	}

	public void setGeneratorPublicKey(byte[] generatorPublicKey) {
		this.generatorPublicKey = generatorPublicKey;
	}

	public void setPreviousBlockHash(byte[] previousBlockHash) {
		this.previousBlockHash = previousBlockHash;
	}

	public void setTotalAmountNQT(long totalAmountNQT) {
		this.totalAmountNQT = totalAmountNQT;
	}

	public void setTotalFeeNQT(long totalFeeNQT) {
		this.totalFeeNQT = totalFeeNQT;
	}

	public void setPayloadLength(int payloadLength) {
		this.payloadLength = payloadLength;
	}

	public void setGenerationSignature(byte[] generationSignature) {
		this.generationSignature = generationSignature;
	}

	public void setPayloadHash(byte[] payloadHash) {
		this.payloadHash = payloadHash;
	}

	private int timestamp;
    public void setBlockTransactions(List<TransactionImpl> blockTransactions) {
		this.blockTransactions = blockTransactions;
	}
    
	public void setBlockSignature(byte[] blockSignature) {
		this.blockSignature = blockSignature;
	}

	public void setCumulativeDifficulty(BigInteger cumulativeDifficulty) {
		this.cumulativeDifficulty = cumulativeDifficulty;
	}

	public void setBaseTarget(long baseTarget) {
		this.baseTarget = baseTarget;
	}

	public void setNextBlockId(long nextBlockId) {
		this.nextBlockId = nextBlockId;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setStringId(String stringId) {
		this.stringId = stringId;
	}

	public void setGeneratorId(long generatorId) {
		this.generatorId = generatorId;
	}

	public void setNonce(long nonce) {
		this.nonce = nonce;
	}

	public void setBlockATs(byte[] blockATs) {
		this.blockATs = blockATs;
	}

	private  long previousBlockId;
    private  byte[] generatorPublicKey;
    private  byte[] previousBlockHash;
    private  long totalAmountNQT;
    private  long totalFeeNQT;
    private  int payloadLength;
    private  byte[] generationSignature;
    private  byte[] payloadHash;
    private volatile List<TransactionImpl> blockTransactions;
    
    private byte[] blockSignature;
    private BigInteger cumulativeDifficulty = BigInteger.ZERO;
    private long baseTarget = Constants.INITIAL_BASE_TARGET;
    private volatile long nextBlockId;
    private int height = -1;
    private volatile long id;
    private volatile String stringId = null;
    private volatile long generatorId;
    private long nonce;
    private byte[] blockATs;
    public BlockImpl(){}
    /**
     * make a Block
     * @param version
     * @param timestamp
     * @param previousBlockId
     * @param generatorPublicKey
     * @param previousBlockHash
     * @param totalAmountNQT
     * @param totalFeeNQT
     * @param payloadLength
     * @param generationSignature
     * @param payloadHash
     * @param blockTransactions
     * @param nonce
     * @param blockAts
     */
    public BlockImpl(int version,int timestamp,long previousBlockId,byte[] generatorPublicKey,byte[] previousBlockHash,
    		long totalAmountNQT,long totalFeeNQT,int payloadLength,byte[] generationSignature,byte[] payloadHash,List<TransactionImpl> blockTransactions,
    		long nonce,byte[] blockAts) {
    	this.version = version;
    	this.timestamp = timestamp;
    	this.previousBlockId=previousBlockId;
    	this.generationSignature = generationSignature;
    	this.generatorPublicKey = generatorPublicKey;
    	this.previousBlockHash = previousBlockHash;
    	this.totalAmountNQT = totalAmountNQT;
    	this.totalFeeNQT = totalAmountNQT;
    	this.payloadHash = payloadHash;
    	this.payloadLength= payloadLength;
    	if(blockTransactions != null){
    		this.blockTransactions = Collections.unmodifiableList(blockTransactions);
    		// the block transaction has to be sorted . 
    		long tmpId= 0;
    		for(int i=0;i<blockTransactions.size();i++){
    			long id = blockTransactions.get(i).getId();
    			if(id <= tmpId && tmpId != 0){
    				throw new RuntimeException("block transaction are not sorted .");
    			}
    			tmpId = id;
    		}
    	}
    	this.nonce = nonce;
    	this.blockATs = blockAts;
	}
    
    /**
     * fuckingn too long 
     * @param version
     * @param timestamp
     * @param previousBlockId
     * @param totalAmountNQT
     * @param totalFeeNQT
     * @param payloadLength
     * @param payloadHash
     * @param generatorPublicKey
     * @param generationSignature
     * @param blockSignature
     * @param previousBlockHash
     * @param cumulativeDifficulty
     * @param baseTarget
     * @param nextBlockId
     * @param height
     * @param id
     * @param nonce
     * @param blockATs
     */
    BlockImpl(int version, int timestamp, long previousBlockId, long totalAmountNQT, long totalFeeNQT, int payloadLength,
            byte[] payloadHash, byte[] generatorPublicKey, byte[] generationSignature, byte[] blockSignature,
            byte[] previousBlockHash, BigInteger cumulativeDifficulty, long baseTarget, long nextBlockId, int height, Long id, long nonce , byte[] blockATs)
          {
      this(version,timestamp,previousBlockId,generatorPublicKey,previousBlockHash,
      		totalAmountNQT,totalFeeNQT,payloadLength,generationSignature,payloadHash,null,
      		nonce,blockATs);
      this.cumulativeDifficulty = cumulativeDifficulty;
      this.baseTarget = baseTarget;
      this.nextBlockId = nextBlockId;
      this.height = height;
      this.id = id;
    }
    
    
	@Override
	public int getVersion() {
		return version;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public String getStringId() {
		return id+"";
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public int getTimestamp() {
		return timestamp;
	}

	@Override
	public long getGeneratorId() {
		return generatorId;
	}

	@Override
	public Long getNonce() {
		return nonce;
	}

	@Override
	public int getScoopNum() {
		return 0;
	}

	@Override
	public byte[] getGeneratorPublicKey() {
		return generatorPublicKey;
	}

	@Override
	public byte[] getBlockHash() {
		return null;
	}
	
	@Override
	public long getPreviousBlockId() {
		return previousBlockId;
	}

	@Override
	public byte[] getPreviousBlockHash() {
		return previousBlockHash;
	}
	
	@Override
	public long getNextBlockId() {
		return nextBlockId;
	}

	@Override
	public long getTotalAmountNQT() {
		return totalAmountNQT;
	}

	@Override
	public long getTotalFeeNQT() {
		return totalFeeNQT;
	}
	
	public byte[] getPayloadHash() {
		return payloadHash;
	}

	@Override
	public List<TransactionImpl> getTransactions() {
		if(blockTransactions == null){
			this.blockTransactions = Collections.unmodifiableList(TransactionDb.findBlockTransactions(getId()));
			for(TransactionImpl tran : blockTransactions){
				tran.setBlock(this);
			}
		}
		return blockTransactions;
	}

	@Override
	public byte[] getGenerationSignature() {
		return generationSignature;
	}

	@Override
	public byte[] getBlockSignature() {
		return blockSignature;
	}

	@Override
	public long getBaseTarget() {
		return baseTarget;
	}

	@Override
	public long getBlockReward() {
		return 0;
	}

	@Override
	public BigInteger getCumulativeDifficulty() {
		return cumulativeDifficulty;
	}

	@Override
	public JSONObject getJSONObject() {
		return null;
	}
	
	@Override
	public byte[] getBlockAts() {
		return blockATs;
	}
	
	public void setPrevious(BlockImpl previousBlock){
		if(previousBlock != null){
			if(previousBlock.getId() != getPreviousBlockId()){
				throw new IllegalStateException("previous block id doesn't match");
			}
			this.height = previousBlock.getHeight() +1;
			// calculate baseTarget by previousBlock
			this.calculateBaseTarget(previousBlock);
		}else{
			this.height = 0;
		}
		
		for(TransactionImpl tran : getTransactions()){
			tran.setBlock(this);
		}
	}
	
}
