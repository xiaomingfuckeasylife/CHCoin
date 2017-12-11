package nxt;

import java.math.BigInteger;
import java.util.List;

import org.json.simple.JSONObject;

public interface Block {
	
	int getVersion();
	
	int getId();
	
	String getStringId();
	
	int getHeight();
	
	int getTimestamp();
	
	long getGeneratorId();
	
	Long getNonce();
	
	int getScoopNum();
	
	byte[] getGeneratorPublicKey();
	
	byte[] getBlockHash();
	
	long getPreviousBlockId();
	
	byte[] getPreviousBlockHash();
	
	long getNextBlockId();
	
	long getTotalAmountNQT();
	
	long getTotalFeeNQT();
	
	int getPayloadHash();
	
	List<? extends Transaction> getTransactions();
	
	byte[] getGenerationSignature();
	
	byte[] getBlockSignature();
	
	long getBaseTarget();
	
	long getBlockReward();
	
	BigInteger getCumulativeDifficulty();
	
	JSONObject getJSONObject();
	
	byte[] getBlockAts();
}
