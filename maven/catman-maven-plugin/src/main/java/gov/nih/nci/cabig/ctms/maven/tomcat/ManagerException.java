package gov.nih.nci.cabig.ctms.maven.tomcat;

/**
 * @author Rhett Sutphin
 */
public class ManagerException extends RuntimeException {
    public ManagerException(String message) {
        super(message);
    }

    public ManagerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ManagerException(Throwable cause) {
        super(cause);
    }
}
