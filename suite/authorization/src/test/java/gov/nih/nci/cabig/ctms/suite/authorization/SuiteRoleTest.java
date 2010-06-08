package gov.nih.nci.cabig.ctms.suite.authorization;

import junit.framework.TestCase;

/**
 * @author Rhett Sutphin
 */
public class SuiteRoleTest extends TestCase {
    public void testDisplayNameForDefaultCase() throws Exception {
        assertEquals("System Administrator", SuiteRole.SYSTEM_ADMINISTRATOR.getDisplayName());
    }

    public void testDisplayNameWhenOverridden() {
        assertEquals("AE Rule and Report Manager", SuiteRole.AE_RULE_AND_REPORT_MANAGER.getDisplayName());
    }

    public void testCsmName() throws Exception {
        assertEquals("system_administrator", SuiteRole.SYSTEM_ADMINISTRATOR.getCsmName());
    }

    public void testGetByCsmName() throws Exception {
        assertEquals(SuiteRole.STUDY_QA_MANAGER, SuiteRole.getByCsmName("study_qa_manager"));
    }

    public void testGetByCsmNameThrowsExceptionWhenUnknown() throws Exception {
        try {
            SuiteRole.getByCsmName("study_qa_mage");
            fail("Exception ont thrown");
        } catch (IllegalArgumentException iae) {
            assertEquals("Wrong message",
                "There is no suite role with the CSM name study_qa_mage", iae.getMessage());
        }
    }

    public void testDescription() throws Exception {
        assertEquals("Creates, manages, imports AE rules and AE report definitions.",
            SuiteRole.AE_RULE_AND_REPORT_MANAGER.getDescription());
    }

    public void testAllRolesHaveDescriptions() throws Exception {
        for (SuiteRole role : SuiteRole.values()) {
            assertNotNull(role + " is missing a description", role.getDescription());
        }
    }

    public void testScopeForUnscopedRole() throws Exception {
        assertFalse("Shouldn't be study scoped", SuiteRole.SYSTEM_ADMINISTRATOR.isStudyScoped());
        assertFalse("Shouldn't be site scoped", SuiteRole.SYSTEM_ADMINISTRATOR.isSiteScoped());
        assertFalse("Shouldn't be scoped", SuiteRole.SYSTEM_ADMINISTRATOR.isScoped());
    }

    public void testScopeForSiteScopedRole() throws Exception {
        assertFalse("Shouldn't be study scoped", SuiteRole.SUBJECT_MANAGER.isStudyScoped());
        assertTrue("Should be site scoped", SuiteRole.SUBJECT_MANAGER.isSiteScoped());
        assertTrue("Should be scoped", SuiteRole.SUBJECT_MANAGER.isScoped());
    }

    public void testScopeForStudyScopedRole() throws Exception {
        assertTrue("Should be study scoped", SuiteRole.AE_REPORTER.isStudyScoped());
        assertTrue("Should be site scoped", SuiteRole.AE_REPORTER.isSiteScoped());
        assertTrue("Should be scoped", SuiteRole.AE_REPORTER.isScoped());
    }

    public void testScopeSetIsImmutable() throws Exception {
        try {
            SuiteRole.DATA_READER.getScopes().remove(ScopeType.SITE);
            fail("Exception not thrown");
        } catch (UnsupportedOperationException uoe) {
            // good
        }
    }
}
