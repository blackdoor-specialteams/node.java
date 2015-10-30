package black.door.node.java.loops;

import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by nfischer on 10/30/2015.
 */
public interface AsyncService {
	<T> Future<T> submit(Supplier<T> operation);

	default <T> void submit(Supplier<T> operation, Consumer<T> success){
		submit(operation, success, Throwable::printStackTrace);
	}
	<T> void submit(Supplier<T> operation,
	                Consumer<T> success,
	                Consumer<Throwable> failure);
}
