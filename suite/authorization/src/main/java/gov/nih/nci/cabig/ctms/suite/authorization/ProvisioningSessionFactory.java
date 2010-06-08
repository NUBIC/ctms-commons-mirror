package gov.nih.nci.cabig.ctms.suite.authorization;

import gov.nih.nci.security.AuthorizationManager;
import gov.nih.nci.security.dao.AuthorizationDAO;

/**
 * @author Rhett Sutphin
 */
@SuppressWarnings({ "RawUseOfParameterizedType" })
public class ProvisioningSessionFactory {
    private AuthorizationManager authorizationManager;
    private AuthorizationDAO authorizationDao;
    private SiteMapping siteMapping;
    private StudyMapping studyMapping;
    private AuthorizationHelper authorizationHelper;
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
     */
    public SuiteRoleMembership createSuiteRoleMembership(SuiteRole role) {
        return new SuiteRoleMembership(role, getSiteMapping(), getStudyMapping());
    }

    public ProvisioningSession createSession(long userId) {
        return new ProvisioningSession(userId, this);
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
     * Set the {@link AuthorizationHelper} to use.  If none is provided, an instance will be created on use.
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
