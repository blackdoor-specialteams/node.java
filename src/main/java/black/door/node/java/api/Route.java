package black.door.node.java.api;

import black.door.node.java.http.HttpRequest;
import black.door.node.java.http.HttpResponse;
import black.door.node.java.http.HttpVerb;

import java.util.function.BiConsumer;
import java.util.regex.Pattern;

/**
 * Created by nfischer on 11/8/2015.
 */
public class Route implements Comparable<Route>{
	private Pattern path;
	private BiConsumer<HttpRequest, HttpResponse> controller;
	private HttpVerb method;

	public Route(HttpVerb method, Pattern path, BiConsumer<HttpRequest, HttpResponse> controller) {
		this.path = path;
		this.controller = controller;
		this.method = method;
	}

	public Pattern getPath() {
		return path;
	}

	protected Route setPath(Pattern path) {
		this.path = path;
		return this;
	}

	public BiConsumer<HttpRequest, HttpResponse> getController() {
		return controller;
	}

	protected Route setController(BiConsumer<HttpRequest, HttpResponse> controller) {
		this.controller = controller;
		return this;
	}

	public HttpVerb getMethod() {
		return method;
	}

	protected Route setMethod(HttpVerb method) {
		this.method = method;
		return this;
	}

	@Override
	public int compareTo(Route o) {
		int ret = this.getMethod().compareTo(o.getMethod());
		if(ret != 0)
			return ret;

		return this.getPath().pattern().compareTo(o.getPath().pattern());
	}
}
