package nxt.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;
/**
 * 
 * @author clark
 * 
 * 2017年12月6日 上午10:05:42
 * 
 * NxtLogManager 
 * 
 */
public class NxtLogManager extends LogManager {
	
	/**
	 * is currently reconfiguring the log
	 */
	private volatile boolean loggingReconfiguration = false;
	

	public NxtLogManager() {
		super();
	}
	
	/**
	 * add indicator to check if currently there are configuring logging files
	 * if so we can interrupt it by calling reset method
	 */
	@Override
	public void readConfiguration(InputStream ins) throws IOException, SecurityException {
		loggingReconfiguration = true;
		super.readConfiguration(ins);
		loggingReconfiguration = false;
	}
	
	
	/**
	 * only if currently is reconfiguration . if not ignore reset 
	 */
	@Override
	public void reset() throws SecurityException {
		if(loggingReconfiguration){
			super.reset();
		}
	}
	
	/**
	 * shut the logger once for all .
	 */
	void nxtShutDown(){
		super.reset();
	}
}
