package black.door.node.java.loops;

import black.door.node.java.api.Node;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

/**
 * Created by nfischer on 11/22/2015.
 */
public class ResponseLoopTest {

	private int serverport = 12345;
	private int sleep = 1000;

	@Test
	public void testResponseOrder() {
		Node node = new Node().get("^/(?<n>\\d+)$", (request, response) -> {
			response.setBody(request.getPathParam("n"));
			BlockingLoop.submits(() -> {
						Thread.sleep(Long.valueOf(request.getQueryParam("sleep")));
						return null;
					},
					v -> response.send());
		});
		node.start(serverport);

		List<Future<HttpResponse<String>>> futures = new LinkedList<>();
		IntStream.range(0, 100).forEach(i->
				futures
						.add(i, Unirest.get("http://localhost:" + serverport + '/' + i)
								.queryString("sleep", sleep - i*10)
								.asStringAsync()));

		IntStream.range(0, 100).forEach(i -> {
			try {
				assertEquals(Integer.valueOf(i++), Integer.valueOf(futures.get(i-1).get().getBody()));
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				fail(e.getMessage());
			}
		});
	}
}