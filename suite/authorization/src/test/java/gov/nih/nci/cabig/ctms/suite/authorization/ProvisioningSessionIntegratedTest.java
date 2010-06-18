package gov.nih.nci.cabig.ctms.suite.authorization;

import gov.nih.nci.cabig.ctms.suite.authorization.domain.TestSiteMapping;
import gov.nih.nci.cabig.ctms.suite.authorization.domain.TestStudyMapping;
import gov.nih.nci.security.authorization.domainobjects.Group;
import gov.nih.nci.security.authorization.domainobjects.Privilege;
import gov.nih.nci.security.authorization.domainobjects.ProtectionElementPrivilegeContext;
import gov.nih.nci.security.exceptions.CSObjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Rhett Sutphin
 */
public class ProvisioningSessionIntegratedTest extends IntegratedTestCase {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private ProvisioningSessionFactory psFactory;

    @Override
    protected void setUp() throws Exception {
        log.info("------ Starting test {} ------", getName());
        super.setUp();
        psFactory = new ProvisioningSessionFactory();
        psFactory.setAuthorizationManager(getAuthorizationManager());
        psFactory.setSiteMapping(new TestSiteMapping());
        psFactory.setStudyMapping(new TestStudyMapping());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        log.info("------  Ending  test {} ------", getName());
    }

    protected ProvisioningSession createSession(long userId) throws Exception {
        return psFactory.createSession(userId);
    }

    public void testDeleteExistingRoleDeletesUserPrivilegeMapping() throws Exception {
        SuiteRole existingRole = SuiteRole.USER_ADMINISTRATOR;
        assertUserHasPrivilege("Test setup failure", -22, "HealthcareSite.MI001", existingRole.getCsmName());

        createSession(-22).deleteRole(existingRole);
        assertUserDoesNotHavePrivilege("Role not removed", -22, "HealthcareSite.MI001", existingRole.getCsmName());
    }

    public void testDeleteExistingRoleDeletesUserGroupMapping() throws Exception {
        SuiteRole existingRole = SuiteRole.USER_ADMINISTRATOR;
        assertUserInGroup("Test setup failure", existingRole.getCsmName(), -22);

        createSession(-22).deleteRole(existingRole);
        assertUserNotInGroup("Membership not deleted", existingRole.getCsmName(), -22);
    }

    public void testDeleteNonExistentRoleDoesNothing() throws Exception {
        createSession(-22).deleteRole(SuiteRole.DATA_ANALYST);

        // expect no exceptions, plus
        assertUserInGroup("Unrelated group membership deleted",
            SuiteRole.USER_ADMINISTRATOR.getCsmName(), -22);
        assertUserHasPrivilege("Unrelated PE deleted", -22, "HealthcareSite.MI001",
            SuiteRole.USER_ADMINISTRATOR.getCsmName());
    }

    public void testDeleteDoesNotAffectOtherRoles() throws Exception {
        SuiteRole existingRole = SuiteRole.USER_ADMINISTRATOR;
        assertUserInGroup("Test setup failure", existingRole.getCsmName(), -22);

        createSession(-22).deleteRole(existingRole);

        assertUserHasPrivilege("Unrelated privilege removed", -22, "HealthcareSite.MI001", "data_reader");
        assertUserHasPrivilege("Unrelated privilege removed", -22, "Study", "data_reader");
        assertUserInGroup("Unrelated group removed", "data_importer", -22);
    }

    public void testReplaceRoleWhenNewScopedRole() throws Exception {
        createSession(-22).replaceRole(psFactory.createSuiteRoleMembership(SuiteRole.DATA_ANALYST).
            forAllSites().forStudies("CRM114"));

        assertUserInGroup("Not added to group", "data_analyst", -22);
        assertUserHasPrivilege("Not given study priv", -22, "Study.CRM114", "data_analyst");
        assertUserHasPrivilege("Not given site priv", -22, "HealthcareSite", "data_analyst");
    }

    public void testReplaceRoleWhenNewGlobalRole() throws Exception {
        createSession(-22).replaceRole(psFactory.createSuiteRoleMembership(SuiteRole.SYSTEM_ADMINISTRATOR));

        assertUserInGroup("Not added to group", "system_administrator", -22);
    }

    public void testReplaceRoleWhenIdentical() throws Exception {
        createSession(-22).replaceRole(psFactory.createSuiteRoleMembership(SuiteRole.DATA_READER).
            forAllStudies().forSites("MI001"));

        assertUserInGroup("Not still in group", "data_reader", -22);
        assertUserHasPrivilege("Not still with study priv", -22, "Study", "data_reader");
        assertUserHasPrivilege("Not still with site priv", -22, "HealthcareSite.MI001", "data_reader");
    }

    public void testReplaceWhenScopeExpanded() throws Exception {
        createSession(-22).replaceRole(
            psFactory.createSuiteRoleMembership(SuiteRole.USER_ADMINISTRATOR).forSites("IL033", "MI001"));

        assertUserInGroup("Not still in group", "user_administrator", -22);
        assertUserHasPrivilege("Original priv not preserved", -22, "HealthcareSite.MI001", "user_administrator");
        assertUserHasPrivilege("New priv not added", -22, "HealthcareSite.IL033", "user_administrator");
    }

