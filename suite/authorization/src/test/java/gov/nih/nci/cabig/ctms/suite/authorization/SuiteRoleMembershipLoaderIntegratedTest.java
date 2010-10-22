package gov.nih.nci.cabig.ctms.suite.authorization;

import java.util.Map;

/**
 * @author Rhett Sutphin
 */
public class SuiteRoleMembershipLoaderIntegratedTest extends IntegratedTestCase {
    private SuiteRoleMembershipLoader helper;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        helper = new SuiteRoleMembershipLoader();
        helper.setAuthorizationManager(getAuthorizationManager());
    }

    public void testAllRoleMembershipsReturned() throws Exception {
        assertEquals(3, helper.getRoleMemberships(-22).size());
    }

    public void testIncompleteRoleMembershipsExcludedByDefault() throws Exception {
        Map<SuiteRole, SuiteRoleMembership> actual = helper.getRoleMemberships(-26);
        assertEquals(1, actual.size());
        assertTrue("Wrong role present", actual.containsKey(SuiteRole.BUSINESS_ADMINISTRATOR));
    }

    public void testIncompleteRoleMembershipsIncludedOnRequest() throws Exception {
        Map<SuiteRole, SuiteRoleMembership> actual = helper.getProvisioningRoleMemberships(-26);
        assertEquals(3, actual.size());
        assertTrue("Missing expected complete role", actual.containsKey(SuiteRole.BUSINESS_ADMINISTRATOR));
        assertTrue("Missing expected incomplete role", actual.containsKey(SuiteRole.STUDY_CREATOR));
        assertTrue("Missing expected incomplete role", actual.containsKey(SuiteRole.STUDY_CALENDAR_TEMPLATE_BUILDER));
    }

    public void testGlobalRoleMembershipCorrectlyConstructed() throws Exception {
        SuiteRoleMembership actual = helper.getRoleMemberships(-22).get(SuiteRole.DATA_IMPORTER);
        assertNotNull(actual);
        assertFalse(actual.isAllSites());
        assertFalse(actual.isAllStudies());
        assertEquals(0, actual.getSiteIdentifiers().size());
        assertEquals(0, actual.getStudyIdentifiers().size());
    }

    public void testSiteSpecificRoleMembershipCorrectlyConstructed() throws Exception {
        SuiteRoleMembership actual = helper.getRoleMemberships(-22).get(SuiteRole.USER_ADMINISTRATOR);
        assertNotNull(actual);
        assertFalse(actual.isAllSites());
        assertFalse(actual.isAllStudies());
        assertEquals(1, actual.getSiteIdentifiers().size());
        assertEquals("MI001", actual.getSiteIdentifiers().get(0));
        assertEquals(0, actual.getStudyIdentifiers().size());
    }

    public void testSiteAndStudySpecificRoleMembershipCorrectlyConstructed() throws Exception {
        SuiteRoleMembership actual = helper.getRoleMemberships(-22).get(SuiteRole.DATA_READER);
        assertNotNull(actual);
        assertFalse(actual.isAllSites());
        assertTrue(actual.isAllStudies());
        assertEquals(1, actual.getSiteIdentifiers().size());
        assertEquals("MI001", actual.getSiteIdentifiers().get(0));
    }
}
