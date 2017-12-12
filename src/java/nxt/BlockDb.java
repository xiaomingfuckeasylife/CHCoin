package nxt;

import java.math.BigInteger;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import nxt.db.Db;

/**
 * 
 * @author clark
 * 
 * 2017年12月12日 上午9:43:44
 * 
 * if has block or not 
 */
final class BlockDb {
	
	
	/**
	 * has block or not
	 */
	static boolean hasBlock(long blockId){
		
		try(Connection conn = Db.getConnection();){
			return (Long)Db.executeQuery(conn, "select count(*) as num from block where id = " + blockId).get("num") >0;
		}catch(Exception ex){
			throw new RuntimeException(ex.getMessage(),ex);
		}
		
	}

	/**
	 * find the last block.
	 * @return
	 */
	static BlockImpl findLastBlock(){
		try(Connection conn = Db.getConnection();){
			Map<String,Object> blockMap = Db.executeQuery(conn, "select * from block order by db_id desc limit 1");
			return loadBlock(blockMap);
		}catch(Exception ex){
			throw new RuntimeException(ex.getMessage(),ex);
		}
	}
	
	private static BlockImpl loadBlock(Map<String,Object> blockMap){
		
		int version = (int) blockMap.get("version");
		int timestamp = (int) blockMap.get("timestamp");
		long previousBlockId = (long) blockMap.get("previousBlockId");
		long totalAmountNQT = (long) blockMap.get("totalAmountNQT");
		long totalFeeNQT = (long) blockMap.get("totalFeeNQT");
		int payloadLength = (int) blockMap.get("payloadLength");
		byte[] generatorPublicKey =  (byte[]) blockMap.get("generatorPublicKey");
		byte[] previousBlockHash =  (byte[]) blockMap.get("previousBlockHash");
		BigInteger cumulativeDifficulty = new BigInteger((byte[])blockMap.get("cumulative_difficulty"));
		long baseTarget = (long) blockMap.get("base_target");
		long nextBlockId = (long) blockMap.get("next_block_id");
		int height = (int) blockMap.get("height");
		byte[] generationSignature =  (byte[]) blockMap.get("generationSignature");
		byte[] blockSignature =  (byte[]) blockMap.get("block_signature");
		byte[] payloadHash =  (byte[]) blockMap.get("payloadHash");
		long id = (long) blockMap.get("id");
		long nonce = (long) blockMap.get("nonce");
		byte[] blockATs =  (byte[]) blockMap.get("ats");
		
		return new BlockImpl(version,timestamp,previousBlockId,totalAmountNQT,totalFeeNQT,payloadLength,payloadHash,generatorPublicKey,generationSignature,blockSignature,
				previousBlockHash,cumulativeDifficulty,baseTarget,nextBlockId,height,id,nonce,blockATs);
	}
}
