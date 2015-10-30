package black.door.node.java.exception;

/**
 * Created by nfischer on 10/29/2015.
 */
public class WrappedException extends RuntimeException {
	public WrappedException(Exception e){
		super(e);
	}
	public void reThrow() throws Exception {
		if(this.getCause() instanceof Error)
			throw (Error) this.getCause();
		if(this.getCause() instanceof RuntimeException)
			throw (RuntimeException) this.getCause();
		throw (Exception) this.getCause();
	}
}
