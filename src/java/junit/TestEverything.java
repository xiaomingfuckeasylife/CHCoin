package junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.bouncycastle.util.Strings;

import nxt.Constants;
import nxt.GeneratorImpl;
import nxt.Nxt;
import nxt.db.Db;
import nxt.db.NxtConnectionPool;
import nxt.util.StrKit;
import nxt.util.Time;

/**
 * 
 * @author clark
 * 
 * 2017年12月5日 上午11:20:22
 */
public class TestEverything implements Serializable{
	
	@org.junit.Test
	public void TestA() throws IOException{
		
		
//		System.out.println(Constants.EPOCH_BEGINNING);
//		System.out.println(new Time.EpochTime().getTime());
//		System.out.println(ClassLoader.getSystemResourceAsStream("sasdf"));
//		TimeUnit unit = TimeUnit.DAYS;
//		System.out.println(unit.toMillis(10));
//		System.out.println(Arrays.toString(Thread.currentThread().getStackTrace()));
//		System.out.println(Math.max(16,Runtime.getRuntime().maxMemory()/(1024 * 1024-128)/2));
		
//		switch (-1) {
//		case 1:
//			System.out.println("1");
//		case 2:
//			System.out.println("2");
//		default:
//			break;
//		}
//		System.out.println("111");
		
//		A obj = new A();
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		ObjectOutputStream os = new ObjectOutputStream(baos);
//		os.writeObject(obj);
//		System.out.println(baos.toByteArray());
		
//		try {
//			Connection conn = Db.beginTransaction();
//			try{
//				Map<String,Object> map =  Db.executeQuery(conn, "select next_update from version");
//				System.out.println(map);
//				Db.executeUpdate(conn, "create table version(id int auto_increment primary key , next_update int not null)");
//			}catch(Exception ex){
//				ex.printStackTrace();
//				Db.rollbackTransaction();
//			}
//			System.out.println(Db.executeQuery(conn, "select * from version"));
//			Db.endTransaction();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
		
//		String str = "  -- `index` int not null COMMENT \"索引？\",";
//		System.out.println(StrKit.rmvPrefixBlank(str));
//		System.out.println(StrKit.rmvPrefixBlank(str).startsWith("--"));
//		
//		System.out.println((char)' ');
		
//		GeneratorImpl impl = new GeneratorImpl();
//		System.out.println(Arrays.toString("829BD824B016326A401d083B33D09229".getBytes()));
//		System.out.println(impl.calculateScoop("829BD824B016326A401d083B33D09229".getBytes(), 1));
		
//		BigInteger hit = new BigInteger("1213");
//		System.out.println(hit.divide(BigInteger.valueOf(12131213)).intValue());
		
		try (Connection conn = Db.beginTransaction();){
			System.out.println(Db.executeQuery(conn, "select 1 from dual"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			Connection conn = Db.getConnection();
			System.out.println(Db.executeQuery(conn, "select 2 from dual"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		SecureRandom sr = new  SecureRandom();
	}
	
	class A implements Serializable{
		private int id;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}
		
	}
}
