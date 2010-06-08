package gov.nih.nci.cabig.ctms.suite.authorization;

import gov.nih.nci.cabig.ctms.suite.authorization.domain.TestSiteMapping;
import gov.nih.nci.cabig.ctms.suite.authorization.domain.TestStudyMapping;
import gov.nih.nci.security.authorization.domainobjects.Group;
import gov.nih.nci.security.authorization.domainobjects.Privilege;
import gov.nih.nci.security.authorization.domainobjects.ProtectionElementPrivilegeContext;
import gov.nih.nci.security.exceptions.CSObjectNotFoundException;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Rhett Sutphin
 */
public class ProvisioningSessionIntegratedTest extends IntegratedTestCase {
    private ProvisioningSessionFactory psFactory;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        psFactory = new ProvisioningSessionFactory();
        psFactory.setAuthorizationManager(getAuthorizationManager());
        psFactory.setAuthorizationDao(getAuthorizationDao());
        psFactory.setSiteMapping(new TestSiteMapping());
        psFactory.setStudyMapping(new TestStudyMapping());
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

    public void testFailsWhenNewMembershipIsInvalid() throws Exception {
        ProvisioningSession helper = createSession(-22);
        try {
            // missing scope
            helper.replaceRole(psFactory.createSuiteRoleMembership(SuiteRole.USER_ADMINISTRATOR));
            fail("Exception not thrown");
        } catch (SuiteAuthorizationValidationException save) {
            // expected
        }
    }

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
