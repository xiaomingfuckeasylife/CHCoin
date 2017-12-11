package nxt;

/**
 * 
 * @author clark
 * 
 * 2017年12月11日 上午10:37:47
 * 
 */
public class TransactionProcessorImpl implements TransactionProcessor {
	/**
	 * rebroad casting transaction
	 */
	private static final boolean enableTransactionRebroadcasting = Nxt.getBooleanProperties("nxt.enableTransactionRebroadcasting");
	/**
	 * test uncomfiredTransaction
	 */
	private static final boolean testUncomfirmedTransaction = Nxt.getBooleanProperties("nxt.testUncomfirmedTransaction");
	/**
	 * after x blocks starting to rebroad cast 
	 */
	private static final int rebroadCastAfter = Nxt.getIntProperty("nxt.rebroadCastAfter");
	/**
	 * rebroad cast .then every x blocks rebroad cast once until they are comfirmed .
	 */
	private static final int rebroadCastEvery = Nxt.getIntProperty("nxt.rebroadCastEvery");
	/**
	 * max uncomfired transaction allowed in the transaction pool.
	 */
	private static final int maxUncomfired = Nxt.getIntProperty("nxt.maxUncomfired");
	
	private static final TransactionProcessorImpl me = new TransactionProcessorImpl();
	
	private TransactionProcessorImpl(){}
	
	public static TransactionProcessorImpl getInstance(){
		return me;
	}
}
