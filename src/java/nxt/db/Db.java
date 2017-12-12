package nxt.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author clark
 * 
 * 2017年12月6日 下午4:01:33
 * 
 */
public final class Db {
	
	private static final NxtConnectionPool cp = NxtConnectionPool.me;
	
	private static final ThreadLocal<DbConnection> localConnection = new ThreadLocal<>();
	
	private static final ThreadLocal<Map<String,Map<DbKey,Object>>> transactionCaches = new ThreadLocal<>();
	
	private static final ThreadLocal<Map<String,Map<DbKey,Object>>> transactionBatches  = new  ThreadLocal<>();
	
	/**
	 * just a gesture
	 */
	public static void init(){}
	
	/**
	 * 
	 * @author clark
	 * 
	 * 2017年12月6日 下午5:08:33
	 * 
	 */
	private static final class DbConnection {
		
		private Connection conn;
		
		private DbConnection(Connection conn){
			this.conn = conn;
		}
		
		/**
		 * not support for safety reason . 
		 */
		public void setAutoCommit(){
			throw new UnsupportedOperationException("not support auto commit ");
		}
		
		/**
		 * this is use to commit for connection that is not stored by ThreadLocal
		 * @throws SQLException
		 */
		public void commit() throws SQLException{
			DbConnection conn = localConnection.get();
			if(conn == null){
				this.conn.commit();
			}else if(this != conn ){
				throw new IllegalStateException("previous connection is not commited yet ");
			}else{
				throw new UnsupportedOperationException("use Db.commitTransaction() to commit the transaction");
			}
		}
		
		/**
		 * for Db
		 * @throws SQLException
		 */
		private void doCommit() throws SQLException {
			this.conn.commit();
		}
		
		/**
		 * this is use to rollback for connection that is not stored by ThreadLocal
		 */
		public void rollback(){
			DbConnection conn = localConnection.get();
			if(conn == null){
				conn.rollback();
			}else if (this != conn){
				throw new IllegalStateException("previous transaction is not committed yet");
			}else{
				throw new UnsupportedOperationException("use Db.endTransaction() to rollback");
			}
		}
		
		/**
		 * for Db
		 * @throws SQLException
		 */
		private void doRollback() throws SQLException{
			this.conn.rollback();
		}
		
		/**
		 * for Db 
		 * @throws SQLException
		 */
		public void close() throws SQLException{
			DbConnection conn = localConnection.get();
			if(conn == null){
				this.conn.close();
			}else if(this != conn){
				throw new IllegalStateException("previous transaction is not committed yet");
			}else{
				throw new UnsupportedOperationException("use Db.endTransaction() to close connection");
			}
		}
	}
	
	public static Connection beginTransaction() throws SQLException{
		
		DbConnection conn= localConnection.get();
		if(conn == null){
			Connection co = cp.getConnection();
			co.setAutoCommit(false);
			conn = new DbConnection(co);
			localConnection.set(conn);
			// initial transactionCaches
			transactionCaches.set(new HashMap<String,Map<DbKey,Object>>());
			// initial transactionBatches
			transactionBatches.set(new HashMap<String,Map<DbKey,Object>>());
			return co;
		}
		
		throw new IllegalStateException("transaction already in progress");
	}
	
	public static Connection getConnection() throws SQLException{
		DbConnection connDb = localConnection.get();
		if(connDb == null)
		{
			Connection conn = cp.getConnection();
			conn.setAutoCommit(true);
			return conn;
		}
		return connDb.conn;
	}
	
	public static void endTransaction() {
		DbConnection conn= localConnection.get();
		if(conn == null){
			throw new IllegalStateException("conn is not in process.");
		}
		localConnection.set(null);
		transactionBatches.get().clear();
		transactionBatches.set(null);
		transactionCaches.get().clear();
		transactionCaches.set(null);
		try {
			conn.close();
		} catch (SQLException e) {
			throw new RuntimeException(e.getMessage(),e);
		}
	}
	
	public static void commitTransaction(){
		DbConnection conn= localConnection.get();
		if(conn == null){
			throw new IllegalStateException("conn is not in process.");
		}
		try {
			conn.doCommit();
		} catch (SQLException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	
	public static void rollbackTransaction(){
		DbConnection conn= localConnection.get();
		if(conn == null){
			throw new IllegalStateException("conn is not in process.");
		}
		try {
			conn.doRollback();
		} catch (SQLException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		transactionCaches.get().clear();
		transactionBatches.get().clear();
	}
	
	private Db(){}
	
	/**
	 * executeQuery
	 * @param conn
	 * @param sql
	 */
	public static List<Map<String,Object>> executeQuery(Connection conn, String sql){
		try(PreparedStatement ps =	conn.prepareStatement(sql);ResultSet rs = ps.executeQuery();){
			List<Map<String,Object>> mapList = new ArrayList<>();
			while(rs.next()){
				Map<String,Object> map = new HashMap<>();
				ResultSetMetaData rsmd = rs.getMetaData();
				int count = rsmd.getColumnCount();
				for(int i=1;i<count+1;i++){
					String name = rsmd.getColumnLabel(i);
					if(name == null){
						name = rsmd.getColumnName(i);
					}
					Object val = rs.getObject(i);
					map.put(name, val);
				}
				mapList.add(map);
			}
			return mapList;
		} catch (SQLException e) {
			throw new RuntimeException(e.getMessage(),e);
		}
	}
	
	/**
	 * executeUpdate
	 * @param conn
	 * @param sql
	 */
	public static int executeUpdate(Connection conn, String sql){
		try (PreparedStatement ps =	conn.prepareStatement(sql);){
			return ps.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e.getMessage() , e);
		}
	}
}
