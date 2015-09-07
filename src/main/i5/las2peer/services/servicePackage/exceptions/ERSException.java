/**
 * 
 */
package i5.las2peer.services.servicePackage.exceptions;

/**
 * A custom exception that will be used across this service.
 * 
 * @author sathvik
 *
 */
public class ERSException extends Exception {
    /**
     * A generated serialVersionUID.
     */
    private static final long serialVersionUID = -1014799182429065646L;

    public ERSException() {

    }

    public ERSException(String message) {
	super(message);
    }

    public ERSException(Throwable cause) {
	super(cause);
    }

    public ERSException(String message, Throwable cause) {
	super(message, cause);
    }
}

