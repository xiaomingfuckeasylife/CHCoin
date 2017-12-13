package nxt.db;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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
 * 
 */
public class TransactionDb {
	
	public static List<TransactionImpl> findBlockTransactions(long blockId){
		
		try(Connection conn = Db.getConnection();){
			List<Map<String,Object>> mapList = Db.executeQuery(conn, "select * from `transaction` where block_id = " + blockId + " and signature is not null order by id");
			List<TransactionImpl> list = new ArrayList<>();
			for(Map<String,Object> map : mapList){
				list.add(loadTransaction(map));
			}
			return list;
		}catch(Exception ex){
			throw new RuntimeException(ex.getMessage(),ex);
		}
		
	};
	
	
	private static TransactionImpl loadTransaction(Map<String,Object> map){
		try {
			
            byte type = (byte) map.get("type");
            byte subtype = (byte) map.get("subtype");
            int timestamp = (int) map.get("timestamp");
            short deadline = (short) map.get("deadline");
            byte[] senderPublicKey = (byte[]) map.get("sender_public_key");
            long amountNQT = (long) map.get("amount");
            long feeNQT = (long) map.get("fee");
            byte[] referencedTransactionFullHash = (byte[]) map.get("referenced_transaction_full_hash");
            int ecBlockHeight = (int) map.get("ec_block_height");
            long ecBlockId = (long) map.get("ec_block_id");
            byte[] signature = (byte[]) map.get("signature");
            long blockId = (long) map.get("block_id");
            int height = (int) map.get("height");
            long id = (long) map.get("id");
            long senderId = (long) map.get("sender_id");
            byte[] attachmentBytes = (byte[]) map.get("attachment_bytes");
            int blockTimestamp = (int) map.get("block_timestamp");
            byte[] fullHash = (byte[]) map.get("full_hash");
            byte version = (byte) map.get("version");
            
            // add transaction attachment 
            ByteBuffer buffer = null;
            if (attachmentBytes != null) {
                buffer = ByteBuffer.wrap(attachmentBytes);
                buffer.order(ByteOrder.LITTLE_ENDIAN);
            }
            
            // creating a transaction 
            TransactionType transactionType = TransactionType.findTransactionType(type, subtype);
            TransactionImpl.BuilderImpl builder = new TransactionImpl.BuilderImpl(version, senderPublicKey,
                    amountNQT, feeNQT, timestamp, deadline,
                    transactionType)
                    .referencedTransactionFullHash(referencedTransactionFullHash)
                    .signature(signature)
                    .blockId(blockId)
                    .height(height)
                    .id(id)
                    .senderId(senderId)
                    .blockTimestamp(blockTimestamp)
                    .fullHash(fullHash);

            if (transactionType.hasRecipient()) {
                long recipientId = (long) map.get("recipient_id");
                builder.recipientId(recipientId);
            }
            
            if (version > 0) {
                builder.ecBlockHeight(ecBlockHeight);
                builder.ecBlockId(ecBlockId);
            }

            return builder.build();

        } catch (Exception e) {
            throw new RuntimeException(e.toString(), e);
        }
	}
}
