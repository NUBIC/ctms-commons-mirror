package gov.nih.nci.cabig.ctms.suite.authorization;

import gov.nih.nci.security.authorization.domainobjects.ProtectionElement;
import gov.nih.nci.security.authorization.domainobjects.ProtectionGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A logical representation of a single scoping element.  Includes conversion from PG/PEs.
 * Immutable.
 *
 * @author Rhett Sutphin
 */
public class ScopeDescription {
    private static final Logger log = LoggerFactory.getLogger(ScopeDescription.class);

    private ScopeType scope;
    private String identifier;

    ////// FACTORIES

    /**
     * Creates an instance which indicates scoping to all objects of the given type.
     * @param scope
     * @return
     */
    public static ScopeDescription createForAll(ScopeType scope) {
        return new ScopeDescription(scope, null);
    }

    /**
     * Creates an instance describing scoping to a single scope object.
     * @throws IllegalArgumentException if identifier is null
     */
    public static ScopeDescription createForOne(
        ScopeType scope, String identifer
    ) throws IllegalArgumentException {
        if (identifer != null) {
            return new ScopeDescription(scope, identifer);
        } else {
            throw new IllegalArgumentException("An identifier is required");
        }
    }

    public static ScopeDescription createFrom(ProtectionElement pe) {
        return createFromCsmName(pe.getProtectionElementName());
    }

    public static ScopeDescription createFrom(ProtectionGroup pg) {
        return createFromCsmName(pg.getProtectionGroupName());
    }

    public static ScopeDescription createFromCsmName(String csmName) {
        for (ScopeType scope : ScopeType.values()) {
            if (csmName.equals(scope.getAllScopeCsmName())) {
                return createForAll(scope);
            } else if (csmName.startsWith(scope.getScopeCsmNamePrefix())){
                return createForOne(scope, csmName.substring(scope.getScopeCsmNamePrefix().length()));
            }
        }

        log.debug("{} does not map to any suite role scope", csmName);
        return null;
    }

    protected ScopeDescription(ScopeType scope, String identifier) {
        this.scope = scope;
        this.identifier = identifier;
    }

    ////// LOGIC

    public boolean isAll() {
        return identifier == null;
    }

    public String getCsmName() {
        if (isAll()) {
            return getScope().getAllScopeCsmName();
        } else {
            return getScope().getScopeCsmNamePrefix() + getIdentifier();
        }
    }

    ////// ACCESSORS

    public ScopeType getScope() {
        return scope;
    }

    public String getIdentifier() {
        if (isAll()) {
            throw new SuiteAuthorizationAccessException(
                "This description indicates access to every %s.  You can't get an identifier from it.",
                scope.name().toLowerCase());
        }
        return identifier;
    }
}
