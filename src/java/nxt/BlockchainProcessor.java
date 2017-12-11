package nxt;

/**
 * 
 * @author clark
 * 
 * 2017年12月11日 上午9:29:39
 * 
 * Observe `Block`
 * 
 */
public interface BlockchainProcessor extends Observable<Block, BlockchainProcessor.Event>{
	
	public static enum Event{
		
		BLOCK_PUSHED, BLOCK_POPPED , BLOCK_GENERATION , BLOCK_SCANED,
		
		RESCAN_BEGIN,RESCAN_END,
		
		BEFORE_BLOCK_ACCEPT,
		
		BEFORE_BLOCK_APPLY,AFTER_BLOCK_APPLY
		
	}
	
	
	
}
