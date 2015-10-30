package black.door.node.java.loops;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by nfischer on 10/27/2015.
 */
public enum EventLoop {
	INST;

	private ExecutorService executorService;

	EventLoop(){
		executorService = Executors.newFixedThreadPool(
				Runtime.getRuntime().availableProcessors());
	}

	public static Future<?> submit(Runnable r){
		return INST.executorService.submit(r);
	}

	public static ExecutorService getExecutorService(){
		return INST.executorService;
	}
}
