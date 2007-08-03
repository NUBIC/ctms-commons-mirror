package gov.nih.nci.cabig.ctms.maven.tomcat;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.io.FileWriter;

/**
 * @author Rhett Sutphin
 * @goal create-context
 */
public class CreateContextMojo extends AbstractWebappMojo {
    /**
     * Absolute path to the context XML file to use when doing local deployments
     *
     * @parameter default-value="${basedir}/target/local-tomcat/context.xml"
     */
    private File contextFile;

    /**
     * @parameter default-value="${basedir}/src/main/webapp"
     */
    private File docBase;

    @Override
    protected void executeInternal() throws MojoExecutionException, MojoFailureException {
        String context = createContextXml();
        getLog().debug("Will write context XML as:\n  " + context);
        ensureContextDirectoryExists();
        writeContext(context);
    }

    private String createContextXml() throws MojoExecutionException {
        String canonicalDocBase;
        try {
            canonicalDocBase = getDocBase().getCanonicalPath();
        } catch (IOException e) {
            throw new MojoExecutionException("Could not canonicalize " + getDocBase(), e);
        }
        getLog().debug("canonical docBase = " + canonicalDocBase);

        return String.format(
            "<Context path=\"%s\" docBase=\"%s\" debug=\"9\" />",
            getPath(), canonicalDocBase
        );
    }

    private void ensureContextDirectoryExists() throws MojoFailureException {
        File contextDir = contextFile.getParentFile();
        if (!contextDir.exists()) {
            getLog().debug("Creating " + contextDir);
            if (!contextDir.mkdirs()) {
                throw new MojoFailureException("Could not create directory " + contextDir + " for " + contextFile);
            }
        }
    }

    private void writeContext(String content) throws MojoExecutionException {
        try {
            getLog().debug("Writing context XML to " + contextFile);
            Writer fw = new FileWriter(contextFile, false);
            fw.write(content);
            fw.close();
        } catch (IOException e) {
            throw new MojoExecutionException("Could not write to " + contextFile, e);
        }
    }

    ////// BEAN PROPERTIES

    public File getContextFile() {
        return contextFile;
    }

    public void setContextFile(File contextFile) {
        this.contextFile = contextFile;
    }

    public File getDocBase() {
        return docBase;
    }

    public void setDocBase(File docBase) {
        this.docBase = docBase;
    }
}
