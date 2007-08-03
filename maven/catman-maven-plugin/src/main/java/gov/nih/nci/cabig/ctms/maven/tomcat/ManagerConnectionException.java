package gov.nih.nci.cabig.ctms.maven.tomcat;

/**
 * @author Rhett Sutphin
 */
public class ManagerConnectionException extends ManagerException {
    public ManagerConnectionException(String message) {
        super(message);
    }

    public ManagerConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ManagerConnectionException(Throwable cause) {
        super(cause);
    }
}
