package gov.nih.nci.cabig.ctms.maven.tomcat;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @author Rhett Sutphin
 * @goal start
 */
public class StartMojo extends AbstractManagerMojo {
    @Override
    protected void executeInternal() throws MojoExecutionException, MojoFailureException {
        start();
    }
}
