package black.door.node.java.function;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by nfischer on 10/27/2015.
 */
public interface FunctionalPotato extends Function<Void, Void>, Consumer<Void>, Supplier<Void>{
	void call();

	default void accept(Void v){call();}
	default Void apply(Void v){call();return null;}
	default Void get(){call();return null;}
}
