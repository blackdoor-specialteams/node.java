package black.door.node.java.loops;

import black.door.dbp.DBP;
import black.door.dbp.StandardChannelName;
import black.door.node.java.Conf;
import black.door.node.java.function.FunctionalFutureCallback;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by nfischer on 10/27/2015.
 */
public enum EventLoop implements Loop{
	INST;

	private ExecutorService executorService;

	EventLoop(){
		executorService = Executors.newWorkStealingPool();
	}

	public static Future<?> submit(Runnable r){
		return INST.executorService.submit(r);
	}

	public ExecutorService getExecutorService(){
		return this.executorService;
	}

}
