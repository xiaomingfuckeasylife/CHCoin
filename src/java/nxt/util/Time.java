package nxt.util;

import nxt.Constants;

/**
 * 
 * @author clark
 * 
 * 2017年12月5日 上午11:17:04
 * 
 * time that the block chain concerned about . 
 * 
 */
public interface Time {
	
	/**
	 * the time has only one behavior 
	 * @return
	 */
	int getTime();
	
	/**
	 * EpochTime Entity to fetch EpochTime .
	 * @author clark
	 * 2017年12月5日 下午2:04:25
	 */
	public static class EpochTime implements Time{
		
		/**
		 * get the seconds between current time and epoch_beginning time  
		 */
		@Override
		public int getTime() {
			// because the GeneratorBlock is 500 millisecond delay so this place is add 500 millisecond
			return (int)(System.currentTimeMillis() - Constants.EPOCH_BEGINNING + 500)/1000;
		}
		
	}
	
	
}
