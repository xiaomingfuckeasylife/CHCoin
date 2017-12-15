package nxt;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nxt.db.Db;

/**
 * 
 * @author clark
 * 
 * 2017年12月14日 下午2:07:47
 * 
 * 
 */
public class PeerDb {
	
	public static List<String> loadPeers(){
		
		try(Connection conn = Db.getConnection();){
			
			List<Map<String,Object>> mapList = Db.executeQuery(conn, "select * from peer");
			List<String> retList = new ArrayList<>();
			for(int i =0;i<mapList.size();i++){
				retList.add((String)mapList.get(i).get("address"));
			}
			return retList;
		}catch(Exception ex){
			throw new RuntimeException(ex.getMessage(), ex);
		}
		
	}
}
