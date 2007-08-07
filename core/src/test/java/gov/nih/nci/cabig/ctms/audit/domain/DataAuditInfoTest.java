package gov.nih.nci.cabig.ctms.audit.domain;

import java.util.Date;

import junit.framework.TestCase;

/**
 * @author Saurabh Agrawal
 */
public class DataAuditInfoTest extends TestCase {
	public void testCopySelfclass() throws Exception {
		DataAuditInfo source = new DataAuditInfo("jim", "127", new Date(), "/where/it/is");
		DataAuditInfo copy = DataAuditInfo.copy(source);
		assertEquals("Wrong username in copy", source.getBy(), copy.getUsername());
		assertEquals("Wrong ip in copy", source.getIp(), copy.getIp());
		assertEquals("Wrong date in copy", source.getOn(), copy.getTime());
		assertEquals("Wrong url in copy", source.getUrl(), copy.getUrl());
	}

	public void testCopySuperclass() throws Exception {
		gov.nih.nci.cabig.ctms.audit.DataAuditInfo source = new gov.nih.nci.cabig.ctms.audit.DataAuditInfo("joe", "15",
				new Date());
		DataAuditInfo copy = DataAuditInfo.copy(source);
		assertEquals("Wrong username in copy", source.getBy(), copy.getUsername());
		assertEquals("Wrong ip in copy", source.getIp(), copy.getIp());
		assertEquals("Wrong date in copy", source.getOn(), copy.getTime());
		assertNull("Unexpected URL in copy", copy.getUrl());
	}
}
