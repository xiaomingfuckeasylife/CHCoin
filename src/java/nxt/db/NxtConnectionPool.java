package nxt.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import nxt.Nxt;

/**
 * 
 * @author clark
 * 
 * 2017年12月7日 上午10:33:32
 * 
 * Connection Pool used to fetch connection .
 */
public class NxtConnectionPool implements Runnable{
	
	/**
	 * max active connection.
	 */
	private static volatile int maxActiveConnections;
	private static volatile int initialConnecion ;
	private static volatile String url;
	private static volatile String username;
	private static volatile String password;
	private static List<Connection> availableConn = new ArrayList<>();
	private static List<Connection>	busyConn = new ArrayList<>();
	/**
	 * 
	 */
	private static volatile boolean devSql;
	
	static{
		url = Nxt.getStringProperty("nxt.dbUrl");
		username = Nxt.getStringProperty("nxt.jdbc.username","root");
		password = Nxt.getStringProperty("nxt.jdbc.password","123456");
		maxActiveConnections = Nxt.getIntProperty("nxt.maxDbConnections",10);
		initialConnecion = Nxt.getIntProperty("nxt.initialDbConnections",3);
		devSql = Nxt.getBooleanProperties("nxt.devSql",true);
		for(int i=0;i<initialConnecion;i++){
			try {
				Connection conn = DriverManager.getConnection(url, username, password);
				availableConn.add(devSql ? new SqlReporter(conn).getConnection():conn);
			} catch (SQLException e) {
				throw new RuntimeException("Create Connection error : " + e.getMessage() );
			}
		}
	}
	
	
	public synchronized Connection getConnection(){
		if(availableConn.size() != 0){
			Connection conn = availableConn.get(0);
			busyConn.add(conn);
			availableConn.remove(0);
			return conn;
		}
		if((busyConn.size() + availableConn.size()) < maxActiveConnections){
			makeNewConn();
		}else{
			throw new RuntimeException(" max connection reached.");
		}
		
		try {
			wait();
		} catch (InterruptedException e) {
			//ignore
		}
		return getConnection();
	}
	
	
	private void makeNewConn(){
		Thread thread = new Thread(this);
		thread.start();
	}
	
	private NxtConnectionPool(){}

	@Override
	public void run() {
		try {
			Connection conn = DriverManager.getConnection(url, username, password);
			availableConn.add(devSql ? new SqlReporter(conn).getConnection():conn);
			notifyAll();
		} catch (SQLException e) {
			throw new RuntimeException("Create Connection error : " + e.getMessage() );
		}
		
	}
	
	public static NxtConnectionPool me = new NxtConnectionPool(); 

}
