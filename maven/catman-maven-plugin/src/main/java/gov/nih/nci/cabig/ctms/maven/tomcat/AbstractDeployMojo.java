package gov.nih.nci.cabig.ctms.maven.tomcat;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;

/**
 * @author Rhett Sutphin
 */
public abstract class AbstractDeployMojo extends AbstractManagerMojo {
    /**
     * Absolute path to the context XML file to use when doing local deployments
     *
     * @parameter default-value="${basedir}/target/local-tomcat/context.xml"
     */
    private File contextFile;

    protected void deploy() throws MojoFailureException, MojoExecutionException {
        executeWithManager(new Command() {
            public ManagerResponse<?> execute(TomcatManager manager) { return manager.deploy(getPath(), getContextFile()); }
        });
    }

    public File getContextFile() {
        return contextFile;
    }

    public void setContextFile(File contextFile) {
        this.contextFile = contextFile;
    }
}
