package black.door.node.java.loops;

import black.door.node.java.exception.WrappedException;
import black.door.node.java.http.HttpResponse;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by nfischer on 10/30/2015.
 */
public enum ResponseLoop {
	INST;
	private ExecutorService executorService;

	ResponseLoop(){
		executorService = Executors.newWorkStealingPool();
	}

	public static void sendResponse(HttpResponse response){
		INST.executorService.submit(() -> {
			try {
				response.sendResponse();
			} catch (IOException e) {
				throw new WrappedException(e);
			}
		});
	}
}
