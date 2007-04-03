package gov.nih.nci.cabig.ctms;

/**
 * @author Rhett Sutphin
 */
/* TODO: move to core module */
public class CommonsSystemException extends RuntimeException {
    public CommonsSystemException(String message) {
        super(message);
    }

    public CommonsSystemException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommonsSystemException(Throwable cause) {
        super(cause);
    }
}
