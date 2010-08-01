package gov.nih.nci.cabig.ctms.suite.authorization;

import gov.nih.nci.cabig.ctms.CommonsError;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * {@link SuiteRoleMembershipLoader} which reflect the authorization information for a particular user.
 *
 * @author Rhett Sutphin
 */
@SuppressWarnings({ "RawUseOfParameterizedType" })
public class SuiteRoleMembership implements Cloneable {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final SuiteRole role;
    private final Map<ScopeType, IdentifiableInstanceMapping> mappings;

    private Map<ScopeType, List<String>> identifiers;
    /**
     * Cache of the actual objects related to identifiers.  Invariant: the list of objects for a
     * given scope will either match the corresponding identifiers or be null.
     */
    private Map<ScopeType, List<?>> applicationObjectCaches;
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
        this.applicationObjectCaches = new HashMap<ScopeType, List<?>>();
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
     * Expand the membership's scope to include another site.
     * @return this (for chaining)
     */
    public SuiteRoleMembership addSite(String identifier) {
        return addScopeObjectOrIdentifier(ScopeType.SITE, identifier);
    }

    /**
     * Expand the membership's scope to include another site.
     * @return this (for chaining)
     */
    public SuiteRoleMembership addSite(Object site) {
        return addScopeObjectOrIdentifier(ScopeType.SITE, site);
    }

    /**
     * Contract the membership's scope by removing the specified site.
     * @return this (for chaining)
     */
    public SuiteRoleMembership removeSite(String identifier) {
        return removeScopeObjectOrIdentifier(ScopeType.SITE, identifier);
    }

    /**
     * Contract the membership's scope by removing the specified site.
     * @return this (for chaining)
     */
    public SuiteRoleMembership removeSite(Object site) {
        return removeScopeObjectOrIdentifier(ScopeType.SITE, site);
    }

    /**
     * Scope this membership to the studies with the given shared identities.
     * @return this (for chaining)
     */
    public SuiteRoleMembership forStudies(String... studyIdents) {
        return forStudies(Arrays.asList(studyIdents));
    }

    /**
     * Scope this membership to the specified application study objects.
     * @return this (for chaining)
     */
    public SuiteRoleMembership forStudies(Object... studyObjects) {
        return forStudies(Arrays.asList(studyObjects));
    }

    /**
     * Scope this membership to the specified studies.  The collection may contain either application
     * study objects or study shared identifiers.
     * @return this (for chaining)
     */
    public SuiteRoleMembership forStudies(Collection<?> studiesOrIdentifiers) {
        return forScopeObjectsOrIdentifiers(ScopeType.STUDY, studiesOrIdentifiers);
    }

    /**
     * Expand the memberships' scope to include another study.
     * @return this (for chaining)
     */
    public SuiteRoleMembership addStudy(String identifier) {
        return addScopeObjectOrIdentifier(ScopeType.STUDY, identifier);
    }

    /**
     * Expand the memberships' scope to include another study.
     * @return this (for chaining)
     */
    public SuiteRoleMembership addStudy(Object study) {
        return addScopeObjectOrIdentifier(ScopeType.STUDY, study);
    }

    /**
     * Contract the membership's scope by removing the specified study.
     * @return this (for chaining)
     */
    public SuiteRoleMembership removeStudy(String identifier) {
        return removeScopeObjectOrIdentifier(ScopeType.STUDY, identifier);
    }

    /**
     * Contract the membership's scope by removing the specified study.
     * @return this (for chaining)
     */
    public SuiteRoleMembership removeStudy(Object study) {
        return removeScopeObjectOrIdentifier(ScopeType.STUDY, study);
    }

    // n.b.: add is always done in terms of identifiers.  We assume that most of the time
    // when you are doing an add, the application objects won't be loaded.  No point in loading
    // them just to add one.
    @SuppressWarnings({ "unchecked" })
    private SuiteRoleMembership addScopeObjectOrIdentifier(ScopeType scope, Object objectOrIdentifier) {
        String identifierToAdd;
        if (objectOrIdentifier instanceof String) {
            identifierToAdd = (String) objectOrIdentifier;
        } else if (getMapping(scope).isInstance(objectOrIdentifier)) {
            identifierToAdd = getMapping(scope).getSharedIdentity(objectOrIdentifier);
        } else {
            throw new SuiteAuthorizationValidationException(
                "Attempted to add an instance of %s as a %s scope object.  This is not an acceptable type; check your mapping.",
                objectOrIdentifier.getClass().getName(), scope.getName());
        }

        if (isAll(scope)) {
            forScopeObjectsOrIdentifiers(scope, Collections.singleton(identifierToAdd));
        } else if (!this.identifiers.get(scope).contains(identifierToAdd)) {
            List<String> newIs = new ArrayList<String>(this.identifiers.get(scope));
            newIs.add(identifierToAdd);
            setIdentifiers(scope, newIs);
        }
        return this;
    }

