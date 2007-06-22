package gov.nih.nci.cabig.ctms;

/**
 * @author Rhett Sutphin
 */
public class CommonsError extends Error {
    public CommonsError(String message) {
        super(message);
    }

    public CommonsError(String message, Throwable cause) {
        super(message, cause);
    }
}
