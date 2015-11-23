package black.door.node.java.loops;

import black.door.node.java.function.FunctionalFutureCallback;
import com.google.common.util.concurrent.*;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by nfischer on 10/27/2015.
 */
public enum BlockingLoop implements Loop{
	INST;

	public static <T> Future<T> submits(Callable<T> operation){
		return INST.submit(operation);
	}

	public static <T> void submits(Callable<T> operation,
	                              Consumer<T> successCallback,
	                              Consumer<Throwable> failureCallback) {
		INST.submit(operation, successCallback, failureCallback);
		/* basically how the listening executor service works
		return INST.executorService.submit(() -> {
			try {
				T result = operation.get();
				EventLoop.submit(() -> callback.accept(null, result));
				return result;
			} catch (Exception ex) {
				EventLoop.submit(() -> callback.accept(ex, null));
				throw ex;
			}
		});
		*/
	}

	public static <T> void submits(Callable<T> operation,
	                              Consumer<T> successCallback) {
		submits(operation, new FunctionalFutureCallback<>(successCallback));
	}

	/**
	 *
	 * @param operation an IO bound operation, such as a HTTP call,
	 *                  database access, or disk usage
	 * @param callback
	 * @param <T>
	 */
	public static <T> void submits(Callable<T> operation,
	                               FunctionalFutureCallback<T> callback){
		INST.submit(operation, callback);
	}

	private ListeningExecutorService executorService;

	BlockingLoop(){
		executorService = MoreExecutors.listeningDecorator(
				BigPool.getExecutorService());
	}

	@Override
	public ExecutorService getExecutorService() {
		return executorService;
	}

	public <T> void submit(Callable<T> operation,
	                       FunctionalFutureCallback<T> callback){
		ListenableFuture<T> future = this.executorService.submit(operation);
		Futures.addCallback(future, callback, EventLoop.INST.getExecutorService());
	}
}
