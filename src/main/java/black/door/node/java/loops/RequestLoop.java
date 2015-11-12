package black.door.node.java.loops;

import black.door.dbp.DBP;
import black.door.dbp.StandardChannelName;
import black.door.node.java.Conf;
import black.door.node.java.Router;
import black.door.node.java.api.Route;
import black.door.node.java.api.Server;
import black.door.node.java.exception.WrappedException;
import black.door.node.java.http.HttpContext;
import black.door.node.java.exception.HttpParsingException;
import black.door.node.java.http.HttpRequest;
import black.door.node.java.http.HttpResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;
import org.apache.commons.io.input.BoundedInputStream;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.nio.channels.ClosedChannelException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

/**
 * Created by nfischer on 10/29/2015.
 */
public class RequestLoop implements Runnable, Closeable{

	public static final int MAX_REQUEST_SIZE = 8 * 1024/*kB*/;

	private ServerSocket serverSocket;
	private ExecutorService executorService;
	private int port;
	private Server server;
	private Router router;
	private ObjectWriter ow;

	public RequestLoop(Server server) throws IOException {
		this.server = server;
		this.router = server.getRouter();
		executorService = Executors.newWorkStealingPool();
		this.port = server.getPort();
		serverSocket = new ServerSocket(port);
		ow = new ObjectMapper().writer();
	}

	public void run(){
		while(server.getRunning().get()){
			if(serverSocket == null){
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					DBP.error().log(e.toString() + (e.getMessage() == null
									? ""
									: ": " + e.getMessage()),
							e.getStackTrace());
				}
			}else{
				try {
					Socket sock = serverSocket.accept();
					sock.setSoTimeout(7 * 1000);
					executorService.submit(handleConnection(sock));
				}catch (IOException e){
					DBP.channel(StandardChannelName.WARNING).log(e.toString() + (e.getMessage() == null
									? ""
									: ": " + e.getMessage()),
							e.getStackTrace());
					throw new WrappedException(e);
				}
			}
		}
	}

	private Runnable handleConnection(Socket sock) {
		return () -> {
			try {
				InputStream sockIs = new BufferedInputStream(
						sock.getInputStream());
				while (server.getRunning().get()) {

					InputStream is = new BoundedInputStream(
							sockIs, MAX_REQUEST_SIZE);
					HttpRequest request = null;
					try {
						request = HttpRequest.parse(is, -1);
					} catch (SocketTimeoutException e) {
					// persistent http connection? bring it
						sock.close();
						break;
					} catch (URISyntaxException | HttpParsingException e) {
						HttpResponse response = new HttpResponse("HTTP/1.1",
								400, "Bad Request");
						ResponseLoop.sendResponse(response, sock);
						DBP.channel(StandardChannelName.WARNING).log(e.toString() + (e.getMessage() == null
										? ""
										: ": " + e.getMessage()),
								e.getStackTrace());
					}
					if(request != null) {
						EventLoop.submit(handleRequest(request, sock));
					}
				}
			} catch (IOException e) {
				if(!sock.isClosed()) {
					try {
						sock.close();
					} catch (IOException e1) {
						DBP.error().log(e.toString() + (e.getMessage() == null
										? ""
										: ": " + e.getMessage()),
								e.getStackTrace());
					}
				}
				if (!(e instanceof ClosedChannelException))
					DBP.channel(StandardChannelName.WARNING).log(e.toString() + (e.getMessage() == null
								? ""
								: ": " + e.getMessage()),
						e.getStackTrace());
			}
		};
	}

	private Runnable handleRequest(HttpRequest request, Socket sock){
		return () -> {
			BiConsumer<HttpRequest, HttpResponse> controller;
			Route route = router.get(request.getVerb(), request.getUri().getPath());
			HttpContext context = new HttpContext(request, sock);
			if(route == null){
				context.getResponse().setStatusCode(404);
				context.getResponse().setBody(request.getUri().getPath() +
						" not found.");
				context.getResponse().send();
			}else{
				controller = route.getController();
				request.definePathParams(route.getPath());
				try {
					controller.accept(request, context.getResponse());
				}catch (Exception e){
					DBP.error().log(e.toString() + (e.getMessage() == null
									? ""
									: ": " + e.getMessage()),
							e.getStackTrace());
					HttpResponse response = context.getResponse();
					response.setStatusCode(500);

					if(Conf.get().getBoolean("nodejava.detailed500")){
						try {
							response.putHeader(HttpHeaders.CONTENT_TYPE,
									String.valueOf(MediaType.JSON_UTF_8));
							response.setBody(ow.writeValueAsBytes(e));
						} catch (JsonProcessingException e1) {
							DBP.error().log(e.toString() + (e.getMessage() == null
											? ""
											: ": " + e.getMessage()),
									e.getStackTrace());
						}
					}
					response.send();
				}
			}
		};
	}


	@Override
	public void close() throws IOException {
		server.getRunning().set(false);
		serverSocket.close();
		executorService.shutdown();
	}
}
