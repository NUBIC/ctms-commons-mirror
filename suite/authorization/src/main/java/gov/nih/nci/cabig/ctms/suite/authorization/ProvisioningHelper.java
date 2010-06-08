package gov.nih.nci.cabig.ctms.suite.authorization;

import gov.nih.nci.security.AuthorizationManager;
import gov.nih.nci.security.authorization.domainobjects.Group;
import gov.nih.nci.security.authorization.domainobjects.ProtectionGroup;
import gov.nih.nci.security.authorization.domainobjects.ProtectionGroupRoleContext;
import gov.nih.nci.security.authorization.domainobjects.Role;
import gov.nih.nci.security.dao.AuthorizationDAO;
import gov.nih.nci.security.exceptions.CSObjectNotFoundException;
import gov.nih.nci.security.exceptions.CSTransactionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Rhett Sutphin
 */
@SuppressWarnings({ "RawUseOfParameterizedType" })
public class ProvisioningHelper {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private AuthorizationManager authorizationManager;
    private AuthorizationDAO authorizationDao;
    private SiteMapping siteMapping;
    private StudyMapping studyMapping;
    private AuthorizationHelper authorizationHelper;
    private CsmHelper csmHelper;

    /**
     * Assists in creating {@link SuiteRoleMembership}s which use the same configuration as this helper.
     * E.g., you might use it like so:
     * <code><pre>
     * long userId = ...
     * gov.nih.nci.ctms.someapp.domain.Study someStudy = ...
     * provisioningHelper.replaceRole(userId, provisioningHelper.createSuiteRoleMembership(Role.DATA_READER).forAllSites().forStudies(someStudy));
     * </pre></code>
     */
    public SuiteRoleMembership createSuiteRoleMembership(SuiteRole role) {
        return new SuiteRoleMembership(role, getSiteMapping(), getStudyMapping());
    }

    /**
     * Ensures that the given user has the given role with exactly the scopes specified in the
     * membership object.  The user need not have the role before the first time this method is
     * called.
     * <p>
     * If the user already has exactly the given scopes, the method does nothing.
     *
     * @param userId the CSM user ID for the user to change
     * @param replacement the membership data to apply to the user
     */
    public void replaceRole(long userId, SuiteRoleMembership replacement) {
        replacement.validate();

        ensureInGroupForRole(replacement.getRole(), userId);

        SuiteRoleMembership current = getAuthorizationHelper().getRoleMemberships(userId).get(replacement.getRole());
        List<SuiteRoleMembership.Difference> diff;
        if (current == null) {
            diff = replacement.diffFromNothing();
        } else {
            diff = current.diff(replacement);
        }
        applyDifferences(userId, replacement.getRole(), diff);
    }

    /**
     * Removes all traces of the given role for the specified user.  If the user doesn't have the
     * role, it does nothing.
     *
     * @param userId the CSM user ID for the user to change
     * @param role the role from which to remove the user
     */
    @SuppressWarnings({ "unchecked" })
    public void deleteRole(long userId, SuiteRole role) {
        ensureNotInGroupForRole(role, userId);

        try {
            Role csmRole = getCsmHelper().getRoleCsmRole(role);
            Set<ProtectionGroupRoleContext> roleContext =
                getAuthorizationManager().getProtectionGroupRoleContextForUser(Long.toString(userId));
            Set<Long> protectionGroupIds = new LinkedHashSet<Long>();
            for (ProtectionGroupRoleContext context : roleContext) {
                if (context.getRoles().contains(csmRole)) {
                    protectionGroupIds.add(context.getProtectionGroup().getProtectionGroupId());
                }
            }
            String[] roleIds = { csmRole.getId().toString() };
            for (Long id : protectionGroupIds) {
                getAuthorizationManager().removeUserRoleFromProtectionGroup(
                    id.toString(), Long.toString(userId), roleIds);
            }
        } catch (CSObjectNotFoundException e) {
            throw new SuiteAuthorizationProvisioningFailure(
                "Accessing the role context failed", e);
        } catch (CSTransactionException e) {
            throw new SuiteAuthorizationProvisioningFailure(
                "Modifying the user's protection group roles failed.", e);
        }
    }

    private void ensureInGroupForRole(SuiteRole role, long userId) {
        Group csmGroup = getCsmHelper().getRoleCsmGroup(role);
        try {
            getAuthorizationManager().assignGroupsToUser(
                Long.toString(userId), new String[] { csmGroup.getGroupId().toString() });
        } catch (CSTransactionException e) {
            throw new SuiteAuthorizationProvisioningFailure(
                "Deleting the group relationship failed", e);
        }
    }

