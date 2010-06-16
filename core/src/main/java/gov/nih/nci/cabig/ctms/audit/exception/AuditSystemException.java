package gov.nih.nci.cabig.ctms.audit.exception;

/**
 * Exception for unexpected, unhandlable runtime conditions while audting. E.g,
 * an InvocationTargetException when it is not expected that the invoked method
 * will throw an exception.
 *
 * @author Saurabh Agrawal
 */
public class AuditSystemException extends RuntimeException {

    /**
     * Instantiates a new audit system exception.
     *
     * @param message
     *            the message
     */
    public AuditSystemException(String message) {
        super(message);
    }

    /**
     * Instantiates a new audit system exception.
     *
     * @param message
     *            the message
     * @param cause
     *            the cause
     */
    public AuditSystemException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new audit system exception.
     *
     * @param cause
     *            the cause
     */
    public AuditSystemException(Throwable cause) {
        super(cause);
    }
}
