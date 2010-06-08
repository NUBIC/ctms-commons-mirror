package gov.nih.nci.cabig.ctms.suite.authorization;

import gov.nih.nci.cabig.ctms.testing.MockRegistry;
import gov.nih.nci.cabig.ctms.suite.authorization.domain.TestSite;
import gov.nih.nci.cabig.ctms.suite.authorization.domain.TestSiteMapping;
import gov.nih.nci.cabig.ctms.suite.authorization.domain.TestStudy;
import gov.nih.nci.cabig.ctms.suite.authorization.domain.TestStudyMapping;
import junit.framework.TestCase;
import static org.easymock.classextension.EasyMock.*;

import java.util.Arrays;

/**
 * @author Rhett Sutphin
 */
@SuppressWarnings({ "unchecked", "RawUseOfParameterizedType" })
public class RoleMembershipTest extends TestCase {
    private MockRegistry mocks;

    private SiteMapping mockSiteMapping;
    private StudyMapping mockStudyMapping;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mocks = new MockRegistry();
        mockSiteMapping = mocks.registerMockFor(SiteMapping.class);
        mockStudyMapping = mocks.registerMockFor(StudyMapping.class);
    }

    public void testRoleIsAccessible() throws Exception {
        assertEquals(Role.BUSINESS_ADMINISTRATOR,
            createMembership(Role.BUSINESS_ADMINISTRATOR).getRole());
    }

    ////// forSites

    public void testAddingSitesByIdentifierVarargs() throws Exception {
        RoleMembership m = createMembership(Role.DATA_READER).forSites("A", "B", "C");
        assertEquals("Wrong number of sites", 3, m.getSiteIdentifiers().size());
        assertEquals("Wrong 1st site", "A", m.getSiteIdentifiers().get(0));
        assertEquals("Wrong 2nd site", "B", m.getSiteIdentifiers().get(1));
        assertEquals("Wrong 3rd site", "C", m.getSiteIdentifiers().get(2));
    }

    public void testAddingSitesByObjectVarargs() throws Exception {
        RoleMembership m = createMembership(Role.DATA_READER).
            forSites(new TestSite("G"), new TestSite("Z"));
        assertEquals("Wrong number of sites", 2, m.getSiteIdentifiers().size());
        assertEquals("Wrong 1st site", "G", m.getSiteIdentifiers().get(0));
        assertEquals("Wrong 2nd site", "Z", m.getSiteIdentifiers().get(1));
    }

    public void testAddingSitesByIdentifierCollection() throws Exception {
        RoleMembership m = createMembership(Role.DATA_READER).
            forSites(Arrays.asList("A", "B", "C"));
        assertEquals("Wrong number of sites", 3, m.getSiteIdentifiers().size());
        assertEquals("Wrong 1st site", "A", m.getSiteIdentifiers().get(0));
        assertEquals("Wrong 2nd site", "B", m.getSiteIdentifiers().get(1));
        assertEquals("Wrong 3rd site", "C", m.getSiteIdentifiers().get(2));
    }

    public void testAddingSitesByObjectList() throws Exception {
        RoleMembership m = createMembership(Role.DATA_READER).
            forSites(Arrays.asList(new TestSite("G"), new TestSite("Z")));
        assertEquals("Wrong number of sites", 2, m.getSiteIdentifiers().size());
        assertEquals("Wrong 1st site", "G", m.getSiteIdentifiers().get(0));
        assertEquals("Wrong 2nd site", "Z", m.getSiteIdentifiers().get(1));
    }

    public void testSitesAddedAsIdentifiersAreResolvedToSites() throws Exception {
        RoleMembership m = createMembership(Role.DATA_READER).
            forSites("E", "L");
        assertEquals("Wrong 1st site", "E", ((TestSite) m.getSites().get(0)).getIdent());
        assertEquals("Wrong 2nd site", "L", ((TestSite) m.getSites().get(1)).getIdent());
    }

    public void testSitesAddedAsObjectsAreAutomaticallyCached() throws Exception {
        expect(mockSiteMapping.isInstance(notNull())).andReturn(true);
        expect(mockSiteMapping.getSharedIdentity(notNull())).andReturn("Z");
        // getApplicationInstances _not_ called
        mocks.replayMocks();

        RoleMembership m = createMockMappingMembership(Role.DATA_ANALYST).
            forSites(new TestSite("F"));
        assertEquals("Wrong actual site", "F", ((TestSite) m.getSites().get(0)).getIdent());
        mocks.verifyMocks();
    }

    public void testSiteObjectsAddedLastWin() throws Exception {
        RoleMembership m = createMembership(Role.AE_REPORTER).
            forSites("A").forSites(new TestSite("B"));
        assertEquals("Wrong number of sites", 1, m.getSiteIdentifiers().size());
        assertEquals("Wrong site won", "B", m.getSiteIdentifiers().get(0));
        assertEquals("Wrong site won", "B", ((TestSite) m.getSites().get(0)).getIdent());
    }

    public void testSiteIdentifiersAddedLastWin() throws Exception {
        RoleMembership m = createMembership(Role.AE_REPORTER).
            forSites(new TestSite("B")).forSites("A");
        assertEquals("Wrong number of sites", 1, m.getSiteIdentifiers().size());
        assertEquals("Wrong site won", "A", m.getSiteIdentifiers().get(0));
        assertEquals("Wrong site won", "A", ((TestSite) m.getSites().get(0)).getIdent());
    }

    ////// forStudies

    public void testAddingStudiesByIdentifierVarargs() throws Exception {
        RoleMembership m = createMembership(Role.DATA_READER).forStudies("A", "B", "C");
        assertEquals("Wrong number of studies", 3, m.getStudyIdentifiers().size());
        assertEquals("Wrong 1st study", "A", m.getStudyIdentifiers().get(0));
        assertEquals("Wrong 2nd study", "B", m.getStudyIdentifiers().get(1));
        assertEquals("Wrong 3rd study", "C", m.getStudyIdentifiers().get(2));
    }

    public void testAddingStudiesByObjectVarargs() throws Exception {
        RoleMembership m = createMembership(Role.DATA_READER).
            forStudies(new TestStudy("G"), new TestStudy("Z"));
        assertEquals("Wrong number of studies", 2, m.getStudyIdentifiers().size());
        assertEquals("Wrong 1st study", "G", m.getStudyIdentifiers().get(0));
        assertEquals("Wrong 2nd study", "Z", m.getStudyIdentifiers().get(1));
    }

    public void testAddingStudiesByIdentifierCollection() throws Exception {
        RoleMembership m = createMembership(Role.DATA_READER).
            forStudies(Arrays.asList("A", "B", "C"));
        assertEquals("Wrong number of studies", 3, m.getStudyIdentifiers().size());
        assertEquals("Wrong 1st study", "A", m.getStudyIdentifiers().get(0));
        assertEquals("Wrong 2nd study", "B", m.getStudyIdentifiers().get(1));
        assertEquals("Wrong 3rd study", "C", m.getStudyIdentifiers().get(2));
    }

    public void testAddingStudiesByObjectList() throws Exception {
        RoleMembership m = createMembership(Role.DATA_READER).
            forStudies(Arrays.asList(new TestStudy("G"), new TestStudy("Z")));
        assertEquals("Wrong number of studies", 2, m.getStudyIdentifiers().size());
        assertEquals("Wrong 1st study", "G", m.getStudyIdentifiers().get(0));
        assertEquals("Wrong 2nd study", "Z", m.getStudyIdentifiers().get(1));
    }

    public void testStudiesAddedAsIdentifiersAreResolvedToStudies() throws Exception {
        RoleMembership m = createMembership(Role.DATA_READER).forStudies("E", "L");
        assertEquals("Wrong number of studies", 2, m.getStudies().size());
        assertEquals("Wrong 1st study", "E", ((TestStudy) m.getStudies().get(0)).getIdent());
        assertEquals("Wrong 2nd study", "L", ((TestStudy) m.getStudies().get(1)).getIdent());
    }

    public void testStudiesAddedAsObjectsAreAutomaticallyCached() throws Exception {
        expect(mockStudyMapping.isInstance(notNull())).andReturn(true);
        expect(mockStudyMapping.getSharedIdentity(notNull())).andReturn("Z");
        // getApplicationInstances _not_ called
        mocks.replayMocks();

        RoleMembership m = createMockMappingMembership(Role.DATA_ANALYST).
            forStudies(new TestStudy("F"));
        assertEquals("Wrong actual study", "F", ((TestStudy) m.getStudies().get(0)).getIdent());
        mocks.verifyMocks();
    }

    public void testStudyObjectsAddedLastWin() throws Exception {
        RoleMembership m = createMembership(Role.AE_REPORTER).
            forStudies("A").forStudies(new TestStudy("B"));
        assertEquals("Wrong number of studies", 1, m.getStudyIdentifiers().size());
        assertEquals("Wrong study won", "B", m.getStudyIdentifiers().get(0));
        assertEquals("Wrong study won", "B", ((TestStudy) m.getStudies().get(0)).getIdent());
    }

    public void testStudyIdentifiersAddedLastWin() throws Exception {
        RoleMembership m = createMembership(Role.AE_REPORTER).
            forStudies(new TestStudy("B")).forStudies("A");
        assertEquals("Wrong number of studies", 1, m.getStudyIdentifiers().size());
        assertEquals("Wrong study won", "A", m.getStudyIdentifiers().get(0));
        assertEquals("Wrong study won", "A", ((TestStudy) m.getStudies().get(0)).getIdent());
    }

    ////// all scope objects

    public void testForAllSites() throws Exception {
        RoleMembership m = createMembership(Role.AE_REPORTER).forAllSites();
        assertTrue("Should be for all sites", m.isAllSites());
    }
    
    public void testAllSitesWinsWhenLast() throws Exception {
        RoleMembership m = createMembership(Role.DATA_READER).forSites("B", "T").forAllSites();
        assertTrue("Should be for all sites", m.isAllSites());
    }

    public void testAllSitesLosesWhenNotLast() throws Exception {
        RoleMembership m = createMembership(Role.DATA_READER).forAllSites().forSites("B", "T");
        assertFalse("Shouldn't be for all sites", m.isAllSites());
    }

    public void testForAllStudies() throws Exception {
        RoleMembership m = createMembership(Role.AE_REPORTER).forAllStudies();
        assertTrue("Should be for all studies", m.isAllStudies());
    }

    public void testAllStudiesWinsWhenLast() throws Exception {
        RoleMembership m = createMembership(Role.DATA_READER).forStudies("B", "T").forAllStudies();
        assertTrue("Should be for all studies", m.isAllStudies());
    }

    public void testAllStudiesLosesWhenNotLast() throws Exception {
        RoleMembership m = createMembership(Role.DATA_READER).forAllStudies().forStudies("B", "T");
        assertFalse("Shouldn't be for all studies", m.isAllStudies());
    }

    public void testCannotGetSiteIdentifiersWhenIsAllSites() throws Exception {
        RoleMembership m = createMembership(Role.AE_REPORTER).forAllSites();
        try {
            m.getSiteIdentifiers();
            fail("Exception not thrown");
        } catch (SuiteAuthorizationAccessException aae) {
            assertEquals(
                "This AE Reporter has access to every site.  You can't list site identifiers for it.", 
                aae.getMessage());
        }
    }

    public void testCannotGetStudyIdentifiersWhenIsAllStudies() throws Exception {
        RoleMembership m = createMembership(Role.DATA_ANALYST).forAllStudies();
        try {
            m.getStudyIdentifiers();
            fail("Exception not thrown");
        } catch (SuiteAuthorizationAccessException aae) {
            assertEquals(
                "This Data Analyst has access to every study.  You can't list study identifiers for it.", 
                aae.getMessage());
        }
    }

    public void testCannotGetSitesWhenIsAllSites() throws Exception {
        RoleMembership m = createMembership(Role.AE_REPORTER).forAllSites();
        try {
            m.getSites();
            fail("Exception not thrown");
        } catch (SuiteAuthorizationAccessException aae) {
            assertEquals(
                "This AE Reporter has access to every site.  You can't list site instances for it.",
                aae.getMessage());
        }
    }

    public void testCannotGetStudiesWhenIsAllStudies() throws Exception {
        RoleMembership m = createMembership(Role.DATA_ANALYST).forAllStudies();
        try {
            m.getStudies();
            fail("Exception not thrown");
        } catch (SuiteAuthorizationAccessException aae) {
            assertEquals(
                "This Data Analyst has access to every study.  You can't list study instances for it.", 
                aae.getMessage());
        }
    }

    ////// validation

    public void testAnUnscopedRoleWithNoScopesIsValid() throws Exception {
        assertValid(createMembership(Role.SYSTEM_ADMINISTRATOR));
    }

    public void testAnUnscopedRoleWithAllSiteScopeIsInvalid() throws Exception {
        assertInvalid(createMembership(Role.SYSTEM_ADMINISTRATOR).forAllSites(),
            "The System Administrator role is not scoped to site.");
    }

    public void testAnUnscopedRoleWithSpecificSiteScopeIsInvalid() throws Exception {
        assertInvalid(createMembership(Role.SYSTEM_ADMINISTRATOR).forSites("L"),
            "The System Administrator role is not scoped to site.");
    }

    public void testAnUnscopedRoleWithAllStudyScopeIsInvalid() throws Exception {
        assertInvalid(createMembership(Role.SYSTEM_ADMINISTRATOR).forAllStudies(),
            "The System Administrator role is not scoped to study.");
    }

    public void testAnUnscopedRoleWithSpecificStudyScopeIsInvalid() throws Exception {
        assertInvalid(createMembership(Role.SYSTEM_ADMINISTRATOR).forStudies("L"),
            "The System Administrator role is not scoped to study.");
    }

    public void testASiteOnlyRoleWithSpecificSiteScopesIsValid() throws Exception {
        assertValid(createMembership(Role.STUDY_CREATOR).forSites("G"));
    }

    public void testASiteOnlyRoleWithAllSiteScopeIsValid() throws Exception {
        assertValid(createMembership(Role.STUDY_CREATOR).forAllSites());
    }

    public void testHavingStudyScopeWithASiteOnlyRoleIsInvalid() throws Exception {
        assertInvalid(createMembership(Role.STUDY_CREATOR).forSites("A").forStudies("CRM114"),
            "The Study Creator role is not scoped to study.");
    }

    public void testHavingAllStudyScopeWithASiteOnlyRoleIsInvalid() throws Exception {
        assertInvalid(createMembership(Role.STUDY_CREATOR).forSites("A").forAllStudies(),
            "The Study Creator role is not scoped to study.");
    }

    public void testASiteOnlyRoleWithoutSiteScopeIsInvalid() throws Exception {
        assertInvalid(createMembership(Role.STUDY_CREATOR),
            "The Study Creator role is scoped to site.  Please specify the site scope.");
    }

    public void testAStudyAndSiteRoleWithStudiesAndSitesIsValid() throws Exception {
        assertValid(createMembership(Role.DATA_ANALYST).forStudies("CRM114").forSites("B"));
    }

    public void testAStudyAndSiteRoleWithAllStudiesIsValid() throws Exception {
        assertValid(createMembership(Role.DATA_ANALYST).forAllStudies().forSites("B"));
    }

    public void testAStudyAndSiteRoleWithAllSitesIsValid() throws Exception {
        assertValid(createMembership(Role.DATA_ANALYST).forAllSites().forStudies("CRM114"));
    }

    public void testAStudyAndSiteRoleWithoutSitesIsInvalid() throws Exception {
        assertInvalid(createMembership(Role.DATA_ANALYST).forStudies("CRM114"),
            "The Data Analyst role is scoped to site.  Please specify the site scope.");
    }

    public void testAStudyAndSiteRoleWithoutStudiesIsInvalid() throws Exception {
        assertInvalid(createMembership(Role.DATA_ANALYST).forSites("T"),
            "The Data Analyst role is scoped to study.  Please specify the study scope.");
    }

    public void testAStudyAndSiteRoleWithoutAnyScopeIsInvalid() throws Exception {
        assertInvalid(createMembership(Role.DATA_ANALYST),
            "The Data Analyst role is scoped to site and study.  Please specify the site and study scopes.");
    }

    private void assertValid(RoleMembership membership) {
        try {
            membership.validate();
        } catch (SuiteAuthorizationValidationException save) {
            fail("Incorrectly invalid: " + save.getMessage());
        }
    }

    private void assertInvalid(RoleMembership membership, String expectedMessage) {
        try {
            membership.validate();
            fail("Incorrectly valid");
        } catch (SuiteAuthorizationValidationException save) {
            assertEquals("Wrong message", expectedMessage, save.getMessage());
        }
    }

    ////// HELPERS

    private RoleMembership createMembership(Role role) {
        return new RoleMembership(role, new TestSiteMapping(), new TestStudyMapping());
    }

    private RoleMembership createMockMappingMembership(Role role) {
        return new RoleMembership(role, mockSiteMapping, mockStudyMapping);
    }
}