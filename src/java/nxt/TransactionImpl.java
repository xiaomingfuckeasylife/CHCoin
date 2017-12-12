package nxt;

import java.util.List;

import org.json.simple.JSONObject;

import nxt.db.DbKey;

public class TransactionImpl implements Transaction{
	
	
	private short deadline;
    private byte[] senderPublicKey;
    private long recipientId;
    private long amountNQT;
    private long feeNQT;
    private String referencedTransactionFullHash;
//    private final TransactionType type;
    private int ecBlockHeight;
    private long ecBlockId;
    private byte version;
    private int timestamp;
//    private final Attachment.AbstractAttachment attachment;
//    private final Appendix.Message message;
//    private final Appendix.EncryptedMessage encryptedMessage;
//    private final Appendix.EncryptToSelfMessage encryptToSelfMessage;
//    private final Appendix.PublicKeyAnnouncement publicKeyAnnouncement;

//    private final List<? extends Appendix.AbstractAppendix> appendages;
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
		// TODO Auto-generated method stub
		return null;
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
	
}
