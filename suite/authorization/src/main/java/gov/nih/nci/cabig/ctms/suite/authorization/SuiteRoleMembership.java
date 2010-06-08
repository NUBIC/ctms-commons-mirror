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
import java.util.LinkedList;

/**
 * The suite's logical representation of a user's inclusion in one of the unified roles.  It has
 * two purposes: first, along with {@link ProvisioningSession}, it assists in the uniform provisioning of
 * users into certain roles.  Second, applications may receive instances of it from
 * {@link AuthorizationHelper} which reflect the authorization information for a particular user.
 *
 * @author Rhett Sutphin
 */
@SuppressWarnings({ "RawUseOfParameterizedType" })
public class SuiteRoleMembership {
    private final SuiteRole role;
    private final Map<ScopeType, IdentifiableInstanceMapping> mappings;

    private Map<ScopeType, List<String>> identifiers;
    /**
     * Cache of the actual objects related to identifiers.  Invariant: the list of objects for a
     * given scope will either match the corresponding identifiers or be null.
     */
    private Map<ScopeType, List<Object>> applicationObjectCaches;
    private Map<ScopeType, Boolean> forAll;

    /**
     * Create a new instance.  Consider using {@link ProvisioningSessionFactory#createSuiteRoleMembership}
     * instead if you have a {@link ProvisioningSessionFactory} available.
     */
    public SuiteRoleMembership(
        SuiteRole role,
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
    public SuiteRoleMembership forSites(String... siteIdents) {
        return forSites(Arrays.asList(siteIdents));
    }

    /**
     * Scope this membership to the specified application site objects.
     * @return this (for chaining)
     */
    public SuiteRoleMembership forSites(Object... siteObjects) {
        return forSites(Arrays.asList(siteObjects));
    }

    /**
     * Scope this membership to the specified sites.  The collection may contain either application
     * site objects or site shared identifiers.
     * @return this (for chaining)
     */
    public SuiteRoleMembership forSites(Collection<?> sitesOrIdentifiers) {
        return forScopeObjectsOrIdentifiers(ScopeType.SITE, sitesOrIdentifiers);
    }

    /**
     * Expand the memberships' scope to include another site.
     * @return this (for chaining)
     */
    public SuiteRoleMembership addSite(String identifier) {
        return addScopeIdentifier(ScopeType.SITE, identifier);
    }

    /**
     * Scope this membership to the studies with the given shared identities.
     * @return this (for chaining)
     */
    public SuiteRoleMembership forStudies(String... studyIdents) {
        return forStudies(Arrays.asList(studyIdents));
    }

    /**
     * Scope this membership to the specified application site objects.
     * @return this (for chaining)
     */
    public SuiteRoleMembership forStudies(Object... studyObjects) {
        return forStudies(Arrays.asList(studyObjects));
    }

    /**
     * Scope this membership to the specified sites.  The collection may contain either application
     * site objects or site shared identifiers.
     * @return this (for chaining)
     */
    public SuiteRoleMembership forStudies(Collection<?> studiesOrIdentifiers) {
        return forScopeObjectsOrIdentifiers(ScopeType.STUDY, studiesOrIdentifiers);
    }

    /**
     * Expand the memberships' scope to include another site.
     * @return this (for chaining)
     */
    public SuiteRoleMembership addStudy(String identifier) {
        return addScopeIdentifier(ScopeType.STUDY, identifier);
    }

    private SuiteRoleMembership addScopeIdentifier(ScopeType scope, String identifier) {
        if (isAll(scope)) {
            forScopeObjectsOrIdentifiers(scope, Collections.singleton(identifier));
        } else {
            List<String> newIs = new ArrayList<String>(this.identifiers.get(scope));
            newIs.add(identifier);
            setIdentifiers(scope, newIs);
        }
        return this;
    }

    @SuppressWarnings({ "unchecked" })
    private SuiteRoleMembership forScopeObjectsOrIdentifiers(ScopeType scope, Collection<?> objectsOrIdentifiers) {
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

    public SuiteRoleMembership forAllSites() {
        return forAll(ScopeType.SITE);
    }

    public SuiteRoleMembership forAllStudies() {
        return forAll(ScopeType.STUDY);
    }

    private SuiteRoleMembership forAll(ScopeType scope) {
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

    public SuiteRole getRole() {
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

    ////// ANALYSIS

    /**
     * Returns a set of {@link Difference}s describing the mutations that are necessary to change
     * this membership into the other one.
     * <p>
     * Ignores the role in each.
     */
    public List<Difference> diff(SuiteRoleMembership other) {
        if (other == null) {
            other = new SuiteRoleMembership(getRole(), null, null);
        }
        List<Difference> differences = new LinkedList<Difference>();
        for (ScopeType scope : ScopeType.values()) {
            if (this.isAll(scope)) {
                if (!other.isAll(scope)) {
                    differences.add(Difference.delete(scope));
                    for (String ident : other.getIdentifiers(scope)) {
                        differences.add(Difference.add(scope, ident));
                    }
                }
            } else {
                if (other.isAll(scope)) {
                    differences.add(Difference.add(scope));
                    for (String ident : this.getIdentifiers(scope)) {
                        differences.add(Difference.delete(scope, ident));
                    }
                } else {
                    for (String thisIdent : this.getIdentifiers(scope)) {
                        if (!other.getIdentifiers(scope).contains(thisIdent)) {
                            differences.add(Difference.delete(scope, thisIdent));
                        }
                    }
                    for (String otherIdent : other.getIdentifiers(scope)) {
                        if (!this.getIdentifiers(scope).contains(otherIdent)) {
                            differences.add(Difference.add(scope, otherIdent));
                        }
                    }
                }
            }
        }
        return differences;
    }

    /**
     * Returns the list of differences needed to create this instance from scratch.
     * @return
     */
    public List<Difference> diffFromNothing() {
        return new SuiteRoleMembership(getRole(), null, null).diff(this);
    }

    public static class Difference {
        public static Difference delete(ScopeType scopeType) {
            return new Difference(Kind.DELETE, ScopeDescription.createForAll(scopeType));
        }

        public static Difference delete(ScopeType scopeType, String ident) {
            return new Difference(Kind.DELETE, ScopeDescription.createForOne(scopeType, ident));
        }

        public static Difference add(ScopeType scopeType) {
            return new Difference(Kind.ADD, ScopeDescription.createForAll(scopeType));
        }

        public static Difference add(ScopeType scopeType, String ident) {
            return new Difference(Kind.ADD, ScopeDescription.createForOne(scopeType, ident));
        }

        public static enum Kind { ADD, DELETE }

        private Kind mode;
        private ScopeDescription scopeDescription;

        private Difference(Kind mode, ScopeDescription scopeDescription) {
            this.mode = mode;
            this.scopeDescription = scopeDescription;
        }

        public Kind getKind() {
            return mode;
        }

        public ScopeDescription getScopeDescription() {
            return scopeDescription;
        }
    }
}
