package nxt.db;

import nxt.Constants;

/**
 * 
 * @author clark
 * 
 * 2017年12月13日 上午10:32:32
 * 
 * only accept two transaction type . payment and mining reward
 */
public abstract class TransactionType {
	
	// only include two transactionType 
	private static final byte TYPE_PAYMENT = 0;
    private static final byte TYPE_BURST_MINING = 20; // jump some for easier nxt updating
    private static final byte SUBTYPE_PAYMENT_ORDINARY_PAYMENT = 0;
    private static final byte SUBTYPE_BURST_MINING_REWARD_RECIPIENT_ASSIGNMENT = 0;
    
    private static final int BASELINE_FEE_HEIGHT = 1; // At release time must be less than current block - 1440
    private static final long BASELINE_FEE = Constants.ONE_NXT;
    private static final int NEXT_FEE_HEIGHT = Integer.MAX_VALUE;
    private static final long NEXT_FEE = Constants.ONE_NXT;
    
    public abstract byte getType();
    public abstract byte getSubType();
    public abstract boolean hasRecipient();
    
    public static TransactionType findTransactionType(byte type, byte subtype) {
        switch (type) {
            case TYPE_PAYMENT:
                switch (subtype) {
                    case SUBTYPE_PAYMENT_ORDINARY_PAYMENT:
                        return Payment.ORDINARY;
                    default:
                        return null;
                }
            case TYPE_BURST_MINING:
            	switch (subtype) {
            		case SUBTYPE_BURST_MINING_REWARD_RECIPIENT_ASSIGNMENT:
            			return BurstMining.REWARD_RECIPIENT_ASSIGNMENT;
            		default:
            			return null;
            	}
            default:
                return null;
        }
    }
    
    static class Payment extends TransactionType{
    	
    	private static Payment ORDINARY = new Payment();
    	
		@Override
		public byte getSubType() {
			return TransactionType.SUBTYPE_PAYMENT_ORDINARY_PAYMENT;
		}
		
		@Override
		public byte getType() {
			return TransactionType.TYPE_PAYMENT;
		}

		@Override
		public boolean hasRecipient() {
			return true;
		}
		
    }
    
    static class BurstMining extends TransactionType{
    	
    	private static BurstMining REWARD_RECIPIENT_ASSIGNMENT = new BurstMining();
    	
		@Override
		public byte getSubType() {
			return TransactionType.SUBTYPE_BURST_MINING_REWARD_RECIPIENT_ASSIGNMENT;
		}
		
		@Override
		public byte getType() {
			return TransactionType.SUBTYPE_PAYMENT_ORDINARY_PAYMENT;
		}
		
		final public boolean hasRecipient() {
			return true;
		}
    }
    
    /**
     * calculate the Fee NQT : make it easier . we are not calculate the Fee by appendix size .
     * @param height
     * @param appendageSize
     */
    public long minimumFeeNQT(int height){
    	if(height < BASELINE_FEE_HEIGHT){
    		return 0;
    	}
    	return NEXT_FEE;
    }
    
}
