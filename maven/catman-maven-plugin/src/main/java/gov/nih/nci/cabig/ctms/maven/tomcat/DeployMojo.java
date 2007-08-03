package gov.nih.nci.cabig.ctms.maven.tomcat;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @author Rhett Sutphin
 * @goal deploy
 */
public class DeployMojo extends AbstractDeployMojo {
    @Override
    public void executeInternal() throws MojoExecutionException, MojoFailureException {
        deploy();
    }
}
