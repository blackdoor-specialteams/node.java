package black.door.node.java.loops;

import black.door.dbp.DBP;
import black.door.dbp.StandardChannelName;
import black.door.node.java.Conf;
import black.door.node.java.function.FunctionalFutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

/**
 * Created by nfischer on 11/15/2015.
 */
public enum ComputeLoop implements Loop{
	INST;

	public static <T> Future<T> submits(Callable<T> operation){
		return INST.submit(operation);
	}

	public static <T> void submits(Callable<T> operation,
	                               Consumer<T> successCallback,
	                               Consumer<Throwable> failureCallback) {
		INST.submit(operation, successCallback, failureCallback);
	}

	public static <T> void submits(Callable<T> operation,
	                               Consumer<T> successCallback) {
		submits(operation, new FunctionalFutureCallback<>(successCallback));
	}

	public static <T> void submits(Callable<T> operation,
	                               FunctionalFutureCallback<T> callback){
		INST.submit(operation, callback);
	}

	private ListeningExecutorService executorService;

	ComputeLoop(){
		boolean hyperthreadCompensation = Conf.get()
				.getBoolean("nodejava.hyperthreadCompensation");
		int nCores = Runtime.getRuntime().availableProcessors();
		int freeCores = Conf.get().getInt("nodejava.freeCores");
		int nThreads = nCores / (hyperthreadCompensation ? 2 : 1);
		nThreads -= freeCores;
		DBP.channel(StandardChannelName.INFO).log("Starting compute loop on "
				+nThreads +" threads.");
		this.executorService = MoreExecutors.listeningDecorator(
				Executors.newWorkStealingPool(nThreads));
	}

	@Override
	public ExecutorService getExecutorService() {
		return executorService;
	}

	@Override
	public <T> void submit(Callable<T> operation, FunctionalFutureCallback<T> callback) {
		ListenableFuture<T> future = this.executorService.submit(operation);
		Futures.addCallback(future, callback, EventLoop.INST.getExecutorService());
	}
}
