package black.door.node.java.function;

import com.google.common.util.concurrent.FutureCallback;

import java.util.function.Consumer;

/**
 * Created by nfischer on 10/28/2015.
 */
public class FunctionalFutureCallback<V> implements FutureCallback<V> {
	private Consumer<Throwable> failure;
	private Consumer<V> success;

	public FunctionalFutureCallback(Consumer<V> success){
		this(success, Throwable::printStackTrace);
	}

	public FunctionalFutureCallback(Consumer<V> success, Consumer<Throwable> failure){
		this.failure = failure;
		this.success = success;
	}

	@Override
	public void onSuccess(V v) {
		success.accept(v);
	}

	@Override
	public void onFailure(Throwable throwable) {
		failure.accept(throwable);
	}
}