    private void ensureNotInGroupForRole(SuiteRole role, long userId) {
        Group csmGroup = getCsmHelper().getRoleCsmGroup(role);
        try {
            getAuthorizationManager().removeUserFromGroup(
                csmGroup.getGroupId().toString(), Long.toString(userId));
        } catch (CSTransactionException e) {
            throw new SuiteAuthorizationProvisioningFailure(
                "Deleting the group relationship failed", e);
        }
    }

    private void applyDifferences(long userId, SuiteRole role, List<SuiteRoleMembership.Difference> diff) {
        Role csmRole = getCsmHelper().getRoleCsmRole(role);
        String[] csmRoleIds = new String[] { csmRole.getId().toString() };
        try {
            for (SuiteRoleMembership.Difference difference : diff) {
                ProtectionGroup pg = getCsmHelper().getOrCreateScopeProtectionGroup(difference.getScopeDescription());
                log.debug("{} role {} ({})", new Object[] { difference.getKind(), csmRole.getId(), csmRole.getName() });
                log.debug("  scoped by PG {} ({})", pg.getProtectionGroupId().toString(), pg.getProtectionGroupName());
                if (difference.getKind().equals(SuiteRoleMembership.Difference.Kind.ADD)) {
                    getAuthorizationManager().addUserRoleToProtectionGroup(
                        Long.toString(userId), csmRoleIds, pg.getProtectionGroupId().toString());
                } else if (difference.getKind().equals(SuiteRoleMembership.Difference.Kind.DELETE)) {
                    getAuthorizationManager().removeUserRoleFromProtectionGroup(
                        pg.getProtectionGroupId().toString(), Long.toString(userId), csmRoleIds);
                }
            }
        } catch (CSTransactionException e) {
            throw new SuiteAuthorizationValidationException("Failed to update role-group associations from " + diff, e);
        }
    }

    ///// CONFIGURATION

    protected synchronized CsmHelper getCsmHelper() {
        if (csmHelper == null) {
            csmHelper = new CsmHelper();
            csmHelper.setAuthorizationDao(getAuthorizationDao());
            csmHelper.setAuthorizationManager(getAuthorizationManager());
            csmHelper.setSiteMapping(getSiteMapping());
            csmHelper.setStudyMapping(getStudyMapping());
        }
        return csmHelper;
    }

    /**
     * Set the {@link CsmHelper} to use.  If none is provided, an instance will be created on use.
     */
    public void setCsmHelper(CsmHelper csmHelper) {
        this.csmHelper = csmHelper;
    }

    protected synchronized AuthorizationHelper getAuthorizationHelper() {
        if (authorizationHelper == null) {
            authorizationHelper = new AuthorizationHelper();
            authorizationHelper.setAuthorizationManager(getAuthorizationManager());
            authorizationHelper.setSiteMapping(getSiteMapping());
            authorizationHelper.setStudyMapping(getStudyMapping());
        }
        return authorizationHelper;
    }

    /**
     * Set the {@link CsmHelper} to use.  If none is provided, an instance will be created on use.
     */
    public void setAuthorizationHelper(AuthorizationHelper authorizationHelper) {
        this.authorizationHelper = authorizationHelper;
    }

    protected AuthorizationManager getAuthorizationManager() {
        return authorizationManager;
    }

    /**
     * Set the CSM AuthorizationMananger to use.
     */
    public void setAuthorizationManager(AuthorizationManager authorizationManager) {
        this.authorizationManager = authorizationManager;
    }

    protected AuthorizationDAO getAuthorizationDao() {
        return authorizationDao;
    }

    /**
     * Set the CSM AuthorizationDao to use.
     */
    public void setAuthorizationDao(AuthorizationDAO authorizationDao) {
        this.authorizationDao = authorizationDao;
    }

    protected SiteMapping getSiteMapping() {
        return siteMapping;
    }

    /**
     * Specify an application site object mapping for this instance.  If this is not wired, created
     * {@link SuiteRoleMembership}s will not be able to accept application site objects.
     */
    public void setSiteMapping(SiteMapping siteMapping) {
        this.siteMapping = siteMapping;
    }

    protected StudyMapping getStudyMapping() {
        return studyMapping;
    }

    /**
     * Specify an application study object mapping for this instance.  If this is not wired, created
     * {@link SuiteRoleMembership}s will not be able to accept application study objects.
     */
    public void setStudyMapping(StudyMapping studyMapping) {
        this.studyMapping = studyMapping;
    }
}
