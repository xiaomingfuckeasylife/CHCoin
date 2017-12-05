package nxt.util;

/**
 * 
 * @author clark
 * 
 * 2017年12月5日 下午4:56:53
 * 
 * Logger entity
 */
public final class Logger {
	
	public static enum Event {
		MESSAGE , EXCEPPTION
	}
	
	public static enum Lever {
		DEBUG , INFO , WARN , ERROR 
	}
	
	// listening to message
	private static final Listeners<String,Event> messageListeners = new Listeners<>();
	
	// listening to exception
	private static final Listeners<Exception,Event> exceptionListeners = new Listeners<>();
	
	// slf4j
	private static final org.slf4j.Logger log;
	
	private static final boolean enableStackTraces;
	
	private static final boolean enableLogTraceback;
	
	
	
}
