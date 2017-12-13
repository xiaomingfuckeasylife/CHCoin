package nxt;

import java.util.Calendar;

/**
 * 
 * @author clark
 * 
 * 2017年12月5日 上午11:27:30
 * 
 * stole the necessary constants
 * 
 */
public final class Constants {
	
	public static final int NQT_BLOCK = 0;
	
	public static final int BURST_DIFF_ADJUST_CHANGE_BLOCK = 2700;
	
	// genesis initial base target 
	public static final long INITIAL_BASE_TARGET = 18325193789L;
	
	// genesis initial base target 
	public static final long MAX_BASE_TARGET = 18325193789L;
	
	
    public static final long MAX_BALANCE_NXT = 2158812800L;
    
    // 1 nxt is with 8 digital which means 1 nxt can be divided into 10^8 like bitcoin  
    public static final long ONE_NXT = 100000000;
    
    public static final long MAX_BALANCE_NQT = MAX_BALANCE_NXT * ONE_NXT;
    
	// max number of transactions a block can have .
	public static final int MAX_NUM_OF_TRANSACTIONS = 255;
	// when the project start off 
	public static final long EPOCH_BEGINNING ;
	/**
	 * initial EPOCH_BEGINNING time ;
	 */
	static {
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 2017);
		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		calendar.set(Calendar.DAY_OF_MONTH, 5);
		calendar.set(Calendar.HOUR_OF_DAY, 9);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		EPOCH_BEGINNING = calendar.getTimeInMillis();
		
		
	}
	
	public static final int MAX_ROLLBACK = Nxt.getIntProperty("nxt.maxRollback");
	
	static{
		if(MAX_ROLLBACK < 1440){
			throw new RuntimeException("nxt.maxRollback must be at least 1440");
		}
	}

	
	public static final int AT_FIX_BLOCK_2 = 67000;
	public static final int AT_FIX_BLOCK_3 = 92000;
    public static final int AT_FIX_BLOCK_4 = 255000;
}