    public void testReplaceWhenScopeChanged() throws Exception {
        createSession(-22).replaceRole(
            psFactory.createSuiteRoleMembership(SuiteRole.USER_ADMINISTRATOR).forAllSites());

        assertUserInGroup("Not still in group", "user_administrator", -22);
        assertUserDoesNotHavePrivilege("Original priv not removed", -22, "HealthcareSite.MI001", "user_administrator");
        assertUserHasPrivilege("New priv not added", -22, "HealthcareSite", "user_administrator");
    }

    public void testReplaceDoesNotAffectOtherRoles() throws Exception {
        assertUserInGroup("Test setup failure", "data_importer", -22);
        assertUserHasPrivilege("Test setup failure", -22, "HealthcareSite.MI001", "data_reader");
        assertUserHasPrivilege("Test setup failure", -22, "Study", "data_reader");

        createSession(-22).replaceRole(
            psFactory.createSuiteRoleMembership(SuiteRole.USER_ADMINISTRATOR).forAllSites());

        assertUserHasPrivilege("Unrelated privilege removed", -22, "HealthcareSite.MI001", "data_reader");
        assertUserHasPrivilege("Unrelated privilege removed", -22, "Study", "data_reader");
        assertUserInGroup("Unrelated group removed", "data_importer", -22);
    }

    public void testReplaceByModifiyingCurrent() throws Exception {
        ProvisioningSession session = createSession(-22);
        session.replaceRole(
            session.getProvisionableRoleMembership(SuiteRole.USER_ADMINISTRATOR).addSite("IL033"));

        assertUserInGroup("Not still in group", "user_administrator", -22);
        assertUserHasPrivilege("Original priv not preserved", -22, "HealthcareSite.MI001", "user_administrator");
        assertUserHasPrivilege("New priv not added", -22, "HealthcareSite.IL033", "user_administrator");
    }

    public void testReplaceFailsWhenNewMembershipIsInvalid() throws Exception {
        try {
            // extra scope
            createSession(-22).replaceRole(
                psFactory.createSuiteRoleMembership(SuiteRole.USER_ADMINISTRATOR).forAllStudies());
            fail("Exception not thrown");
        } catch (SuiteAuthorizationValidationException save) {
            // expected
        }
    }

    public void testReplaceDoesNotFailWhenNewMembershipIsIncomplete() throws Exception {
        createSession(-22).replaceRole(psFactory.createSuiteRoleMembership(SuiteRole.USER_ADMINISTRATOR));
        // no exception
        assertUserInGroup("Group not added", "user_administrator", -22);
    }

    public void testDeleteAfterReplaceNewWorks() throws Exception {
        ProvisioningSession session = psFactory.createSession(-26);
        session.replaceRole(psFactory.createSuiteRoleMembership(SuiteRole.STUDY_TEAM_ADMINISTRATOR).
            forAllSites().forAllStudies());
        session.deleteRole(SuiteRole.STUDY_TEAM_ADMINISTRATOR);

        assertUserNotInGroup("Group assoc not deleted", "study_team_administrator", -26);
        assertUserDoesNotHavePrivilege("Study priv not deleted", -26, "Study", "study_team_administrator");
        assertUserDoesNotHavePrivilege("Site priv not deleted", -26, "HealthcareSite", "study_team_administrator");
    }

    public void testReplaceSameAfterDeleteWorks() throws Exception {
        ProvisioningSession session = psFactory.createSession(-22);
        session.deleteRole(SuiteRole.USER_ADMINISTRATOR);
        session.replaceRole(psFactory.createSuiteRoleMembership(SuiteRole.USER_ADMINISTRATOR).
            forSites("MI001"));

        assertUserInGroup("User not restored to group", "user_administrator", -22);
        assertUserHasPrivilege("Site priv not restored", -22, "HealthcareSite.MI001", "user_administrator");
    }

    public void testGetProvisonableRoleMembershipReturnsBlankForUnknownRole() throws Exception {
        assertUserNotInGroup("Test setup failure", "data_analyst", -22);

        SuiteRoleMembership actual = psFactory.createSession(-22).
            getProvisionableRoleMembership(SuiteRole.DATA_ANALYST);
        assertEquals(SuiteRole.DATA_ANALYST, actual.getRole());
        assertNotNull(actual);
        assertFalse(actual.isAllSites());
        assertFalse(actual.isAllStudies());
        assertEquals(0, actual.getSiteIdentifiers().size());
        assertEquals(0, actual.getStudyIdentifiers().size());
    }

    public void testGlobalRoleMembershipCorrectlyConstructed() throws Exception {
        SuiteRoleMembership actual = psFactory.createSession(-22).
            getProvisionableRoleMembership(SuiteRole.DATA_IMPORTER);
        assertNotNull(actual);
        assertFalse(actual.isAllSites());
        assertFalse(actual.isAllStudies());
        assertEquals(0, actual.getSiteIdentifiers().size());
        assertEquals(0, actual.getStudyIdentifiers().size());
    }

