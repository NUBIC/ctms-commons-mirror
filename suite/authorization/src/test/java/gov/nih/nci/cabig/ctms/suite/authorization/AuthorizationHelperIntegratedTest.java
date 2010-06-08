package gov.nih.nci.cabig.ctms.suite.authorization;

import java.util.Map;

/**
 * @author Rhett Sutphin
 */
public class AuthorizationHelperIntegratedTest extends IntegratedTestCase {
    private AuthorizationHelper helper;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        helper = new AuthorizationHelper();
        helper.setAuthorizationManager(getAuthorizationManager());
    }

    public void testAllRoleMembershipsReturned() throws Exception {
        assertEquals(3, helper.getRoleMemberships(-22).size());
    }

    public void testInvalidRoleMembershipsExcluded() throws Exception {
        Map<SuiteRole, SuiteRoleMembership> actual = helper.getRoleMemberships(-26);
        assertEquals(1, actual.size());
        assertTrue("Wrong role present", actual.containsKey(SuiteRole.BUSINESS_ADMINISTRATOR));
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
