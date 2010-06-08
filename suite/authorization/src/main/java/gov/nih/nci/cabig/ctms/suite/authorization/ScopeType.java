package gov.nih.nci.cabig.ctms.suite.authorization;

/** The scopes which may apply to a role. */
public enum ScopeType {
    SITE("HealthcareSite"),
    STUDY("Study")
    ;

    private String allScopeCsmName;
    private String scopeCsmNamePrefix;

    ScopeType(String csmName) {
        this.allScopeCsmName = csmName;
        this.scopeCsmNamePrefix = csmName + '.';
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
}
