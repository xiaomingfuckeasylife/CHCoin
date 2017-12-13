package nxt;

/**
 * 
 * @author clark
 * 
 * 2017年12月12日 上午9:50:37
 * 
 * genesis block 
 */
final class Genesis {
	
	/**
	 * genesis id 
	 */
	static final long GENESIS_BLOCK_ID = 223421231421413L;
	
	/**
	 * 32 byte 
	 */
	static final byte[] CREATOR_PUBLIC_KEY = new byte[]{
			0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
			0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
	};
	
	/**
	 * 64 byte 
	 */
	static final byte[] GENESIS_BLOCK_SIGNATURE = new byte[]{
			0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
			0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
			0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
			0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
	};
	
	static final long CREATOR_ID = 0L;
	static final long GENESIS_RECIPIENTS = 0L;
	static final int []GENESIS_AMOUNTS = {};
}

