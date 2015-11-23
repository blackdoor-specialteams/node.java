package black.door.node.java.loops;

import black.door.node.java.exception.WrappedException;
import black.door.node.java.http.HttpContext;
import black.door.node.java.http.HttpResponse;
import org.javatuples.Pair;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Created by nfischer on 10/30/2015.
 */
public enum ResponseLoop {
	INST;
	private ExecutorService executorService;

	ResponseLoop(){
		executorService = BigPool.getExecutorService();
	}

	public static Future sendResponse(HttpResponse response, Socket sock){

		return INST.executorService.submit(() -> {
			try {
				OutputStream os = sock.getOutputStream();
				os.write(response.serialize());
				os.flush();
				//sock.close();
			} catch (IOException e) {
				throw new WrappedException(e);
			}
		});
	}
}
