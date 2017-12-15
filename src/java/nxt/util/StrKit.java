package nxt.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 
 * @author clark
 * 
 * 2017年12月8日 下午4:53:00
 * 
 */
public final class StrKit {
	
	public static boolean isBlank(String str){
		if(str == null || str.length() ==0 || str.trim().length() == 0)
			return true;
		return false;
	}
	
	public static String rmvPrefixBlank(String str){
		char cArr[] = str.toCharArray();
		int index = 0;
		for(int i=0;i<cArr.length;i++){
			if(cArr[i] != ' '){
				index = i;
				break;
			}
		}
		return str.substring(index);
	}
	
	public static void main(String[] args) {
		
		 try {
			InetAddress inetAddress = InetAddress.getByName("localhost");
			System.out.println(inetAddress.isAnyLocalAddress()+" " + inetAddress.isLinkLocalAddress()+ " " +inetAddress.isLoopbackAddress());
			System.out.println("");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
}
