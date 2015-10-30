package black.door.node.java.loops;

import black.door.node.java.function.FunctionalFutureCallback;
import com.google.common.util.concurrent.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by nfischer on 10/27/2015.
 */
public enum GenericIoLoop implements AsyncService{
	INST;

	public static <T> Future<T> submits(Supplier<T> operation){
		return INST.submit(operation);
	}

	public static <T> void submits(Supplier<T> operation,
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

	public static <T> void submits(Supplier<T> operation,
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
	public static <T> void submits(Supplier<T> operation,
	                               FunctionalFutureCallback<T> callback){
		INST.submit(operation, callback);
	}

	private ListeningExecutorService executorService;

	GenericIoLoop(){
		executorService = MoreExecutors.listeningDecorator(
				Executors.newWorkStealingPool());
	}

	public <T> void submit(Supplier<T> operation,
	                       FunctionalFutureCallback<T> callback){
		ListenableFuture<T> future = this.executorService.submit(operation::get);
		Futures.addCallback(future, callback, EventLoop.getExecutorService());
	}

	@Override
	public <T> Future<T> submit(Supplier<T> operation) {
		return this.executorService.submit(operation::get);
	}

	@Override
	public <T> void submit(Supplier<T> operation,
	                       Consumer<T> success,
	                       Consumer<Throwable> failure) {
		submit(operation, new FunctionalFutureCallback<>(
				success,
				failure));
	}

}
