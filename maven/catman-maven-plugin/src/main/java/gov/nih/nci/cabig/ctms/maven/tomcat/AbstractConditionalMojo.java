package gov.nih.nci.cabig.ctms.maven.tomcat;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.MojoExecutionException;

import java.util.List;

/**
 * @author Rhett Sutphin
 */
public abstract class AbstractConditionalMojo extends AbstractDeployMojo {
    protected WebApplication findThisApplication() throws MojoExecutionException, MojoFailureException {
        for (WebApplication deployedApp : list()) {
            if (deployedApp.getDeployedPath().equals(getPath())) return deployedApp;
        }
        return null;
    }
}
