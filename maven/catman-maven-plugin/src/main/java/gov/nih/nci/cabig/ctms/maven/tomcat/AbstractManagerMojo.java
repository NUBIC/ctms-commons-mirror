package gov.nih.nci.cabig.ctms.maven.tomcat;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.util.List;

/**
 * @author Rhett Sutphin
 */
public abstract class AbstractManagerMojo extends AbstractWebappMojo {
    /**
     * @parameter default="http://localhost:8080/manager"
     */
    private String url;

    /**
     * Username for accessing the manager application
     *
     * @parameter
     * @required
     */
    private String username;

    /**
     * Password for accessing the manager application
     *
     * @parameter
     * @required
     */
    private String password;

    private TomcatManager manager;

    ////// SIMPLE EXECUTIONS

    protected <P> ManagerResponse<P> executeWithManager(Command<P> exec) throws MojoFailureException, MojoExecutionException {
        try {
            ManagerResponse<P> response = exec.execute(getTomcatManager());
            handleResponse(response);
            return response;
        } catch (ManagerException e) {
            throw new MojoExecutionException("Error while executing manager command -- " + e.getMessage(), e);
        }
    }

    protected void handleResponse(ManagerResponse<?> response) throws MojoFailureException {
        if (response.isOK()) {
            getLog().info(response.getStatusLine());
        } else {
            throw new MojoFailureException("Command failed.  Tomcat manager said:  '" + response.getStatusLine() + '\'');
        }
    }

    protected void undeploy() throws MojoFailureException, MojoExecutionException {
        executeWithManager(new Command<String>() {
            public ManagerResponse<String> execute(TomcatManager manager) { return manager.undeploy(getPath()); }
        });
    }

    protected void reload() throws MojoFailureException, MojoExecutionException {
        executeWithManager(new Command<String>() {
            public ManagerResponse<String> execute(TomcatManager manager) { return manager.reload(getPath()); }
        });
    }

    protected void start() throws MojoFailureException, MojoExecutionException {
        executeWithManager(new Command<String>() {
            public ManagerResponse<String> execute(TomcatManager manager) { return manager.start(getPath()); }
        });
    }

    protected void stop() throws MojoFailureException, MojoExecutionException {
        executeWithManager(new Command<String>() {
            public ManagerResponse<String> execute(TomcatManager manager) { return manager.stop(getPath()); }
        });
    }

    protected List<WebApplication> list() throws MojoFailureException, MojoExecutionException {
        return executeWithManager(new Command<List<WebApplication>>() {
            public ManagerResponse<List<WebApplication>> execute(TomcatManager manager) { return manager.list(); }
        }).getPayload();
    }

    protected static interface Command<T> {
        ManagerResponse<T> execute(TomcatManager manager);
    }

    //////

    private TomcatManager getTomcatManager() {
        if (manager == null) manager = new TomcatManager(getUrl(), getUsername(), getPassword());
        return manager;
    }

    // allow manager to be overridden for testing

    protected void setTomcatManager(TomcatManager tomcatManager) {
        this.manager = tomcatManager;
    }

    ////// BEAN ACCESSORS

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
