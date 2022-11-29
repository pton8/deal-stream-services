package main.java.org.hearst.exception;

public class DealStreamServiceException extends Exception {

    private static final long serialVersionUID = 1L;

    public DealStreamServiceException() {
        super();
    }

    public DealStreamServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the detail message
     */
    public DealStreamServiceException(String message) {
        super(message);
    }

    public DealStreamServiceException(Throwable cause) {
        super(cause);
    }
}
