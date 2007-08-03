package gov.nih.nci.cabig.ctms.maven.tomcat;

/**
 * @author Rhett Sutphin
*/
public class WebApplication {
    private String deployedPath, statusText, filePath;
    private Integer sessionCount;

    public WebApplication(String deployedPath, String statusText, Integer sessions, String filePath) {
        this.deployedPath = deployedPath;
        this.statusText = statusText;
        this.sessionCount = sessions;
        this.filePath = filePath;
    }

    ////// LOGIC

    public boolean isRunning() {
        return "running".equals(getStatusText());
    }

    public boolean isStopped() {
        return !isRunning();
    }

    ////// BEAN ACCESSORS

    public String getDeployedPath() {
        return deployedPath;
    }

    public String getStatusText() {
        return statusText;
    }

    public String getFilePath() {
        return filePath;
    }

    public Integer getSessionCount() {
        return sessionCount;
    }
}
