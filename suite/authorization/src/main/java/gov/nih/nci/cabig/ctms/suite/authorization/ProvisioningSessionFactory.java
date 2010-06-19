package gov.nih.nci.cabig.ctms.suite.authorization;

import gov.nih.nci.security.AuthorizationManager;

/**
 * Creates {@link ProvisioningSession}s.  While provisioning sessions should be used within a single
 * request, this class is intended to be used as an application-level singleton.
 *
 * @author Rhett Sutphin
 */
@SuppressWarnings({ "RawUseOfParameterizedType" })
public class ProvisioningSessionFactory {
    private AuthorizationManager authorizationManager;
    private SiteMapping siteMapping;
    private StudyMapping studyMapping;
    private SuiteRoleMembershipLoader suiteRoleMembershipLoader;
    private CsmHelper csmHelper;

    /**
     * Assists in creating {@link SuiteRoleMembership}s which use the same configuration as this factory.
     * E.g., you might use it like so:
     * <code><pre>
     * long userId;
     * gov.nih.nci.ctms.someapp.domain.Study someStudy;
     * // ...
     * ProvisioningSession ps = factory.createSession(userId);
     * ps.replaceRole(factory.createSuiteRoleMembership(Role.DATA_READER).forAllSites().forStudies(someStudy));
     * </pre></code>
     *
     * @see ProvisioningSession#getProvisionableRoleMembership
     */
    public SuiteRoleMembership createSuiteRoleMembership(SuiteRole role) {
        return new SuiteRoleMembership(role, getSiteMapping(), getStudyMapping());
    }

    /**
     * The main factory method.
     *
     * @param userId
     */
    public ProvisioningSession createSession(long userId) {
        return new ProvisioningSession(userId, this);
    }

    ///// CONFIGURATION

    protected synchronized CsmHelper getCsmHelper() {
        if (csmHelper == null) {
            csmHelper = new CsmHelper();
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

    protected synchronized SuiteRoleMembershipLoader getSuiteRoleMembershipLoader() {
        if (suiteRoleMembershipLoader == null) {
            suiteRoleMembershipLoader = new SuiteRoleMembershipLoader();
            suiteRoleMembershipLoader.setAuthorizationManager(getAuthorizationManager());
            suiteRoleMembershipLoader.setSiteMapping(getSiteMapping());
            suiteRoleMembershipLoader.setStudyMapping(getStudyMapping());
        }
        return suiteRoleMembershipLoader;
    }

    /**
     * Set the {@link SuiteRoleMembershipLoader} to use.  If none is provided, an instance will be created on use.
     */
    public void setSuiteRoleMembershipLoader(SuiteRoleMembershipLoader suiteRoleMembershipLoader) {
        this.suiteRoleMembershipLoader = suiteRoleMembershipLoader;
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
