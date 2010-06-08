package gov.nih.nci.cabig.ctms.suite.authorization;

import gov.nih.nci.security.authorization.domainobjects.Group;
import gov.nih.nci.security.authorization.domainobjects.Privilege;
import gov.nih.nci.security.authorization.domainobjects.ProtectionElementPrivilegeContext;
import gov.nih.nci.security.exceptions.CSObjectNotFoundException;
import gov.nih.nci.cabig.ctms.suite.authorization.domain.TestSiteMapping;
import gov.nih.nci.cabig.ctms.suite.authorization.domain.TestStudyMapping;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Rhett Sutphin
 */
public class ProvisioningHelperIntegratedTest extends IntegratedTestCase {
    private ProvisioningHelper helper;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        helper = new ProvisioningHelper();
        helper.setAuthorizationManager(getAuthorizationManager());
        helper.setAuthorizationDao(getAuthorizationDao());
        helper.setSiteMapping(new TestSiteMapping());
        helper.setStudyMapping(new TestStudyMapping());
    }

    public void testCreateRoleMembership() throws Exception {
        RoleMembership aRoleMembership = helper.createRoleMembership(Role.DATA_IMPORTER);
        assertNotNull(aRoleMembership);
        assertEquals("Wrong role", Role.DATA_IMPORTER, aRoleMembership.getRole());
        assertNotNull("Site mapping not passed along", aRoleMembership.getMapping(ScopeType.SITE));
        assertNotNull("Study mapping not passed along", aRoleMembership.getMapping(ScopeType.STUDY));
    }

    public void testDeleteExistingRoleDeletesUserPrivilegeMapping() throws Exception {
        Role existingRole = Role.USER_ADMINISTRATOR;
        assertUserHasPrivilege("Test setup failure", -22, "HealthcareSite.MI001", existingRole.getCsmName());

        helper.deleteRole(-22, existingRole);
        assertUserDoesNotHavePrivilege("Role not removed", -22, "HealthcareSite.MI001", existingRole.getCsmName());
    }

    public void testDeleteExistingRoleDeletesUserGroupMapping() throws Exception {
        Role existingRole = Role.USER_ADMINISTRATOR;
        assertUserInGroup("Test setup failure", existingRole.getCsmName(), -22);

        helper.deleteRole(-22, existingRole);
        assertUserNotInGroup("Membership not deleted", existingRole.getCsmName(), -22);
    }

    public void testDeleteNonExistentRoleDoesNothing() throws Exception {
        helper.deleteRole(-22, Role.DATA_READER);

        // expect no exceptions, plus
        assertUserInGroup("Unrelated group membership deleted",
            Role.USER_ADMINISTRATOR.getCsmName(), -22);
        assertUserHasPrivilege("Unrelated PE deleted", -22, "HealthcareSite.MI001",
            Role.USER_ADMINISTRATOR.getCsmName());
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
