package gov.nih.nci.cabig.ctms.suite.authorization;

import gov.nih.nci.security.AuthorizationManager;
import gov.nih.nci.security.authorization.domainobjects.Group;
import gov.nih.nci.security.authorization.domainobjects.Privilege;
import gov.nih.nci.security.authorization.domainobjects.ProtectionElementPrivilegeContext;
import gov.nih.nci.security.exceptions.CSObjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Retrieves {@link SuiteRoleMembership}s from CSM.
 *
 * @author Rhett Sutphin
 */
@SuppressWarnings({ "RawUseOfParameterizedType" })
public class SuiteRoleMembershipLoader {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private AuthorizationManager authorizationManager;
    private SiteMapping siteMapping;
    private StudyMapping studyMapping;

    /**
     * Returns all the complete {@link SuiteRoleMembership}s for a user, indexed by {@link SuiteRole}.
     * <p>
     * This is an alternative to using the CSM API to acquire this information piecemeal.  Pros:
     * automatically excludes invalid or incomplete memberships.  Cons: Less future-proof.
     */
    public Map<SuiteRole, SuiteRoleMembership> getRoleMemberships(long userId) {
        return getRoleMemberships(userId, false);
    }

    /**
     * Returns all the {@link SuiteRoleMembership}s for a user, indexed by {@link SuiteRole}.
     * <p>
     * Includes incomplete memberships (e.g., memberships for study-scoped roles that are missing
     * study scoping information).  This method should not be used for authorization; only for
     * provisioning.
     */
    public Map<SuiteRole, SuiteRoleMembership> getProvisioningRoleMemberships(long userId) {
        return getRoleMemberships(userId, true);
    }

    private Map<SuiteRole, SuiteRoleMembership> getRoleMemberships(long userId, boolean forProvisioning) {
        Map<SuiteRole, SuiteRoleMembership> memberships = readMemberships(userId);
        for (Iterator<Map.Entry<SuiteRole, SuiteRoleMembership>> it = memberships.entrySet().iterator(); it.hasNext();) {
            Map.Entry<SuiteRole, SuiteRoleMembership> entry = it.next();
            try {
                entry.getValue().validate();
                if (!forProvisioning) {
                    entry.getValue().checkComplete();
                }
            } catch (SuiteAuthorizationValidationException e) {
                log.debug("Removing inappropriate membership for {}: {}", entry.getKey(), e.getMessage());
                it.remove();
            }
        }
        return memberships;
    }

    @SuppressWarnings({ "unchecked" })
    private Map<SuiteRole, SuiteRoleMembership> readMemberships(long userId) {
        Map<SuiteRole, SuiteRoleMembership> result = new LinkedHashMap<SuiteRole, SuiteRoleMembership>();
        for (SuiteRole r : readRoles(userId)) {
            if (!result.containsKey(r)) {
                result.put(r, createRoleMembership(r));
            }
        }       
        enhanceRoleMemberships(userId, result);
        return result;
    }

    private Collection<SuiteRole> readRoles(long userId) {
        Collection<SuiteRole> result = new LinkedList<SuiteRole>();
        Set<Group> groups;
        try {
            groups = getAuthorizationManager().getGroups(Long.toString(userId));
        } catch (CSObjectNotFoundException e) {
            throw new SuiteAuthorizationAccessException("Failed to load groups for user %s", e, userId);
        }
        for (Group group : groups) {
            SuiteRole r = SuiteRole.getByCsmName(group.getGroupName());
            result.add(r);
        }
        return result;
    }

    private void enhanceRoleMemberships(long userId, Map<SuiteRole, SuiteRoleMembership> memberships) {
        Set<ProtectionElementPrivilegeContext> contexts;
        try {
            contexts = getAuthorizationManager().getProtectionElementPrivilegeContextForUser(Long.toString(userId));
        } catch (CSObjectNotFoundException e) {
            throw new SuiteAuthorizationAccessException(
                "Failed to load protection elements / privileges for user %s", e, userId);
        }
        for (ProtectionElementPrivilegeContext context : contexts) {
            ScopeDescription sd = ScopeDescription.createFrom(context.getProtectionElement());
            for (Object p : context.getPrivileges()) {
                SuiteRole role = SuiteRole.getByCsmName(((Privilege) p).getName());
                if (!memberships.containsKey(role)) {
                    memberships.put(role, createRoleMembership(role));
                }
                if (sd.isAll()) {
                    if (sd.getScope() == ScopeType.SITE) {
                        memberships.get(role).forAllSites();
                    } else if (sd.getScope() == ScopeType.STUDY) {
                        memberships.get(role).forAllStudies();
                    }
                } else {
                    if (sd.getScope() == ScopeType.SITE) {
                        memberships.get(role).addSite(sd.getIdentifier());
                    } else if (sd.getScope() == ScopeType.STUDY) {
                        memberships.get(role).addStudy(sd.getIdentifier());
                    }
                }
            }
        }
    }

    private SuiteRoleMembership createRoleMembership(SuiteRole role) {
        return new SuiteRoleMembership(role, getSiteMapping(), getStudyMapping());
    }

    ////// CONFIGURATION

    protected AuthorizationManager getAuthorizationManager() {
        return authorizationManager;
    }

    public void setAuthorizationManager(AuthorizationManager authorizationManager) {
        this.authorizationManager = authorizationManager;
    }

    protected SiteMapping getSiteMapping() {
        return siteMapping;
    }

    public void setSiteMapping(SiteMapping siteMapping) {
        this.siteMapping = siteMapping;
    }

    protected StudyMapping getStudyMapping() {
        return studyMapping;
    }

    public void setStudyMapping(StudyMapping studyMapping) {
        this.studyMapping = studyMapping;
    }
}
