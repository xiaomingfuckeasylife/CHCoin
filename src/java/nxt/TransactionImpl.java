package nxt;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.json.simple.JSONObject;

import com.mysql.jdbc.Buffer;

import nxt.db.DbKey;
import nxt.db.TransactionType;

/**
 * 
 * @author clark
 * 
 * 2017年12月13日 上午11:13:14
 * 
 */
public class TransactionImpl implements Transaction{
	
	
	public TransactionImpl(BuilderImpl builder){
		
		this.timestamp = builder.timestamp;
        this.deadline = builder.deadline;
        this.senderPublicKey = builder.senderPublicKey;
        this.recipientId = builder.recipientId;
        this.amountNQT = builder.amountNQT;
        this.referencedTransactionFullHash = builder.referencedTransactionFullHash;
        this.signature = builder.signature;
        this.type = builder.type;
        this.version = builder.version;
        this.blockId = builder.blockId;
        this.height = builder.height;
        this.id = builder.id;
        this.senderId = builder.senderId;
        this.blockTimestamp = builder.blockTimestamp;
        this.fullHash = builder.fullHash;
		this.ecBlockHeight = builder.ecBlockHeight;
        this.ecBlockId = builder.ecBlockId;
        int effectiveHeight = (height < Integer.MAX_VALUE ? height : Nxt.getBlockchain().getHeight());
        long minimumFeeNQT = type.minimumFeeNQT(effectiveHeight);
        feeNQT = minimumFeeNQT;
        
	}
	
	public static final class BuilderImpl implements Builder {

        private final short deadline;
        private final byte[] senderPublicKey;
        private final long amountNQT;
        private final long feeNQT;
        private final TransactionType type;
        private final byte version;
        private final int timestamp;
        private long recipientId;
        private String referencedTransactionFullHash;
        private byte[] signature;
        private long blockId;
        private int height = Integer.MAX_VALUE;
        private long id;
        private long senderId;
        private int blockTimestamp = -1;
        private String fullHash;
        private int ecBlockHeight;
        private long ecBlockId;
        
        public BuilderImpl(byte version, byte[] senderPublicKey, long amountNQT, long feeNQT, int timestamp, short deadline,
                    TransactionType type) {
            this.version = version;
            this.timestamp = timestamp;
            this.deadline = deadline;
            this.senderPublicKey = senderPublicKey;
            this.amountNQT = amountNQT;
            this.feeNQT = feeNQT;
            this.type = type;
        }
        
        @Override
        public TransactionImpl build() throws Exception {
            return new TransactionImpl(this);
        }
        
        @Override
        public BuilderImpl recipientId(long recipientId) {
            this.recipientId = recipientId;
            return this;
        }
        
        @Override
        public BuilderImpl referencedTransactionFullHash(String referencedTransactionFullHash) {
            this.referencedTransactionFullHash = referencedTransactionFullHash;
            return this;
        }
        
        public BuilderImpl referencedTransactionFullHash(byte[] referencedTransactionFullHash) {
            if (referencedTransactionFullHash != null) {
                this.referencedTransactionFullHash = Convert.toHexString(referencedTransactionFullHash);
            }
            return this;
        }
        
        public BuilderImpl id(long id) {
            this.id = id;
            return this;
        }
        
        public BuilderImpl signature(byte[] signature) {
            this.signature = signature;
            return this;
        }
        
        public BuilderImpl blockId(long blockId) {
            this.blockId = blockId;
            return this;
        }
        
        public BuilderImpl height(int height) {
            this.height = height;
            return this;
        }
        
        public BuilderImpl senderId(long senderId) {
            this.senderId = senderId;
            return this;
        }
        
        public BuilderImpl fullHash(String fullHash) {
            this.fullHash = fullHash;
            return this;
        }
        
        /**
         * 
         * transaction fullhash calculation using hex
         * @param fullHash
         * @return
         */
        public BuilderImpl fullHash(byte[] fullHash) {
            if (fullHash != null) {
                this.fullHash = Convert.toHexString(fullHash);
            }
            return this;
        }
        
        public BuilderImpl blockTimestamp(int blockTimestamp) {
            this.blockTimestamp = blockTimestamp;
            return this;
        }
        
