package gov.nih.nci.cabig.ctms.suite.authorization;

import gov.nih.nci.security.authorization.domainobjects.Group;
import gov.nih.nci.security.authorization.domainobjects.ProtectionGroup;
import gov.nih.nci.security.authorization.domainobjects.Role;
import gov.nih.nci.security.exceptions.CSTransactionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Encapsulates a facade for a series of operations on the provisioning data for a single user.
 * It is not intended to be kept around for longer than a single request.
 *
 * @see ProvisioningSessionFactory
 * @author Rhett Sutphin
 */
public class ProvisioningSession {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private long userId;
    private ProvisioningSessionFactory factory;
    /**
     * A cache of the logical reflection of this user's roles and scopes.  Kept in sync with CSM at
     * all times.
     */
    private Map<SuiteRole, SuiteRoleMembership> roleMemberships;

    /**
     * @see gov.nih.nci.cabig.ctms.suite.authorization.ProvisioningSessionFactory
     */
    public ProvisioningSession(long userId, ProvisioningSessionFactory factory) {
        this.userId = userId;
        this.factory = factory;
        this.roleMemberships = factory.getAuthorizationHelper().getRoleMemberships(userId);
    }

    /**
     * Ensures that the given user has the given role with exactly the scopes specified in the
     * membership object.  The user need not have the role before the first time this method is
     * called.
     * <p>
     * If the user already has exactly the given scopes, the method does nothing.
     *
     * @param replacement the membership data to apply to the user
     */
    public synchronized void replaceRole(SuiteRoleMembership replacement) {
        replacement.validate();

        ensureInGroupForRole(replacement.getRole());

        SuiteRoleMembership current = this.roleMemberships.get(replacement.getRole());
        List<SuiteRoleMembership.Difference> diff;
        if (current == null) {
            diff = replacement.diffFromNothing();
        } else {
            diff = current.diff(replacement);
        }
        applyDifferences(replacement.getRole(), diff);
        this.roleMemberships.put(replacement.getRole(), replacement);
    }

    /**
     * Removes all traces of the given role for the specified user.  If the user doesn't have the
     * role, it does nothing.
     *
     * @param role the role from which to remove the user
     */
    @SuppressWarnings({ "unchecked" })
    public synchronized void deleteRole(SuiteRole role) {
        ensureNotInGroupForRole(role);

        SuiteRoleMembership current = this.roleMemberships.remove(role);
        if (current != null) {
            applyDifferences(role, current.diff(null));
        }
    }

    /**
     * Returns a {@link SuiteRoleMembership} reflecting the user's current membership the specified
     * role.  If the user isn't in the given role, it returns a new clean instance that can be
     * configured and passed to {@link #replaceRole}.
     */
    public SuiteRoleMembership getProvisionableRoleMembership(SuiteRole role) {
        SuiteRoleMembership current = this.roleMemberships.get(role);
        if (current == null) {
            return factory.createSuiteRoleMembership(role);
        } else {
            return current.clone();
        }
    }

    private void ensureInGroupForRole(SuiteRole role) {
        Group csmGroup = factory.getCsmHelper().getRoleCsmGroup(role);
        try {
            factory.getAuthorizationManager().addGroupsToUser(
                Long.toString(userId), new String[] { csmGroup.getGroupId().toString() });
        } catch (CSTransactionException e) {
            throw new SuiteAuthorizationProvisioningFailure(
                "Deleting the group relationship failed", e);
        }
    }

    private void ensureNotInGroupForRole(SuiteRole role) {
        Group csmGroup = factory.getCsmHelper().getRoleCsmGroup(role);
        try {
            factory.getAuthorizationManager().removeUserFromGroup(
                csmGroup.getGroupId().toString(), Long.toString(userId));
        } catch (CSTransactionException e) {
            throw new SuiteAuthorizationProvisioningFailure(
                "Deleting the group relationship failed", e);
        }
    }

    private void applyDifferences(SuiteRole role, List<SuiteRoleMembership.Difference> diff) {
        Role csmRole = factory.getCsmHelper().getRoleCsmRole(role);
        String[] csmRoleIds = new String[] { csmRole.getId().toString() };
        try {
            for (SuiteRoleMembership.Difference difference : diff) {
                ProtectionGroup pg = factory.getCsmHelper().getOrCreateScopeProtectionGroup(difference.getScopeDescription());
                log.debug("{} role {} ({})", new Object[] { difference.getKind(), csmRole.getId(), csmRole.getName() });
                log.debug("  scoped by PG {} ({})", pg.getProtectionGroupId().toString(), pg.getProtectionGroupName());
                if (difference.getKind().equals(SuiteRoleMembership.Difference.Kind.ADD)) {
                    factory.getAuthorizationManager().addUserRoleToProtectionGroup(
                        Long.toString(userId), csmRoleIds, pg.getProtectionGroupId().toString());
                } else if (difference.getKind().equals(SuiteRoleMembership.Difference.Kind.DELETE)) {
                    factory.getAuthorizationManager().removeUserRoleFromProtectionGroup(
                        pg.getProtectionGroupId().toString(), Long.toString(userId), csmRoleIds);
                }
            }
        } catch (CSTransactionException e) {
            throw new SuiteAuthorizationValidationException("Failed to update role-group associations from " + diff, e);
        }
    }
}
