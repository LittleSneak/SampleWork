package graph;
/**
 * 
 */

/**
 * @author alexfalconer-athanassakos
 *
 */
public class NoSuchNodeException extends Exception {

	/**
	 * serialVersionUID generated serial identifier.
	 */
	private static final long serialVersionUID = 1L;

	/** Creates a new NoSuchNodeException. */
	public NoSuchNodeException() {
		super(); //call superclass constructor.
	}

	/** Creates a new NoSuchNodeException.
	 * @param message message message detailing the exception.
	 */
	public NoSuchNodeException(String message) {
		super(message);  //call superclass constructor.
	}

}
