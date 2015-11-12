package black.door.node.java.api;

import black.door.node.java.Router;
import black.door.node.java.exception.WrappedException;
import black.door.node.java.loops.RequestLoop;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by nfischer on 11/1/2015.
 */
public class Server implements Runnable{

	private Router router;
	private int port;
	private AtomicBoolean running = new AtomicBoolean(false);
	private RequestLoop requestLoop;

	protected Server(int port, Router router){
		this.setPort(port);
		this.setRouter(router);
	}

	public AtomicBoolean getRunning() {
		return running;
	}

	public int getPort() {
		return port;
	}

	protected Server setPort(int port) {
		this.port = port;
		return this;
	}

	public Router getRouter() {
		return router;
	}

	protected Server setRouter(Router router) {
		this.router = router;
		return this;
	}

	public void stop() throws IOException {
		this.running.set(false);
		this.requestLoop.close();
	}

	@Override
	public void run() {
		try {
			this.requestLoop = new RequestLoop(this);
		} catch (IOException e) {
			throw new WrappedException(e);
		}
		this.running.set(true);
		this.requestLoop.run();
	}
}
