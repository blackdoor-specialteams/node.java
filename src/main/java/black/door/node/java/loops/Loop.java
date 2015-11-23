package black.door.node.java.loops;

import black.door.node.java.function.FunctionalFutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Consumer;

/**
 * Created by nfischer on 11/15/2015.
 */
public interface Loop {

	ExecutorService getExecutorService();

	default <T> void submit(Callable<T> operation,
	                        FunctionalFutureCallback<T> callback){
		getExecutorService().submit(() -> {
			try {
				T result = operation.call();
				getExecutorService().submit(() -> callback.onSuccess(result));
			} catch (Throwable ex) {
				getExecutorService().submit(() -> callback.onFailure(ex));
			}
		});
	}

	default <T> Future<T> submit(Callable<T> operation) {
		return this.getExecutorService().submit(operation::call);
	}

	default <T> void submit(Callable<T> operation,
	                       Consumer<T> success,
	                       Consumer<Throwable> failure) {
		submit(operation, new FunctionalFutureCallback<>(
				success,
				failure));
	}
}
