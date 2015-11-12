package black.door.node.java;

import black.door.node.java.api.Route;
import black.door.node.java.http.HttpRequest;
import black.door.node.java.http.HttpResponse;
import black.door.node.java.http.HttpVerb;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;

/**
 * Created by nfischer on 10/29/2015.
 */
public class Router {
	private Map<HttpVerb, Set<Route>> routes;

	public Router(){
		routes = new ConcurrentSkipListMap<>();
		for(HttpVerb method : HttpVerb.values()){
			routes.put(method, new ConcurrentSkipListSet<>());
		}
	}

	public Route get(HttpVerb method, String path){
		return routes.get(method).stream()
				.filter(r -> r.getPath().matcher(path).matches())
				.findFirst()
				.orElse(null);
	}

	public Router add(Route route){
		routes.get(route.getMethod())
				.add(route);
		return this;
	}

}
