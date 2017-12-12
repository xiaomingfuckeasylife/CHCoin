package nxt.db;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nxt.TransactionImpl;

/**
 * 
 * @author clark
 * 
 * 2017年12月12日 下午2:25:12
 * 
 */
public class TransactionDb {

	static List<TransactionImpl> findBlockTransactions(int blockId){
		
		try(Connection conn = Db.getConnection();){
			List<Map<String,Object>> mapList = Db.executeQuery(conn, "select * from `transaction` where block_id = " + blockId + " and signature is not null order by id");
			List<TransactionImpl> list = new ArrayList<>();
			for(Map<String,Object> map : mapList){
				list.add(loadTransaction(map));
			}
		}catch(Exception ex){
			throw new RuntimeException(ex.getMessage(),ex);
		}
		
	};
	
	
}