    public void testSiteSpecificRoleMembershipCorrectlyConstructed() throws Exception {
        SuiteRoleMembership actual = psFactory.createSession(-22).
            getProvisionableRoleMembership(SuiteRole.USER_ADMINISTRATOR);
        assertNotNull(actual);
        assertFalse(actual.isAllSites());
        assertFalse(actual.isAllStudies());
        assertEquals(1, actual.getSiteIdentifiers().size());
        assertEquals("MI001", actual.getSiteIdentifiers().get(0));
        assertEquals(0, actual.getStudyIdentifiers().size());
    }

    public void testSiteAndStudySpecificRoleMembershipCorrectlyConstructed() throws Exception {
        SuiteRoleMembership actual = psFactory.createSession(-22).
            getProvisionableRoleMembership(SuiteRole.DATA_READER);
        assertNotNull(actual);
        assertFalse(actual.isAllSites());
        assertTrue(actual.isAllStudies());
        assertEquals(1, actual.getSiteIdentifiers().size());
        assertEquals("MI001", actual.getSiteIdentifiers().get(0));
    }

    public void testGetProvisionableRoleMembershipReturnsDifferentInstancesForSubsequentCalls() throws Exception {
        ProvisioningSession session = psFactory.createSession(-22);
        SuiteRoleMembership first = session.getProvisionableRoleMembership(SuiteRole.DATA_READER);
        SuiteRoleMembership second = session.getProvisionableRoleMembership(SuiteRole.DATA_READER);
        assertNotSame(first, second);
    }

    public void testGetProvisionableRoleMembershipDoesNotReturnInvalidMemberships() throws Exception {
        SuiteRoleMembership actual = psFactory.createSession(-26).
            getProvisionableRoleMembership(SuiteRole.DATA_IMPORTER);
        assertNotNull("Not returned", actual);
        assertFalse("Should be clean", actual.hasSiteScope());
        assertFalse("Should be clean", actual.hasStudyScope());
    }

    public void testGetProvisionableRoleMembershipReturnsIncompletePartiallyScopedMemberships() throws Exception {
        SuiteRoleMembership actual = psFactory.createSession(-26).
            getProvisionableRoleMembership(SuiteRole.STUDY_TEAM_ADMINISTRATOR);
        assertNotNull("Not returned", actual);
        assertFalse("Should be missing site scope", actual.hasSiteScope());
        assertTrue("Should have study scope", actual.hasStudyScope());
    }

    ////// ASSERTIONS

    private void assertUserHasPrivilege(
        String message, long userId, String peName, String privilegeName
    ) throws CSObjectNotFoundException {
        Collection<String> existing = findPrivilegeNamesForPE(userId, peName);
        assertTrue(message + ": user " + userId + " does not have " + privilegeName +
            " for " + peName + ". Only: " + existing + '.', 
            existing.contains(privilegeName));
    }

    private void assertUserDoesNotHavePrivilege(
        String message, long userId, String peName, String privilegeName
    ) throws CSObjectNotFoundException {
        Collection<String> existing = findPrivilegeNamesForPE(userId, peName);
        assertFalse(message + ": user " + userId + " incorrectly has " + privilegeName +
            " for " + peName + '.',
            existing.contains(privilegeName));
    }

    private void assertUserInGroup(
        String message, String expectedGroupName, long userId
    ) throws CSObjectNotFoundException {
        Set<String> actualGroupNames = getUserGroupNames(userId);
        assertTrue(message + ": user " + userId + " not in " + expectedGroupName +
            ".  Is in " + actualGroupNames + '.', actualGroupNames.contains(expectedGroupName));
    }

    private void assertUserNotInGroup(
        String message, String expectedGroupName, long userId
    ) throws CSObjectNotFoundException {
        Set<String> actualGroupNames = getUserGroupNames(userId);
        assertFalse(message + ": user " + userId + " in " + expectedGroupName +
            '.', actualGroupNames.contains(expectedGroupName));
    }

    @SuppressWarnings({ "unchecked" })
    private Set<String> getUserGroupNames(long userId) throws CSObjectNotFoundException {
        Set<Group> actualGroups = getAuthorizationManager().getGroups(Long.toString(userId));
        Set<String> actualGroupNames = new LinkedHashSet<String>();
        for (Group group : actualGroups) {
            actualGroupNames.add(group.getGroupName());
        }
        return actualGroupNames;
    }

    @SuppressWarnings({ "unchecked" })
    private Collection<String> findPrivilegeNamesForPE(
        long userId, String pe
    ) throws CSObjectNotFoundException {
        Set<ProtectionElementPrivilegeContext> ctxts =
            getAuthorizationManager().getProtectionElementPrivilegeContextForUser(Long.toString(userId));
        Set<String> privileges = new LinkedHashSet<String>();
        for (ProtectionElementPrivilegeContext pepContext : ctxts) {
            if (pepContext.getProtectionElement().getProtectionElementName().equals(pe)) {
                for (Object o : pepContext.getPrivileges()) {
                    privileges.add(((Privilege) o).getName());
                }
            }
        }
        return privileges;
    }
}
