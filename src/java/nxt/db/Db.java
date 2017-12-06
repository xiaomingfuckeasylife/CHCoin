package nxt.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import javax.sql.ConnectionPoolDataSource;

import nxt.Nxt;

/**
 * 
 * @author clark
 * 
 * 2017年12月6日 下午4:01:33
 * 
 */
public final class Db {
	
	/**
	 *  original jdbc ConnectionPool 
	 */
	private static final ConnectionPoolDataSource cp;
	
	/**
	 * max active connection.
	 */
	private static volatile int maxActiveConnections;
	
	private static final ThreadLocal<DbConnection> localConnection = new ThreadLocal<>();
	
	private static final ThreadLocal<Map<String,Map<DbKey,Object>>> transactionCaches = new ThreadLocal<>();
	
	private static final ThreadLocal<Map<String,Map<DbKey,Object>>> transactionBatches  = new  ThreadLocal<>();
	
	
	static{
		
		long maxCacheSize = Nxt.getIntProperty("nxt.dbCacheKB");
		
	}
	
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
				throw new UnsupportedOperationException("use Db.closeTransaction() to close connection");
			}
		}
	}
	
	static{
		
	}
	
}
