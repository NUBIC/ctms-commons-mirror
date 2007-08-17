package gov.nih.nci.cabig.ctms;

/**
 * @author Rhett Sutphin
 */
public class CommonsSystemException extends RuntimeException {
    public CommonsSystemException(String message, Object... messageFormats) {
        super(String.format(message, messageFormats));
    }

    public CommonsSystemException(String message, Throwable cause, Object... messsageFormats) {
        super(String.format(message, messsageFormats), cause);
    }

    public CommonsSystemException(Throwable cause) {
        super(cause);
    }
}
