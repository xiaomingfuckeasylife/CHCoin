package junit;

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
		System.out.println(ClassLoader.getSystemResourceAsStream("sasdf"));
//		System.out.println(Nxt.defaultProperties);
		
	}
}
