package nxt;

import nxt.util.Listener;

/**
 * 
 * @author clark
 * 
 * 2017年12月11日 上午11:13:38
 * 
 * observe the event 
 */
public interface Observable<T,E extends Enum<E>> {
	
	boolean addListener(Listener<T> listener,E eventType);
	
	boolean removeListener(Listener<T> listener, E eventType);
	
}
