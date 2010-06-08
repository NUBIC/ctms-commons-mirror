package gov.nih.nci.cabig.ctms.suite.authorization;

import gov.nih.nci.cabig.ctms.CommonsSystemException;

/**
 * A runtime exception for encapsulating CSM API errors.
 *
 * @author Rhett Sutphin
 */
public class SuiteAuthorizationProvisioningFailure extends CommonsSystemException {
    public SuiteAuthorizationProvisioningFailure(String message, Object... messageFormats) {
        super(message, messageFormats);
    }

    public SuiteAuthorizationProvisioningFailure(String message, Throwable cause, Object... messsageFormats) {
        super(message, cause, messsageFormats);
    }

    public SuiteAuthorizationProvisioningFailure(Throwable cause) {
        super(cause);
    }
}
