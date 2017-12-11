package nxt;

import java.math.BigInteger;
import java.util.List;

import org.json.simple.JSONObject;

public class BlockImpl implements Block {

	@Override
	public int getVersion() {
		return 0;
	}

	@Override
	public int getId() {
		return 0;
	}

	@Override
	public String getStringId() {
		return null;
	}

	@Override
	public int getHeight() {
		return 0;
	}

	@Override
	public int getTimestamp() {
		return 0;
	}

	@Override
	public long getGeneratorId() {
		return 0;
	}

	@Override
	public Long getNonce() {
		return null;
	}

	@Override
	public int getScoopNum() {
		return 0;
	}

	@Override
	public byte[] getGeneratorPublicKey() {
		return null;
	}

	@Override
	public byte[] getBlockHash() {
		return null;
	}

	@Override
	public long getPreviousBlockId() {
		return 0;
	}

	@Override
	public byte[] getPreviousBlockHash() {
		return null;
	}

	@Override
	public long getNextBlockId() {
		return 0;
	}

	@Override
	public long getTotalAmountNQT() {
		return 0;
	}

	@Override
	public long getTotalFeeNQT() {
		return 0;
	}

	@Override
	public int getPayloadHash() {
		return 0;
	}

	@Override
	public List<? extends Transaction> getTransactions() {
		return null;
	}

	@Override
	public byte[] getGenerationSignature() {
		return null;
	}

	@Override
	public byte[] getBlockSignature() {
		return null;
	}

	@Override
	public long getBaseTarget() {
		return 0;
	}

	@Override
	public long getBlockReward() {
		return 0;
	}

	@Override
	public BigInteger getCumulativeDifficulty() {
		return null;
	}

	@Override
	public JSONObject getJSONObject() {
		return null;
	}
	
	@Override
	public byte[] getBlockAts() {
		return null;
	}

}
