package gov.nih.nci.cabig.ctms.suite.authorization;

import gov.nih.nci.security.AuthorizationManager;
import gov.nih.nci.security.authorization.domainobjects.Group;
import gov.nih.nci.security.authorization.domainobjects.Privilege;
import gov.nih.nci.security.authorization.domainobjects.ProtectionElementPrivilegeContext;
import gov.nih.nci.security.exceptions.CSObjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Rhett Sutphin
 */
@SuppressWarnings({ "RawUseOfParameterizedType" })
public class AuthorizationHelper {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private AuthorizationManager authorizationManager;
    private SiteMapping siteMapping;
    private StudyMapping studyMapping;

    /**
     * Returns all the {@link RoleMembership}s for a user, indexed by {@link Role}.
     * <p>
     * Applications may not wish to use this as their primary authorization interface.
     * That is fine -- this method is expected to primarily be used by {@link ProvisioningHelper}.
     */
    public Map<Role, RoleMembership> getRoleMemberships(long userId) {
        Map<Role, RoleMembership> memberships = new LinkedHashMap<Role, RoleMembership>();
        memberships.putAll(readUnscopedRoles(userId));
        memberships.putAll(readScopedRoles(userId));
        for (Iterator<Map.Entry<Role, RoleMembership>> it = memberships.entrySet().iterator(); it.hasNext();) {
            Map.Entry<Role, RoleMembership> entry = it.next();
            try {
                entry.getValue().validate();
            } catch (SuiteAuthorizationValidationException e) {
                log.debug("Removing invalid membership for {}: {}", entry.getKey(), e.getMessage());
                it.remove();
            }
        }
        return memberships;
    }

    @SuppressWarnings({ "unchecked" })
    private Map<Role, RoleMembership> readUnscopedRoles(long userId) {
        Map<Role, RoleMembership> result = new LinkedHashMap<Role, RoleMembership>();
        Set<Group> groups;
        try {
            groups = getAuthorizationManager().getGroups(Long.toString(userId));
        } catch (CSObjectNotFoundException e) {
            throw new SuiteAuthorizationAccessException("Failed to load groups for user %s", e, userId);
        }
        for (Group group : groups) {
            Role r = Role.getByCsmName(group.getGroupName());
            if (!r.isScoped()) {
                result.put(r, createRoleMembership(r));
            }
        }
        return result;
    }

    @SuppressWarnings({ "unchecked" })
    private Map<Role, RoleMembership> readScopedRoles(long userId) {
        Map<Role, RoleMembership> result = new LinkedHashMap<Role, RoleMembership>();
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
                Role role = Role.getByCsmName(((Privilege) p).getName());
                if (!result.containsKey(role)) {
                    result.put(role, createRoleMembership(role));
                }
                if (sd.isAll()) {
                    if (sd.getScope() == ScopeType.SITE) {
                        result.get(role).forAllSites();
                    } else if (sd.getScope() == ScopeType.STUDY) {
                        result.get(role).forAllStudies();
                    }
                } else {
                    if (sd.getScope() == ScopeType.SITE) {
                        result.get(role).addSite(sd.getIdentifier());
                    } else if (sd.getScope() == ScopeType.STUDY) {
                        result.get(role).addStudy(sd.getIdentifier());
                    }
                }
            }
        }

        return result;
    }

    private RoleMembership createRoleMembership(Role role) {
        return new RoleMembership(role, getSiteMapping(), getStudyMapping());
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
