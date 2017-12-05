package nxt;

/**
 * 
 * @author clark
 * 
 * 2017年12月5日 下午3:07:36
 * 
 * generator to generator public key and stuff 
 */
public interface Generator {
	
	/**
	 * 
	 * @author clark
	 * 
	 * 2017年12月5日 下午3:09:26
	 * 
	 * generator event 
	 * 
	 */
	public static enum Event{
		
		GENERATOR_DEADLINE,START_FORGING,STOP_FORGING;
		
	}
	
	/**
	 * @author clark
	 * 
	 * 2017年12月5日 下午3:11:52
	 * 
	 * generator state variables . like the generated public key . the accountId and so on . 
	 */
	interface GeneratorState{
		
		
		
	}
}
