package nxt;

/**
 * 
 * @author clark
 * 
 * 2017年12月11日 下午3:08:56
 * 
 */
public interface Blockchain {
	
	Block getLastBlock();
	
	Block getLastBlock(int timestamp);
	
	int getHeight();
	
	Block getBlock(long blockId);
	
	Block getBlockAtHeight(int height);
	
	boolean hasBlock(long blockId);
	
}
