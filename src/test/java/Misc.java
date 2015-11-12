
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.net.URI;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.IntStream;

/**
 * Created by nfischer on 10/29/2015.
 */
public class Misc {
	@Test
	public void test() throws Exception {

		System.out.println(new URI("http://thing.com/otherthing/cat").getPath());

		Consumer<Void> fn = (v) -> {
			try {
				throw new RandomException();
			} catch (RandomException e) {
				throw new ExceptionWrapper(e);
			}
		};
/*
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
*/
		ObjectMapper om = new ObjectMapper();
		System.out.println(om.writeValueAsString(new Exception("hi")));

		ExecutorService executorService = Executors.newWorkStealingPool();
		executorService.submit(() -> {
			if(true)
				throw new Exception("wooo");
			return 5;
		});

	}

	public static class RandomException extends Exception{

	}

	public static class ExceptionWrapper extends RuntimeException{
		public ExceptionWrapper(Throwable e){
			super(e);
		}
	}

	@Test
	public void fibBench(){
		List<Long> times = new LinkedList<>();
		times.add((long) 0);
		times.add((long) 0);
		long avgTime = 0;
		int n = 2;
		while(avgTime < 200){
			System.out.println(n);
			List<Long> results = new LinkedList<>();
			for(int i = 0; i < 30; i++){
				long start = System.currentTimeMillis();
				IntStream.range(0, n).forEach(this::fibN);
				//fibN(n);
				results.add(System.currentTimeMillis() -start);
			}
			avgTime = results.stream().mapToLong(value -> value).sum() /
					results.size();
			times.add(avgTime);
			n++;
		}
		Iterator<Long> ti = times.iterator();
		for(int i = 0; i < times.size(); i++){
			System.out.println("n:" + i + " " + ti.next());
		}
	}

	public long fibN(int n){
		if(n <= 1)
			return n;
		return fibN(n-2) + fibN(n-1);
	}
}
