package nxt;


import org.json.simple.JSONObject;

public interface Transaction {
		
		long getId();

	    String getStringId();

	    long getSenderId();

	    byte[] getSenderPublicKey();

	    long getRecipientId();

	    int getHeight();

	    long getBlockId();

	    Block getBlock();

	    int getTimestamp();

	    int getBlockTimestamp();

	    short getDeadline();

	    int getExpiration();

	    long getAmountNQT();

	    long getFeeNQT();

	    String getReferencedTransactionFullHash();

	    byte[] getSignature();

	    String getFullHash();

//	    TransactionType getType();
//
//	    Attachment getAttachment();

	    void sign(String secretPhrase);

	    boolean verifyPublicKey();

	    boolean verifySignature();

	    void validate() throws Exception;

	    byte[] getBytes();

	    byte[] getUnsignedBytes();

	    JSONObject getJSONObject();

	    byte getVersion();

//	    Appendix.Message getMessage();
//
//	    Appendix.EncryptedMessage getEncryptedMessage();
//
//	    Appendix.EncryptToSelfMessage getEncryptToSelfMessage();
//
//	    List<? extends Appendix> getAppendages();

	    /*
	    Collection<TransactionType> getPhasingTransactionTypes();

	    Collection<TransactionType> getPhasedTransactionTypes();
	    */

	    int getECBlockHeight();

	    long getECBlockId();
}