        public BuilderImpl ecBlockHeight(int height) {
            this.ecBlockHeight = height;
            return this;
        }
        
        public BuilderImpl ecBlockId(long blockId) {
            this.ecBlockId = blockId;
            return this;
        }
        
    }
	
	private short deadline;
    private byte[] senderPublicKey;
    private long recipientId;
    private long amountNQT;
    private long feeNQT;
    private String referencedTransactionFullHash;
    private final TransactionType type;
    private int ecBlockHeight;
    private long ecBlockId;
    private byte version;
    private int timestamp;
    private int appendagesSize;

    private volatile int height = Integer.MAX_VALUE;
    private volatile long blockId;
    private volatile Block block;
    private volatile byte[] signature;
    private volatile int blockTimestamp = -1;
    private volatile long id;
    private volatile String stringId;
    private volatile long senderId;
    private volatile String fullHash;
    private volatile DbKey dbKey;
	
	
	@Override
	public long getId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getStringId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getSenderId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public byte[] getSenderPublicKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getRecipientId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getBlockId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Block getBlock() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getTimestamp() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getBlockTimestamp() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public short getDeadline() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getExpiration() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getAmountNQT() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getFeeNQT() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getReferencedTransactionFullHash() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] getSignature() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFullHash() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sign(String secretPhrase) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean verifyPublicKey() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean verifySignature() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void validate() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public byte[] getBytes() {
		ByteBuffer buf = ByteBuffer.allocate(getSize());
		buf.order(ByteOrder.LITTLE_ENDIAN);
		// 1 + 1 + 4 + 2 + 32 + 8 + (useNQT() ? 8 + 8 + 32 : 4 + 4 + 8) + 64  
		// 1
		buf.put(this.type.getType());
		// 1
		buf.put((byte)(this.version << 4 | this.type.getSubType()));
		// 4
		buf.putInt(this.timestamp);
		// 2 
		buf.putShort(this.deadline);
		// 32
		if(this.height < Constants.AT_FIX_BLOCK_4){
			buf.put(this.senderPublicKey);
		}else{
			buf.putLong(this.senderId);
			buf.put(new byte[24]);
		}
		// 8
		buf.putLong(this.type.hasRecipient() ? this.recipientId:Genesis.CREATOR_ID);
		
		if(useNQT()){
			// 8 + 8 + 32 
			buf.putLong(this.amountNQT);
			buf.putLong(this.feeNQT);
			if(this.referencedTransactionFullHash != null){
				buf.put(Convert.parseHexString(referencedTransactionFullHash));
			}else{
				buf.put(new byte[32]);
			}
		}else{
			// 4 + 4 + 8
			buf.putInt((int)(this.amountNQT/Constants.ONE_NXT));
			buf.putInt((int)(this.feeNQT/Constants.ONE_NXT));
			if(this.referencedTransactionFullHash != null){
				buf.putLong(Convert.fullHashToId(Convert.parseHexString(referencedTransactionFullHash)));
			}else{
				buf.putLong(0l);
			}
		}
		// 64
		buf.put(this.signature != null ? this.signature : new byte[64]);
		
		// losing ecBlockHeight and ecBlockId for now .
		return buf.array();
	}
	
	private int getSize(){
		return signatureOffset() + 64  + (version > 0 ? 4 + 4 + 8 : 0);
	}
	
	private int signatureOffset() {
	    return 1 + 1 + 4 + 2 + 32 + 8 + (useNQT() ? 8 + 8 + 32 : 4 + 4 + 8);
    }

    private boolean useNQT() {
        return this.height > Constants.NQT_BLOCK
                && (this.height < Integer.MAX_VALUE
                || Nxt.getBlockchain().getHeight() >= Constants.NQT_BLOCK);
    }
    
	@Override
	public byte[] getUnsignedBytes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONObject getJSONObject() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte getVersion() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getECBlockHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getECBlockId() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/**
	 *  this 4 properties comes from block.
	 * @param block
	 */
	public void setBlock(Block block){
		this.block = block;
		this.blockId = block.getId();
		this.timestamp = block.getTimestamp();
		this.height = block.getHeight();
	}
	
	@Override
	public int compareTo(Transaction o) {
		return 0;
	}
}