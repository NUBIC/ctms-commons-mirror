package gov.nih.nci.cabig.ctms.maven.tomcat;

import junit.framework.TestCase;

import java.io.File;
import java.io.Reader;
import java.io.FileReader;
import java.io.FileNotFoundException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.FileUtils;

/**
 * @author Rhett Sutphin
 */
public class CreateContextMojoTest extends TestCase {
    private CreateContextMojo mojo;
    private File contextFile;
    private File docBase;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        contextFile = File.createTempFile("context-", "");
        docBase = File.createTempFile("docBase-", "");

        mojo = new CreateContextMojo();
        mojo.setContextFile(contextFile);
        mojo.setDocBase(docBase);
        mojo.setPath("/widget");
    }

    @Override
    protected void tearDown() throws Exception {
        contextFile.delete();
        docBase.delete();
        super.tearDown();
    }

    public void testContextCreated() throws Exception {
        contextFile.delete();
        assertFalse("Test setup problem -- contextFile shouldn't exist", contextFile.exists());

        mojo.execute();
        assertTrue("Context file not created", contextFile.exists());

        String contents = FileUtils.readFileToString(contextFile);
        assertEquals("Wrong context",
            String.format("<Context path=\"/widget\" docBase=\"%s\" debug=\"9\" />", docBase.getCanonicalPath()),
            contents.trim()
        );
    }
}
