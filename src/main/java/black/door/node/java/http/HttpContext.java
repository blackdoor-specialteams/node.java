package black.door.node.java.http;

import java.net.Socket;

/**
 * Created by nfischer on 10/29/2015.
 */
public class HttpContext {
	HttpRequest request;
	Socket sock;
	HttpResponse response;

	public HttpContext(HttpRequest request, Socket sock){
		this.request = request;
		this.sock = sock;
		this.response = new HttpResponse("HTTP/1.1", 200, "OK");
		response.setContext(this);
	}

	public HttpRequest getRequest() {
		return request;
	}

	public HttpContext setRequest(HttpRequest request) {
		this.request = request;
		return this;
	}

	public Socket getSock() {
		return sock;
	}

	public HttpContext setSock(Socket sock) {
		this.sock = sock;
		return this;
	}

	public HttpResponse getResponse() {
		return response;
	}

	public HttpContext setResponse(HttpResponse response) {
		this.response = response;
		return this;
	}
}
