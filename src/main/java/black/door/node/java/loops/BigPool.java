package black.door.node.java.loops;

import black.door.node.java.Conf;

import java.util.concurrent.*;

/**
 * Created by nfischer on 11/14/2015.
 */
public enum BigPool {
	INST;

	ExecutorService executorService;

	BigPool(){
		int bound = Conf.get().getInt("nodejava.blockingLoopThreadBound");
		if(bound > 0){
			throw new UnsupportedOperationException("Bounded blocking loop threads is not yet supported");
			/*
			executorService = new ThreadPoolExecutor(
					Runtime.getRuntime().availableProcessors(),
					bound,
					Conf.get().getInt("nodejava.threadKeepAlive"),
					TimeUnit.SECONDS,
					new LinkedBlockingDeque<>());
			*/
		}else{
			executorService = Executors.newCachedThreadPool();
		}
	}

	static synchronized ExecutorService getExecutorService(){
		while(INST.executorService == null){}
		return INST.executorService;
	}
}
