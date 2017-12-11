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
	
}
