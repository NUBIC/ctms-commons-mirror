package gov.nih.nci.cabig.ctms.suite.authorization;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The suite's logical representation of a user's inclusion in one of the unified roles.  It has
 * two purposes: first, along with {@link ProvisioningHelper}, it assists in the uniform provisioning of
 * users into certain roles.  Second, may receive instances of it from {@link TODO} 
 * which reflect the authorization information for a particular user.
 *
 * @author Rhett Sutphin
 */
@SuppressWarnings({ "RawUseOfParameterizedType" })
public class RoleMembership {
    private final Role role;
    private final Map<ScopeType, IdentifiableInstanceMapping> mappings;

    private Map<ScopeType, List<String>> identifiers;
    /**
     * Cache of the actual objects related to identifiers.  Invariant: the list of objects for a
     * given scope will either match the corresponding identifiers or be null.
     */
    private Map<ScopeType, List<Object>> applicationObjectCaches;
    private Map<ScopeType, Boolean> forAll;

    public RoleMembership(
        Role role,
        SiteMapping siteMapping,
        StudyMapping studyMapping
    ) {
        this.role = role;
        this.mappings = new HashMap<ScopeType, IdentifiableInstanceMapping>();
        this.mappings.put(ScopeType.SITE, siteMapping);
        this.mappings.put(ScopeType.STUDY, studyMapping);

        this.identifiers = new HashMap<ScopeType, List<String>>();
        this.applicationObjectCaches = new HashMap<ScopeType, List<Object>>();
        this.forAll = new HashMap<ScopeType, Boolean>();

        clear(ScopeType.SITE);
        clear(ScopeType.STUDY);
    }

    private synchronized void clear(ScopeType scope) {
        this.identifiers.put(scope, Collections.<String>emptyList());
        this.applicationObjectCaches.put(scope, null);
        this.forAll.put(scope, false);
    }

    ////// BUILDER

    /**
     * Scope this membership to the sites with the given shared identities.
     * @return this (for chaining)
     */
    public RoleMembership forSites(String... siteIdents) {
        return forSites(Arrays.asList(siteIdents));
    }

    /**
     * Scope this membership to the specified application site objects.
     * @return this (for chaining)
     */
    public RoleMembership forSites(Object... siteObjects) {
        return forSites(Arrays.asList(siteObjects));
    }

    /**
     * Scope this membership to the specified sites.  The collection may contain either application
     * site objects or site shared identifiers.
     * @return this (for chaining)
     */
    public RoleMembership forSites(Collection<?> sitesOrIdentifiers) {
        return forScopeObjectsOrIdentifiers(ScopeType.SITE, sitesOrIdentifiers);
    }

    /**
     * Scope this membership to the studies with the given shared identities.
     * @return this (for chaining)
     */
    public RoleMembership forStudies(String... studyIdents) {
        return forStudies(Arrays.asList(studyIdents));
    }

    /**
     * Scope this membership to the specified application site objects.
     * @return this (for chaining)
     */
    public RoleMembership forStudies(Object... studyObjects) {
        return forStudies(Arrays.asList(studyObjects));
    }

    /**
     * Scope this membership to the specified sites.  The collection may contain either application
     * site objects or site shared identifiers.
     * @return this (for chaining)
     */
    public RoleMembership forStudies(Collection<?> studiesOrIdentifiers) {
        return forScopeObjectsOrIdentifiers(ScopeType.STUDY, studiesOrIdentifiers);
    }

    @SuppressWarnings({ "unchecked" })
    private RoleMembership forScopeObjectsOrIdentifiers(ScopeType scope, Collection<?> objectsOrIdentifiers) {
        if (objectsOrIdentifiers.isEmpty()) {
            setIdentifiers(scope, Collections.<String>emptyList());
        } else {
            Object aMember = objectsOrIdentifiers.iterator().next();
            if (aMember instanceof String) {
                setIdentifiers(scope, new ArrayList<String>((Collection<? extends String>) objectsOrIdentifiers));
            } else if (getMapping(scope).isInstance(aMember)) {
                setApplicationObjects(scope, new ArrayList(objectsOrIdentifiers));
            }
        }
        return this;
    }

    @SuppressWarnings({ "unchecked" })
    private void setApplicationObjects(ScopeType scope, List<Object> objects) {
        List<String> correspondingIdentifiers = new ArrayList<String>(objects.size());
        for (Object siteObject : objects) {
            correspondingIdentifiers.add(getMapping(scope).getSharedIdentity(siteObject));
        }
        setIdentifiers(scope, correspondingIdentifiers);
        this.applicationObjectCaches.put(scope, objects);
    }

