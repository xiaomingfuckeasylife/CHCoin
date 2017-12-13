package nxt.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import nxt.Nxt;

/**
 * 
 * @author clark
 * 
 * 2017年12月5日 下午4:34:11
 * 
 * thread pool to run application on the background 
 * 
 */
public final class ThreadPool {
	/**
	 * scheduled running thread 
	 */
	private static ScheduledExecutorService scheduledThreadPool;
	/**
	 * running in backgroud , long value is the delay time of the task . HashMap not thread safe . using synchronized 
	 */
	private static Map<Runnable,Long> backgroudJobs = new HashMap<>();
	private static Map<Runnable,Long> backgroundJobsCores = new HashMap<>();
	/**
	 * prefix and suffix of the job running .
	 */
	private static List<Runnable> beforeStartJobs = new ArrayList<>();
	private static List<Runnable> lastBeforeStartJobs = new ArrayList<>();
	private static List<Runnable> afterStartJobs = new ArrayList<>();
	
	/**
	 * @param name
	 * @param runnable
	 * @param dely
	 */
	public static synchronized void scheduleThread(String name , Runnable runnable , int dely ){
		scheduleThread(name, runnable, dely, TimeUnit.SECONDS);
	}
	
	
	/**
	 * schedule a task and put it in the background job 。
	 * should be one by one 
	 * @param name
	 * @param runnable
	 * @param dely
	 * @param timeUnit
	 */
	public static synchronized void scheduleThread(String name , Runnable runnable , int dely , TimeUnit timeUnit){
		
		if(scheduledThreadPool != null){
			throw new IllegalStateException("Executor service already started , no new jobs accepted");
		}
		
		if(!Nxt.getBooleanProperties("nxt.disable"+name+"Thread")){
			backgroudJobs.put(runnable, timeUnit.toMillis(dely));
		}else{
			Logger.logMessage("will not run " + name + " thread");
		}
	}
	
	/**
	 * put runnable on container run before start 
	 * @param runnable
	 * @param runLast
	 */
	public static synchronized void runBeforeStart(Runnable runnable,boolean runLast){
	
		if(scheduledThreadPool == null){
			throw new IllegalStateException("Executor service already started , no new jobs accepted");
		}
		
		if(runLast)
			lastBeforeStartJobs.add(runnable);
		else 
			beforeStartJobs.add(runnable);
	}
	
}