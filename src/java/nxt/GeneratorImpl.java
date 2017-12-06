package nxt;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import nxt.util.Listeners;
import nxt.util.ThreadPool;

/**
 * 
 * @author clark
 * 
 * 2017年12月5日 下午3:18:34
 * 
 * implementation of Genarator 
 */
public class GeneratorImpl implements Generator {
	
	// listener of GeneratorImpl
	private static final Listeners<Generator, Event> listeners = new Listeners<>();
	
	// using to store GeneratorState value with specific generator number 
	private static final ConcurrentMap<Long,GeneratorStateImpl> generators = new ConcurrentHashMap<>();
	
	// hold all the generators 
	private static final Collection<? extends GeneratorState> allGenerators = Collections.unmodifiableCollection(generators.values());
	
	// using this thread to forge a block 
	private static final Runnable generateBlockThread = new  Runnable() {
		
		@Override
		public void run() {
			// using this to product 
		}
	};
	
	static {
		// delay 500 milliseconds 
		ThreadPool.scheduleThread("GenerateBlocks", generateBlockThread, 500, TimeUnit.MILLISECONDS);
		
	}
	
	class GeneratorStateImpl implements GeneratorState{
		
	}
	
	
}
