package black.door.node.java.benchmark;

import black.door.node.java.api.Node;
import black.door.node.java.loops.BlockingLoop;
import black.door.node.java.loops.ComputeLoop;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

/**
 * Created by nfischer on 11/8/2015.
 */
public class Main {
	public static void main(String[] args) {
		new Node().get("^/fib/(?<n>\\d+)$", (request, response) -> {
			// sleep for some time to simulate an arbitrary blocking operation
			int sleep = Integer.valueOf(request.getQueryParam("sleep"));
			Future sleepFuture;
			if(sleep > 0){
				sleepFuture = BlockingLoop.submits(() -> {
					Thread.sleep(sleep);
					return null;
				});
			}else{
				sleepFuture = new NullFuture();
			}


			Consumer<List<Long>> postCompute = fibs -> {
				// write results of computation to disk
				BlockingLoop.submits(() -> {
							String out = writeToDisk(fibs);
							sleepFuture.get();
							return out;
						},
						result -> {
							//everything worked, return result to client
							response.setBody(result);
							response.send();
						}
				);
			};

			int n = Integer.parseInt(request.getPathParam("n"));

			if(n < 2){
				postCompute.accept(LongStream.range(0, n)
						.collect((Supplier<LinkedList<Long>>) LinkedList::new,
						LinkedList<Long>::add,
						LinkedList<Long>::addAll));
			}else {
				// do a ridiculous amount of computing
				ComputeLoop.submits(() -> IntStream.range(0, n)
								.mapToLong(Main::fibN)
								.collect((Supplier<LinkedList<Long>>) LinkedList::new,
										LinkedList<Long>::add,
										LinkedList<Long>::addAll),
						postCompute);
			}
		})
				.run(8080);
	}

	private static String writeToDisk(List<Long> fibs) throws IOException, ExecutionException, InterruptedException {
		//File f = new File("outs/" + UUID.randomUUID().toString());
		//f.createNewFile();
		StringBuilder sb = new StringBuilder();
		//try (PrintStream os = new PrintStream(
		//		new BufferedOutputStream(
		//				new FileOutputStream(f)))) {
			fibs.forEach(l -> {
		//		os.println(l);
				sb.append(l).append('\n');
			});
		//}
		String out = sb.toString();
		//Files.write(f.toPath(), out.getBytes());
		return out;
	}

	public static long fibN(int n) {
		if (n <= 1)
			return n;
		return fibN(n - 2) + fibN(n - 1);
	}
}
