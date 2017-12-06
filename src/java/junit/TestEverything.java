package junit;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import nxt.Constants;
import nxt.Nxt;
import nxt.util.Time;

/**
 * 
 * @author clark
 * 
 * 2017年12月5日 上午11:20:22
 */
public class TestEverything{
	
	@org.junit.Test
	public void TestA(){
		
		
//		System.out.println(Constants.EPOCH_BEGINNING);
//		System.out.println(new Time.EpochTime().getTime());
//		System.out.println(ClassLoader.getSystemResourceAsStream("sasdf"));
//		System.out.println(new Nxt());
//		TimeUnit unit = TimeUnit.DAYS;
//		System.out.println(unit.toMillis(10));
//		System.out.println(Arrays.toString(Thread.currentThread().getStackTrace()));
		
		System.out.println(Math.max(16,Runtime.getRuntime().maxMemory()/(1024 * 1024-128)/2));
	}
}
