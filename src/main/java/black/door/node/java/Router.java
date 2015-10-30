package black.door.node.java;

import black.door.node.java.http.HttpRequest;
import black.door.node.java.http.HttpResponse;
import black.door.node.java.http.HttpVerb;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.function.BiConsumer;

/**
 * Created by nfischer on 10/29/2015.
 */
public class Router {
	public static BiConsumer<HttpRequest, HttpResponse> getController(HttpRequest request){
		throw new NotImplementedException();
	}
}
