package nxt;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.sql.ConnectionPoolDataSource;

import nxt.db.Db;
import nxt.db.DbVersion;
import nxt.util.Logger;
import nxt.util.StrKit;
import nxt.util.Time;

/**
 * 
 * @author clark
 * 
 * 2017年12月5日 上午11:07:24
 * 
 * 从这一刻开始,正式步入区块链行业,开始开发我的第一个币。
 * 
 */
public class Nxt {
	
	public static final String VERSION = "0.0.1";
	
	public static final String APPLICATION = "CHC";
	
	// 距离创世时间多少秒  volatile 使得变量不会存储缓存中
	public static volatile Time time = new Time.EpochTime();
	
	public static final Properties defaultProperties = new Properties();
	
	/**
	 * load properties files 
	 */
	static {
		
		System.out.println("Initializing CHCoin server version " + Nxt.VERSION);
		
		try(InputStream is = ClassLoader.getSystemResourceAsStream("chc-default.properties")){
			if(is != null){
				defaultProperties.load(is);
			}else{
				String filePath = System.getProperty("chc-default.properties");
				if(filePath != null){
					try(InputStream iss = new FileInputStream(new File(filePath))){
						defaultProperties.load(iss);
					}catch(IOException ex){
						throw new RuntimeException("error loading chc-default.properties from " + filePath);
					}
				}else{
					throw new RuntimeException("chc-default.properties is not in classpath or system property chc-default.properties not defined either ");
				}
			}
		}catch(IOException ex){
			throw new RuntimeException("Error loading chc-default.properties",ex);
		}
		
	}
	
	// using chc.properties file to override chc-default.properties
	private static final Properties properties = new Properties(defaultProperties);
	
	static{
		try(InputStream is = ClassLoader.getSystemResourceAsStream("ch.properties")){
			if(is != null){
				defaultProperties.load(is);
			}else{
				String filePath = System.getProperty("chc-default.properties");
				if(filePath != null){
					try(InputStream iss = new FileInputStream(new File(filePath))){
						defaultProperties.load(iss);
					}catch(IOException ex){
						// ignore  
					}
				}
			}
		}catch(IOException ex){
			// ignore 
		}
	}
	
	/**
	 * program entry
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		init();
	}
	
	public static String getStringProperty(String name){
		return properties.getProperty(name,"");
	}
	
	public static String getStringProperty(String name , String defaultValue){
		String value = properties.getProperty(name);
		if(value == null){
			return defaultValue;
		}
		return value;
	}
	
	/**
	 * get property value and transform them to int value  
	 * @param name
	 * @return
	 */
	public static int getIntProperty(String name){
		return getIntProperty(name, 0);
	}
	
	/**
	 * get property value and has a default value 
	 * @param name
	 * @param defaultVal
	 * @return
	 */
	public static int getIntProperty(String name , int defaultVal){
		String value =  properties.getProperty(name);
		try{
			return Integer.valueOf(value);
		}catch(Exception ex){
			return defaultVal;
		}
	}
	
	private static Generator generator = new GeneratorImpl();
	
	public static boolean getBooleanProperties(String name){
		return getBooleanProperties(name, false);
	}
	
	public static boolean getBooleanProperties(String name , boolean defaultValue){
		
		String value = properties.getProperty(name);
		
		if(value == null ){
			return defaultValue;
		}
		
		if(value.toUpperCase().equals("TRUE")){
			return true;
		}
		
		if(value.toUpperCase().equals("FALSE")){
			return true;
		}
		
		Logger.logMessage(name+" not defined , assuming false");
		
		return defaultValue;
	}
	
	public static List<String> getStringListProperty(String  strList){
		String valList = getStringProperty(strList);
		if(StrKit.isBlank(valList)){
			return Collections.emptyList();
		}
		List<String> retList = new ArrayList<String>();
		String[] arrStr =  strList.split(";");
		for(String s : arrStr){
			if(StrKit.isBlank(s)){
				continue;
			}
			retList.add(s);
		}
		return retList;
	}
	
	
	private static void init(){
		Init.init();
	}
	
	/**
	 * init all module.
	 * @author clark
	 * 2017年12月6日 下午3:51:18
	 */
	public static class Init{
		// initial using the static 
		static{
			long startTime = System.currentTimeMillis();
			Logger.init();
			Db.init();
			DbVersion.init();
			TransactionProcessorImpl.getInstance();
			
		}
		public static void init(){}
		private Init(){}
	}
	
	
	public static BlockchainProcessorImpl getBlockchainProcessor(){
		
		return BlockchainProcessorImpl.getInstance();
	}
	
	public static BlockchainImpl getBlockchain(){
		return BlockchainImpl.getInstance();
	}
	
	public static int getEpochTime(){
		return time.getTime();
	}
}