    public RoleMembership forAllSites() {
        return forAll(ScopeType.SITE);
    }

    public RoleMembership forAllStudies() {
        return forAll(ScopeType.STUDY);
    }

    private RoleMembership forAll(ScopeType scope) {
        clear(scope);
        this.forAll.put(scope, true);
        return this;
    }

    ////// VALIDATION

    public void validate() throws SuiteAuthorizationValidationException {
        // validate scopes
        Set<ScopeType> extraScopes = new LinkedHashSet<ScopeType>();
        Set<ScopeType> missingScopes = new LinkedHashSet<ScopeType>();
        for (ScopeType scope : ScopeType.values()) {
            boolean applicable = this.role.getScopes().contains(scope);
            boolean present = isScoped(scope);
            if (applicable && !present) {
                missingScopes.add(scope);
            } else if (present && !applicable) {
                extraScopes.add(scope);
            }
        }

        if (!missingScopes.isEmpty()) {
            String msMsg = scopeNameList(missingScopes);
            throw new SuiteAuthorizationValidationException(
                "The %s role is scoped to %s.  Please specify the %s scope%s.",
                role.getDisplayName(), msMsg, msMsg, missingScopes.size() == 1 ? "" : "s");
        }

        if (!extraScopes.isEmpty()) {
            throw new SuiteAuthorizationValidationException(
                "The %s role is not scoped to %s.",
                role.getDisplayName(), scopeNameList(extraScopes));
        }
    }

    private String scopeNameList(Set<ScopeType> scopes) {
        Collection<String> msNames = new LinkedHashSet<String>();
        for (ScopeType missingScope : scopes) {
            msNames.add(missingScope.name().toLowerCase());
        }
        return StringUtils.join(msNames, " and ");
    }

    private boolean isScoped(ScopeType scope) {
        return isAll(scope) || !getIdentifiers(scope).isEmpty();
    }

    ////// ACCESSORS

    public Role getRole() {
        return role;
    }

    /**
     * Returns the application site objects representing the scope of this role.  Invoking this
     * method may result in an object resolve.
     */
    public synchronized List<Object> getSites() {
        return getApplicationObjects(ScopeType.SITE);
    }

    /**
     * Returns the application site objects representing the scope of this role.  Invoking this
     * method may result in an object resolve.
     */
    public synchronized List<Object> getStudies() {
        return getApplicationObjects(ScopeType.STUDY);
    }

    @SuppressWarnings({ "unchecked" })
    private List<Object> getApplicationObjects(ScopeType scope) {
        if (isAll(scope)) {
            throw new SuiteAuthorizationAccessException(
                "This %s has access to every %s.  You can't list %s instances for it.",
                role.getDisplayName(), scope.name().toLowerCase(), scope.name().toLowerCase());
        }
        if (this.applicationObjectCaches.get(scope) == null) {
            this.applicationObjectCaches.put(scope,
                getMapping(scope).getApplicationInstances(getIdentifiers(scope)));
        }
        return this.applicationObjectCaches.get(scope);
    }

    public List<String> getSiteIdentifiers() {
        return getIdentifiers(ScopeType.SITE);
    }

    public List<String> getStudyIdentifiers() {
        return getIdentifiers(ScopeType.STUDY);
    }

    private List<String> getIdentifiers(ScopeType scope) throws SuiteAuthorizationAccessException {
        if (isAll(scope)) {
            throw new SuiteAuthorizationAccessException(
                "This %s has access to every %s.  You can't list %s identifiers for it.",
                role.getDisplayName(), scope.name().toLowerCase(), scope.name().toLowerCase());
        }
        return this.identifiers.get(scope);
    }

    private synchronized void setIdentifiers(ScopeType scope, List<String> identifiers) {
        clear(scope);
        this.identifiers.put(scope, identifiers);
    }

    public boolean isAllSites() {
        return isAll(ScopeType.SITE);
    }

    public boolean isAllStudies() {
        return isAll(ScopeType.STUDY);
    }

    private boolean isAll(ScopeType scope) {
        Boolean value = forAll.get(scope);
        if (value == null) {
            return false;
        } else {
            return value;
        }
    }

    protected IdentifiableInstanceMapping getMapping(ScopeType scope) {
        return mappings.get(scope);
    }
}