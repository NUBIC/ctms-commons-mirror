package gov.nih.nci.cabig.ctms.tools;

import gov.nih.nci.cabig.ctms.lang.DateTools;
import junit.framework.TestCase;

import java.util.Calendar;

/**
 * @author Rhett Sutphin
 */
public class BuildInfoTest extends TestCase {
    private BuildInfo buildInfo = new BuildInfo();
    
    public void testIsSnapshotWhenItIs() throws Exception {
        buildInfo.setVersionNumber("0.7-SNAPSHOT");
        assertTrue(buildInfo.isSnapshot());
    }
    
    public void testIsSnapshotWhenItIsNot() throws Exception {
        buildInfo.setVersionNumber("0.7");
        assertFalse(buildInfo.isSnapshot());
    }
    
    public void testIsSnapshotForDev() throws Exception {
        buildInfo.setVersionNumber("0.7.1.DEV");
        assertTrue(buildInfo.isSnapshot());
    }

    public void testIsSnapshotWhenNullVersion() throws Exception {
        buildInfo.setVersionNumber(null);
        assertTrue(buildInfo.isSnapshot());
    }
    
    private void addAllFields() {
        buildInfo.setApplicationName("CTMS-o");
        buildInfo.setHostname("localhost");
        buildInfo.setVersionNumber("1.22-SNAPSHOT");
        buildInfo.setUsername("builder");
        buildInfo.setTimestamp(DateTools.createDate(2007, Calendar.FEBRUARY, 5, 13, 18, 8));
    }

    public void testReleaseBuildName() throws Exception {
        addAllFields();
        buildInfo.setVersionNumber("1.28");
        assertEquals("CTMS-o v. 1.28", buildInfo.getBuildName());
    }

    public void testSnapshotBuildName() throws Exception {
        addAllFields();
        assertEquals("CTMS-o v. 1.22-SNAPSHOT (builder@localhost 2007-02-05 13:18:08)", buildInfo.getBuildName());
    }

    public void testSnapshotBuildNameNoAppName() throws Exception {
        addAllFields();
        buildInfo.setApplicationName(null);
        assertEquals("Untitled CTMS application v. 1.22-SNAPSHOT (builder@localhost 2007-02-05 13:18:08)", buildInfo.getBuildName());
    }

    public void testSnapshotBuildNameNoVersion() throws Exception {
        addAllFields();
        buildInfo.setVersionNumber(null);
        assertEquals("CTMS-o [unknown SNAPSHOT] (builder@localhost 2007-02-05 13:18:08)", buildInfo.getBuildName());
    }

    public void testSnapshotBuildNameNoUser() throws Exception {
        addAllFields();
        buildInfo.setUsername(null);
        assertEquals("CTMS-o v. 1.22-SNAPSHOT (localhost 2007-02-05 13:18:08)", buildInfo.getBuildName());
    }

    public void testSnapshotBuildNameNoHostname() throws Exception {
        addAllFields();
        buildInfo.setHostname(null);
        assertEquals("CTMS-o v. 1.22-SNAPSHOT (builder 2007-02-05 13:18:08)", buildInfo.getBuildName());
    }

    public void testSnapshotBuildNameNoTimestamp() throws Exception {
        addAllFields();
        buildInfo.setTimestamp(null);
        assertEquals("CTMS-o v. 1.22-SNAPSHOT (builder@localhost [unknown time])", buildInfo.getBuildName());
    }

    public void testToStringIsBuildName() throws Exception {
        addAllFields();
        buildInfo.setVersionNumber("3.9.0");
        assertEquals("CTMS-o v. 3.9.0", buildInfo.toString());
    }
}
