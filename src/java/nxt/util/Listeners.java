package nxt.util;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 
 * @author clark
 * 
 * 2017年12月5日 下午3:21:22
 * 
 * Listener of the block chain
 * 
 * T is the listening Object , and there must be a event 
 * that is a enum type to listen which means the listening
 * Object has have a event enum subtype .
 */
public final class Listeners<T ,E extends Enum<E>> {
	
	/**
	 * store listener for specific event .
	 */
	private ConcurrentHashMap<Enum<E>,List<Listener<T>>> listenersMap = new ConcurrentHashMap<>();
	
	/**
	 * notify all the listener that listening at this event .
	 * @param t
	 * @param event
	 */
	public void notify(T t , Enum<E> event){
		List<Listener<T>> listListener = listenersMap.get(event);
		if(listListener != null){
			for(int i=0;i<listListener.size();i++){
				Listener<T> listener = listListener.get(i);
				listener.notify(t);
			}
		}
	}
	
	/**
	 * 
	 * @param listener
	 * @param eventType
	 */
	public void addListener(Listener<T> listener , E eventType){
		synchronized (eventType) {
			List<Listener<T>> listListener = listenersMap.get(eventType);
			if(listListener == null){
				listListener = new CopyOnWriteArrayList<>();
				listenersMap.put(eventType,listListener);
			}
			listListener.add(listener);
		}
	}
	
	/**
	 * 
	 * @param listener
	 * @param eventType
	 * @return
	 */
	public boolean removeListener(Listener<T> listener, E eventType){
		synchronized (eventType) {
			List<Listener<T>> listenerList = listenersMap.get(eventType);
			if(listenerList != null){
				return listenerList.remove(listener);
			}
		}
		return false;
	}
}
