package black.door.node.java.loops;

import black.door.node.java.Router;
import black.door.node.java.api.http;
import black.door.node.java.http.HttpContext;
import black.door.node.java.http.HttpRequest;
import black.door.node.java.http.HttpResponse;
import org.apache.commons.io.input.BoundedInputStream;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

/**
 * Created by nfischer on 10/29/2015.
 */
public class RequestLoop implements Runnable, Closeable{

	public static final int MAX_REQUEST_SIZE = 12*8;

	private ServerSocket serverSocket;
	private ExecutorService executorService;
	private boolean running = false;
	private int port;

	public RequestLoop(int port){
		executorService = Executors.newWorkStealingPool();
		this.port = port;
	}

	public void run(){
		if(serverSocket != null){
			serverSocket.close();
		}
		serverSocket = new ServerSocket(port);

		requestLoop();
	}

	private void requestLoop() throws IOException {
		//INST.executorService.submit(() -> {
		while(running){
			if(serverSocket == null){
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}else{
				Socket sock = serverSocket.accept(); //TODO should io exception be handled here?
				sock.setSoTimeout(7 *1000);
				executorService.submit(handleConnection(sock));
			}
		}
		//});
	}

	private Runnable handleConnection(Socket sock) {
		return () -> {
			try {
				while (running) {
					InputStream is = new BoundedInputStream(
							sock.getInputStream(), MAX_REQUEST_SIZE);
					HttpRequest request = null;
					try {
						request = HttpRequest.parse(is, -1);
					} catch (SocketTimeoutException e) { // persistent http connection? bring it
						break;
					} catch (URISyntaxException e) {
						//TODO 400
						e.printStackTrace();
					}
					if(request != null) {
						EventLoop.submit(handleRequest(request, sock));
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		};
	}

	private Runnable handleRequest(HttpRequest request, Socket sock){
		return () -> {
			BiConsumer<HttpRequest, HttpResponse> controller;
			controller = Router.getController(request);
			HttpContext context = new HttpContext(request, sock);
			if(controller == null){
				context.getResponse().setStatusCode(404);
				context.getResponse().setStatusMessage("Not Found");
				context.getResponse().end();
			}else{
				try {
					controller.accept(request, context.getResponse());
				}catch (Exception e){
					e.printStackTrace();
					context.getResponse().setStatusCode(500);
					context.getResponse().setStatusMessage("Internal Server Error");
					//todo add configurable setting to enable sending json format exception in response
					context.getResponse().end();
				}
			}
		};
	}


	@Override
	public void close() throws IOException {
		running = false;
		serverSocket.close();
		executorService.shutdown();
	}
}
