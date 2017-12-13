package nxt;


import org.json.simple.JSONObject;

public interface Transaction extends Comparable<Transaction>{
		
	 	public static interface Builder {
	 		
	        Builder recipientId(long recipientId);
	        
	        Builder referencedTransactionFullHash(String referencedTransactionFullHash);
	        
	        Transaction build() throws Exception;
	        
	    }
	 	
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

	    void sign(String secretPhrase);

	    boolean verifyPublicKey();

	    boolean verifySignature();

	    void validate() throws Exception;

	    byte[] getBytes();

	    byte[] getUnsignedBytes();

	    JSONObject getJSONObject();

	    byte getVersion();
	    
	    int getECBlockHeight();
	    
	    long getECBlockId();
}
