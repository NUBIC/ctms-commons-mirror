package gov.nih.nci.cabig.ctms.suite.authorization;

/** The scopes which may apply to a role. */
public enum ScopeType {
    SITE("HealthcareSite", "sites"),
    STUDY("Study", "studies")
    ;

    private final String allScopeCsmName;
    private final String scopeCsmNamePrefix;
    private final String pluralName;

    ScopeType(String csmName, String pluralName) {
        this.allScopeCsmName = csmName;
        this.scopeCsmNamePrefix = csmName + '.';
        this.pluralName = pluralName;
    }

    /**
     * The name for the CSM PG/PE pair indicating that the user has access within all the
     * objects of this type.
     */
    public String getAllScopeCsmName() {
        return allScopeCsmName;
    }

    /**
     * The prefix for the names of the CSM PG/PE pairs which indicate that the user has access to
     * a single object of this type.
     */
    public String getScopeCsmNamePrefix() {
        return scopeCsmNamePrefix;
    }

    public String getName() {
        return name().toLowerCase();
    }

    public String getPluralName() {
        return pluralName;
    }
}