    // n.b.: remove is always done in terms of identifiers.  We assume that most of the time
    // when you are doing a remove, the application objects won't be loaded.  No point in loading
    // them just to remove one.
    @SuppressWarnings({ "unchecked" })
    private SuiteRoleMembership removeScopeObjectOrIdentifier(ScopeType scope, Object objectOrIdentifier) {
        String identifierToRemove = null;
        if (objectOrIdentifier instanceof String) {
            identifierToRemove = (String) objectOrIdentifier;
        } else if (getMapping(scope).isInstance(objectOrIdentifier)) {
            identifierToRemove = getMapping(scope).getSharedIdentity(objectOrIdentifier);
        } else {
            log.warn("Attempted to remove an instance of {} as an authorization {} scope object.  There is no conversion from that type to an identifier, so nothing will happen.",
                objectOrIdentifier.getClass().getName(), scope.getName());
        }

        List<String> newIs = new ArrayList<String>(this.identifiers.get(scope));
        newIs.remove(identifierToRemove);
        setIdentifiers(scope, newIs);
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

    /**
     * Flags this membership as having the special "all sites" site scope.
     */
    public SuiteRoleMembership forAllSites() {
        return forAll(ScopeType.SITE);
    }

    /**
     * Flags this membership as having the special "all studies" study scope.
     */
    public SuiteRoleMembership forAllStudies() {
        return forAll(ScopeType.STUDY);
    }

    private SuiteRoleMembership forAll(ScopeType scope) {
        clear(scope);
        this.forAll.put(scope, true);
        return this;
    }

    /**
     * Flags this membership as <em>not</em> having the special "all sites" site scope.
     * Setting any specific site scope has the same effect, so you only need to call this
     * method if you want to clear an existing for-all flag without specifying a particular scope.
     */
    public SuiteRoleMembership notForAllSites() {
        return notForAll(ScopeType.SITE);
    }

    /**
     * Flags this membership as <em>not</em> having the special "all studies" study scope.
     * Setting any specific study scope has the same effect, so you only need to call this
     * method if you want to clear an existing for-all flag without specifying a particular scope.
     */
    public SuiteRoleMembership notForAllStudies() {
        return notForAll(ScopeType.STUDY);
    }

    private SuiteRoleMembership notForAll(ScopeType scope) {
        this.forAll.put(scope, false);
        return this;
    }

    ////// VALIDATION

    /**
     * Verifies that the membership is sufficiently well-formed to be persisted to CSM.
     * Such a membership may still not be complete enough to actually be used for authorization.
     *
     * @see #checkComplete
     */
    public void validate() throws SuiteAuthorizationValidationException {
        Set<ScopeType> extraScopes = new LinkedHashSet<ScopeType>();
        for (ScopeType scope : ScopeType.values()) {
            boolean applicable = this.role.getScopes().contains(scope);
            boolean present = hasScope(scope);
            if (present && !applicable) {
                extraScopes.add(scope);
            }
        }

        if (!extraScopes.isEmpty()) {
            throw new SuiteAuthorizationValidationException(
                "The %s role is not scoped to %s.",
                role.getDisplayName(), scopeNameList(extraScopes));
        }
    }

    /**
     * Ensures that a membership has enough associated information to be used for authorization.
     * I.e., that scope data is specified for all the scopes which apply to it.
     *
     * @see #validate
     * @see SuiteRole#getScopes()
     */
    public void checkComplete() throws SuiteAuthorizationValidationException {
        Set<ScopeType> missingScopes = new LinkedHashSet<ScopeType>();
        for (ScopeType scope : ScopeType.values()) {
            boolean applicable = this.role.getScopes().contains(scope);
            boolean present = hasScope(scope);
            if (applicable && !present) {
                missingScopes.add(scope);
            }
        }

        if (!missingScopes.isEmpty()) {
            String msMsg = scopeNameList(missingScopes);
            throw new SuiteAuthorizationValidationException(
                "The %s role is scoped to %s.  Please specify the %s scope%s.",
                role.getDisplayName(), msMsg, msMsg, missingScopes.size() == 1 ? "" : "s");
        }
    }

    private String scopeNameList(Set<ScopeType> scopes) {
        Collection<String> msNames = new LinkedHashSet<String>();
        for (ScopeType missingScope : scopes) {
            msNames.add(missingScope.getName());
        }
        return StringUtils.join(msNames, " and ");
    }

    /**
     * Returns true if this membership has any site scoping information associated.
     * (Not whether it <i>should</i> but whether it does.)
     */
    public boolean hasSiteScope() {
        return hasScope(ScopeType.SITE);
    }

    /**
     * Returns true if this membership has any study scoping information associated.
     * (Not whether it <i>should</i> but whether it does.)
     */
    public boolean hasStudyScope() {
        return hasScope(ScopeType.STUDY);
    }

    public boolean hasScope(ScopeType scope) {
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
    public synchronized List<?> getSites() {
        return getApplicationObjects(ScopeType.SITE);
    }

    /**
     * Returns the application site objects representing the scope of this role.  Invoking this
     * method may result in an object resolve.
     */
    public synchronized List<?> getStudies() {
        return getApplicationObjects(ScopeType.STUDY);
    }

    @SuppressWarnings({ "unchecked" })
    public List<?> getApplicationObjects(ScopeType scope) {
        if (isAll(scope)) {
            throw new SuiteAuthorizationAccessException(
                "This %s has access to every %s.  You can't list %s instances for it.",
                role.getDisplayName(), scope.getName(), scope.getName());
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

    public List<String> getIdentifiers(ScopeType scope) throws SuiteAuthorizationAccessException {
        if (isAll(scope)) {
            throw new SuiteAuthorizationAccessException(
                "This %s has access to every %s.  You can't list %s identifiers for it.",
                role.getDisplayName(), scope.getName(), scope.getName());
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

    public boolean isAll(ScopeType scope) {
        Boolean value = forAll.get(scope);
        if (value == null) {
            return false;
        } else {
            return value;
        }
    }

    protected IdentifiableInstanceMapping getMapping(ScopeType scope) {
        IdentifiableInstanceMapping mapping = mappings.get(scope);
        if (mapping == null) {
            throw new SuiteAuthorizationAccessException(
                "No %s mapping was provided.  Either provide one or stick to the identifier-based methods.",
                scope.getName());
        }
        return mapping;
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
     */
    public List<Difference> diffFromNothing() {
        return new SuiteRoleMembership(getRole(), null, null).diff(this);
    }

    @Override
    public SuiteRoleMembership clone() {
        SuiteRoleMembership clone;
        try {
            clone = (SuiteRoleMembership) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new CommonsError("Clone is supported", e);
        }

        clone.applicationObjectCaches = new HashMap<ScopeType, List<?>>();
        clone.identifiers = new HashMap<ScopeType, List<String>>();
        for (Map.Entry<ScopeType, List<String>> entry : this.identifiers.entrySet()) {
            clone.identifiers.put(entry.getKey(), new ArrayList<String>(entry.getValue()));
        }
        clone.forAll = new HashMap<ScopeType, Boolean>(this.forAll);
        return clone;
    }

    /**
     * A single point of difference between the scopes of two SuiteRoleMemberships.  It's either
     * {@link Kind an add or delete} of either a single identifier or the special "all" scope.
     */
    public static class Difference {
        /**
         * Creates a delete difference for the all scope.
         */
        public static Difference delete(ScopeType scopeType) {
            return new Difference(Kind.DELETE, ScopeDescription.createForAll(scopeType));
        }

        /**
         * Creates a delete difference for a single identifier.
         */
        public static Difference delete(ScopeType scopeType, String ident) {
            return new Difference(Kind.DELETE, ScopeDescription.createForOne(scopeType, ident));
        }

        /**
         * Creates an add difference for the all scope.
         */
        public static Difference add(ScopeType scopeType) {
            return new Difference(Kind.ADD, ScopeDescription.createForAll(scopeType));
        }

        /**
         * Creates an difference for a single identifier.
         */
        public static Difference add(ScopeType scopeType, String ident) {
            return new Difference(Kind.ADD, ScopeDescription.createForOne(scopeType, ident));
        }

        /**
         * The possible kinds of {@link Difference}s.
         */
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
