package black.door.node.java.api;

import black.door.node.java.Router;
import black.door.node.java.http.HttpRequest;
import black.door.node.java.http.HttpResponse;
import black.door.node.java.http.HttpVerb;

import java.util.function.BiConsumer;
import java.util.regex.Pattern;

/**
 * Created by nfischer on 10/29/2015.
 */
public class Node {

	private Router router;

	public Node(){
		router = new Router();
	}

	private Server prepServer(int port){
		return new Server(port, router);
	}

	/**
	 * Starts a new node.java server as dæmon in a new thread.
	 * @return an instance of a running server
	 */
	public Server start(int port){
		Server s = new Server(port, router);
		new Thread(s).start();
		return s;
	}

	/**
	 * Starts a node.java server in the current thread.
	 */
	public void run(int port){
		new Server(port, router).run();
	}

	public Node route(HttpVerb method, String path,
	                  BiConsumer<HttpRequest, HttpResponse> controller){
		router.add(new Route(method, Pattern.compile(path), controller));
		return this;
	}

	//region convenience methods

	public Node get(String path, BiConsumer<HttpRequest, HttpResponse> controller){
		return route(HttpVerb.GET, path, controller);
	}

	public Node put(String path, BiConsumer<HttpRequest, HttpResponse> controller){
		return route(HttpVerb.PUT, path, controller);
	}

	public Node post(String path, BiConsumer<HttpRequest, HttpResponse> controller){
		return route(HttpVerb.POST, path, controller);
	}

	public Node delete(String path, BiConsumer<HttpRequest, HttpResponse> controller){
		return route(HttpVerb.DELETE, path, controller);
	}

	public Node patch(String path, BiConsumer<HttpRequest, HttpResponse> controller){
		return route(HttpVerb.PATCH, path, controller);
	}

	//endregion
}
