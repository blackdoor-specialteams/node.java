package black.door.node.java.benchmark;

import black.door.node.java.api.Node;
import black.door.node.java.loops.BlockingLoop;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

/**
 * Created by nfischer on 11/8/2015.
 */
public class Main {
	public static void main(String[] args) {
		new Node().get("^/fib/(?<n>\\d+)$", (request, response) -> {
			// sleep for some time to simulate an arbitrary blocking operation
			int sleep = Integer.valueOf(request.getQueryParam("sleep"));
			Future sleepFuture = BlockingLoop.submits(() -> {
				Thread.sleep(sleep);
				return null;
			});

			// do a ridiculous amount of computing
			List<Long> fibs = new LinkedList<>();
			IntStream.range(0, Integer.parseInt(request.getPathParam("n")))
					.forEach(i -> fibs.add(fibN(i)));

			// write results of computation to disk
			BlockingLoop.submits(() -> {
				File f = new File("outs/" + UUID.randomUUID().toString());
				f.createNewFile();
				StringBuilder sb = new StringBuilder();
				try (PrintStream os = new PrintStream(
						new BufferedOutputStream(
								new FileOutputStream(f)))) {
					fibs.forEach(l -> {
						os.println(l);
						sb.append(l).append('\n');
					});
				}
				String out = sb.toString();
				//Files.write(f.toPath(), out.getBytes());
				sleepFuture.get();
				return out;
			}, result -> {
				//everything worked, return result to client
				response.setBody(result);
				response.send();
			}, throwable -> {
				// something failed, return 500
				// we shouldn't actually need to do this, server should do it for us
				response.setStatusCode(500);
				response.setBody(throwable.toString());
				response.send();
			});
		})
				.run(8080);
	}

	public static long fibN(int n) {
		if (n <= 1)
			return n;
		return fibN(n - 2) + fibN(n - 1);
	}
}
