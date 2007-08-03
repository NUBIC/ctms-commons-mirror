package gov.nih.nci.cabig.ctms.maven.tomcat;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import com.pyx4j.log4j.MavenLogAppender;

/**
 * @author Rhett Sutphin
 */
public abstract class AbstractWebappMojo extends AbstractMojo {
    /**
     * Application path to manipulate
     *
     * @parameter expression="/${project.build.finalName}"
     */
    private String path;

    public final void execute() throws MojoExecutionException, MojoFailureException {
        MavenLogAppender.startPluginLog(this);
        try {
            executeInternal();
        } finally {
           MavenLogAppender.endPluginLog(this);
        }
    }

    protected abstract void executeInternal() throws MojoExecutionException, MojoFailureException;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
