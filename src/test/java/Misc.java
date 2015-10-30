import org.junit.Test;

import java.util.function.Consumer;

/**
 * Created by nfischer on 10/29/2015.
 */
public class Misc {
	@Test
	public void test() throws Exception {
		Consumer<Void> fn = (v) -> {
			try {
				throw new RandomException();
			} catch (RandomException e) {
				throw new ExceptionWrapper(e);
			}
		};

		try{
			fn.accept(null);
		}catch (ExceptionWrapper e){
			Throwable cause = e.getCause();
			if(cause instanceof Error)
				throw (Error) cause;
			if(cause instanceof RuntimeException)
				throw (RuntimeException) cause;
			throw (Exception) e.getCause();
		}

	}

	public static class RandomException extends Exception{

	}

	public static class ExceptionWrapper extends RuntimeException{
		public ExceptionWrapper(Throwable e){
			super(e);
		}
	}
}
