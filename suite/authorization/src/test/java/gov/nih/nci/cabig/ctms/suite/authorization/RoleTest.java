package gov.nih.nci.cabig.ctms.suite.authorization;

import junit.framework.TestCase;

/**
 * @author Rhett Sutphin
 */
public class RoleTest extends TestCase {
    public void testDisplayNameForDefaultCase() throws Exception {
        assertEquals("System Administrator", Role.SYSTEM_ADMINISTRATOR.getDisplayName());
    }

    public void testDisplayNameWhenOverridden() {
        assertEquals("AE Rule and Report Manager", Role.AE_RULE_AND_REPORT_MANAGER.getDisplayName());
    }

    public void testCsmName() throws Exception {
        assertEquals("system_administrator", Role.SYSTEM_ADMINISTRATOR.getCsmName());
    }

    public void testGetByCsmName() throws Exception {
        assertEquals(Role.STUDY_QA_MANAGER, Role.getByCsmName("study_qa_manager"));
    }

    public void testGetByCsmNameThrowsExceptionWhenUnknown() throws Exception {
        try {
            Role.getByCsmName("study_qa_mage");
            fail("Exception ont thrown");
        } catch (IllegalArgumentException iae) {
            assertEquals("Wrong message",
                "There is no suite role with the CSM name study_qa_mage", iae.getMessage());
        }
    }

    public void testDescription() throws Exception {
        assertEquals("Creates, manages, imports AE rules and AE report definitions.",
            Role.AE_RULE_AND_REPORT_MANAGER.getDescription());
    }

    public void testAllRolesHaveDescriptions() throws Exception {
        for (Role role : Role.values()) {
            assertNotNull(role + " is missing a description", role.getDescription());
        }
    }

    public void testScopeForUnscopedRole() throws Exception {
        assertFalse("Shouldn't be study scoped", Role.SYSTEM_ADMINISTRATOR.isStudyScoped());
        assertFalse("Shouldn't be site scoped", Role.SYSTEM_ADMINISTRATOR.isSiteScoped());
        assertFalse("Shouldn't be scoped", Role.SYSTEM_ADMINISTRATOR.isScoped());
    }

    public void testScopeForSiteScopedRole() throws Exception {
        assertFalse("Shouldn't be study scoped", Role.SUBJECT_MANAGER.isStudyScoped());
        assertTrue("Should be site scoped", Role.SUBJECT_MANAGER.isSiteScoped());
        assertTrue("Should be scoped", Role.SUBJECT_MANAGER.isScoped());
    }

    public void testScopeForStudyScopedRole() throws Exception {
        assertTrue("Should be study scoped", Role.AE_REPORTER.isStudyScoped());
        assertTrue("Should be site scoped", Role.AE_REPORTER.isSiteScoped());
        assertTrue("Should be scoped", Role.AE_REPORTER.isScoped());
    }

    public void testScopeSetIsImmutable() throws Exception {
        try {
            Role.DATA_READER.getScopes().remove(ScopeType.SITE);
            fail("Exception not thrown");
        } catch (UnsupportedOperationException uoe) {
            // good
        }
    }
}
