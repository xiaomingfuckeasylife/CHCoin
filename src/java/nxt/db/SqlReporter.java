package nxt.db;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.Arrays;

import nxt.util.Logger;
/**
 * 
 * @author clark
 * 
 * 2017年12月8日 下午1:45:24
 * 
 * proxy Connection . report sql before execute a sql .
 */
public final class SqlReporter implements InvocationHandler{
	
	private Connection conn;
	
	public SqlReporter(Connection conn){
		
		this.conn = conn;
	}
	
	public Connection getConnection(){
		Class<?> clz = conn.getClass();
		return (Connection) Proxy.newProxyInstance(clz.getClassLoader(),new Class[]{Connection.class},this);
	}
	
	@Override
	public Object invoke(Object obj, Method method, Object[] argsArr) throws Throwable {
		
		if(method.getName().toLowerCase().indexOf("statement") != -1){
			Logger.logMessage(" sql : " + Arrays.toString(argsArr));
		}
		try{
			return method.invoke(conn, argsArr);
		}catch(Exception ex){
			throw new RuntimeException(ex.getMessage(),ex);
		}
	}
	
}
