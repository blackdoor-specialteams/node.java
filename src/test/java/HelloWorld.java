import black.door.node.java.api.Node;
import com.google.common.net.HttpHeaders;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by nfischer on 11/8/2015.
 */
public class HelloWorld {
	public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException {

		/*
		ExecutorService es = Executors.newWorkStealingPool();
		ServerSocket ss = new ServerSocket(8080);

		Future<Socket> futureS = es.submit(() -> {
			Socket s = ss.accept();
			//s.getOutputStream().write("hello\n".getBytes());
			return s;
		});

		es.submit(() -> {
			try {
				Socket s = futureS.get();
				System.out.println(ParseTools.parseHeaders(s.getInputStream()));
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		});

		Thread.sleep(9999 * 1000);
		*/

		System.out.println(new URI("website.com/path/woo#thing"));


		Node node = new Node();
		node.get("/hello", (request, response) -> {
			String body = "";
			body += request.getUri();
			body += request.getQueryParams();
			body += request.getHeaders();
			response.setBody(body);
			response.send();
		});

		node.get("/error", ((request1, response1) -> {
			throw new RuntimeException("problem!");
		}));

		node.get("/red", (request, response) -> {
			long start = System.currentTimeMillis();
			response.setStatusCode(200);
			response.putHeader(HttpHeaders.LOCATION, "localhost:8080/hello");
			response.send();
			System.out.println(System.currentTimeMillis());
			System.out.println(System.currentTimeMillis() - start);
		});

		node.run(8080);

	}
}
