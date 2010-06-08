package gov.nih.nci.cabig.ctms.suite.authorization;

import gov.nih.nci.cabig.ctms.CommonsSystemException;

/**
 * Indicates an error accessing some authorization element.
 *
 * @author Rhett Sutphin
 */
public class SuiteAuthorizationAccessException extends CommonsSystemException {
    public SuiteAuthorizationAccessException(String message, Object... messageFormats) {
        super(message, messageFormats);
    }

    public SuiteAuthorizationAccessException(String message, Throwable cause, Object... messsageFormats) {
        super(message, cause, messsageFormats);
    }

    public SuiteAuthorizationAccessException(Throwable cause) {
        super(cause);
    }
}
