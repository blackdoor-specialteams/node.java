package black.door.node.java.loops;

import black.door.dbp.DBP;
import black.door.dbp.StandardChannelName;
import black.door.node.java.Conf;

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
		boolean hyperthreadCompensation = Conf.get()
				.getBoolean("nodejava.hyperthreadCompensation");
		int nCores = Runtime.getRuntime().availableProcessors();
		int freeCores = Conf.get().getInt("nodejava.freeCores");
		int nThreads = nCores / (hyperthreadCompensation ? 2 : 1);
		nThreads -= freeCores;
		DBP.channel(StandardChannelName.INFO).log("Starting event loop on "
				+nThreads +" threads.");
		executorService = Executors.newFixedThreadPool(nThreads);
	}

	public static Future<?> submit(Runnable r){
		return INST.executorService.submit(r);
	}

	public static ExecutorService getExecutorService(){
		return INST.executorService;
	}
}
