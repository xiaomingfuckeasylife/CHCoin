package nxt.crypto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 
 * @author clark
 * 
 * 2017年12月12日 上午11:12:00
 * 
 */
public final class Crypto {
	
	/**
	 * get sha 256 algorithm digest
	 * @return
	 */
	public static MessageDigest sha256(){
		return getMessageDigist("SHA-256");
	}
	
	private static MessageDigest getMessageDigist(String argrithm){
		try {
			return MessageDigest.getInstance(argrithm);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e.getMessage(),e);
		}
	}
	
}
