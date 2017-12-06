package nxt.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.logging.LogManager;

import nxt.Nxt;

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
	
	static{
		
		String oldManager = System.getProperty("java.util.logging.manager");
		
		System.setProperty("java.util.logging.manager", "nxt.util.NxtLogManager");
		
		if(!(LogManager.getLogManager() instanceof NxtLogManager)){
			System.setProperty("java.util.logging.manager", (oldManager != null) ? oldManager : "java.util.logging.LogManager");
		};
		
		if(!Boolean.getBoolean("Nxt.doNotConfigureLogging")){
			try{
				
				Properties logProp = new Properties();
				boolean foundProperties = false;
				try(InputStream is = Logger.class.getClassLoader().getSystemResourceAsStream("logging-default.properties")){
					if(is != null){
						foundProperties = true;
						logProp.load(is);
					}
				}
				
				try(InputStream is = Logger.class.getClassLoader().getSystemResourceAsStream("logging.properties")){
					if(is != null){
						foundProperties = false;
						logProp.load(is);
					}
				}
				
				if(foundProperties){
					
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					logProp.store(out, "log propertis");
					ByteArrayInputStream input = new ByteArrayInputStream(out.toByteArray());
					LogManager.getLogManager().readConfiguration(input);
					out.close();
					input.close();
				}
				// add log format 
				BriefLogFormat.init();
			}catch(Exception ex){
				throw new RuntimeException("Error loading log properties");
			}
		}
		// log instance is nxt.Nxt 
		log = org.slf4j.LoggerFactory.getLogger(nxt.Nxt.class);
		enableLogTraceback = Nxt.getBooleanProperties("nxt.enableLogTraceback");
		enableStackTraces = Nxt.getBooleanProperties("nxt.enableStackTraces");
		logInfoMessage("logging enabled");
	}
	
	public static void logInfoMessage(String msg){
		doLog(Lever.INFO,msg,null);
	}
	
	public static void logMessage(String msg){
		doLog(Lever.INFO,msg,null);
	}
	
	private static void doLog(Lever lever , String msg , Exception ex){
		
		if(enableLogTraceback){
			
			StackTraceElement ele = Thread.currentThread().getStackTrace()[3];
			if(ele != null){
				String clz = ele.getClassName();
				int ind = clz.lastIndexOf(".");
				if(ind != -1){
					clz = clz.substring(ind+1);
				}
				String methodName = ele.getMethodName();
				msg = clz + "." + methodName +":"+msg;
			}
			
		}
		
		if(enableStackTraces){
			if(ex != null){
				msg = msg +"\n" + ex.toString();
			}
		}
		
		switch (lever) {
		case INFO:
			log.info(msg,ex);
			break;
		case DEBUG:
			log.debug(msg,ex);
			break;
		case WARN :
			log.warn(msg,ex);
			break;
		case ERROR:
			log.error(msg,ex);
			break;
		default:
			break;
		}
		
		// notify listener
		if(ex != null)
			exceptionListeners.notify(ex, Logger.Event.EXCEPPTION);
		else
			messageListeners.notify(msg, Logger.Event.MESSAGE);
		
	}
	
	public static void init(){}
}
