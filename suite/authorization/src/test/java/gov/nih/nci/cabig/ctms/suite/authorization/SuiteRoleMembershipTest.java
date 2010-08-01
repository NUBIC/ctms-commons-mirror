package gov.nih.nci.cabig.ctms.suite.authorization;

import gov.nih.nci.cabig.ctms.suite.authorization.domain.TestSite;
import gov.nih.nci.cabig.ctms.suite.authorization.domain.TestSiteMapping;
import gov.nih.nci.cabig.ctms.suite.authorization.domain.TestStudy;
import gov.nih.nci.cabig.ctms.suite.authorization.domain.TestStudyMapping;
import gov.nih.nci.cabig.ctms.testing.MockRegistry;
import junit.framework.TestCase;

import java.util.Arrays;
import java.util.List;

import static org.easymock.classextension.EasyMock.*;

/**
 * @author Rhett Sutphin
 */
@SuppressWarnings({ "unchecked", "RawUseOfParameterizedType" })
public class SuiteRoleMembershipTest extends TestCase {
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
        assertEquals(SuiteRole.BUSINESS_ADMINISTRATOR,
            createMembership(SuiteRole.BUSINESS_ADMINISTRATOR).getRole());
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public void testSiteListIsCastableToSpecificApplicationType() throws Exception {
        List<TestSite> sites =
            (List<TestSite>) createMembership(SuiteRole.AE_REPORTER).getSites();
        // testing that this cast is compilable
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public void testSiteListItemIsCastableToSpecificApplicationType() throws Exception {
        TestSite site =
            (TestSite) createMembership(SuiteRole.AE_REPORTER).forSites(new TestSite("F")).getSites().get(0);
        // testing that this cast is compilable
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public void testStudyListIsCastableToSpecificApplicationType() throws Exception {
        List<TestStudy> studies =
            (List<TestStudy>) createMembership(SuiteRole.AE_REPORTER).getStudies();
        // testing that this cast is compilable
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public void testStudyListItemIsCastableToSpecificApplicationType() throws Exception {
        TestStudy study =
            (TestStudy) createMembership(SuiteRole.AE_REPORTER).forStudies(new TestStudy("T")).getStudies().get(0);
        // testing that this cast is compilable
    }

    ////// forSites

    public void testAddingSitesByIdentifierVarargs() throws Exception {
        SuiteRoleMembership m = createMembership(SuiteRole.DATA_READER).forSites("A", "B", "C");
        assertEquals("Wrong number of sites", 3, m.getSiteIdentifiers().size());
        assertEquals("Wrong 1st site", "A", m.getSiteIdentifiers().get(0));
        assertEquals("Wrong 2nd site", "B", m.getSiteIdentifiers().get(1));
        assertEquals("Wrong 3rd site", "C", m.getSiteIdentifiers().get(2));
    }

    public void testAddingSitesByObjectVarargs() throws Exception {
        SuiteRoleMembership m = createMembership(SuiteRole.DATA_READER).
            forSites(new TestSite("G"), new TestSite("Z"));
        assertEquals("Wrong number of sites", 2, m.getSiteIdentifiers().size());
        assertEquals("Wrong 1st site", "G", m.getSiteIdentifiers().get(0));
        assertEquals("Wrong 2nd site", "Z", m.getSiteIdentifiers().get(1));
    }

    public void testAddingSitesByIdentifierCollection() throws Exception {
        SuiteRoleMembership m = createMembership(SuiteRole.DATA_READER).
            forSites(Arrays.asList("A", "B", "C"));
        assertEquals("Wrong number of sites", 3, m.getSiteIdentifiers().size());
        assertEquals("Wrong 1st site", "A", m.getSiteIdentifiers().get(0));
        assertEquals("Wrong 2nd site", "B", m.getSiteIdentifiers().get(1));
        assertEquals("Wrong 3rd site", "C", m.getSiteIdentifiers().get(2));
    }

    public void testAddingSitesByObjectList() throws Exception {
        SuiteRoleMembership m = createMembership(SuiteRole.DATA_READER).
            forSites(Arrays.asList(new TestSite("G"), new TestSite("Z")));
        assertEquals("Wrong number of sites", 2, m.getSiteIdentifiers().size());
        assertEquals("Wrong 1st site", "G", m.getSiteIdentifiers().get(0));
        assertEquals("Wrong 2nd site", "Z", m.getSiteIdentifiers().get(1));
    }

    public void testSitesAddedAsIdentifiersAreResolvedToSites() throws Exception {
        SuiteRoleMembership m = createMembership(SuiteRole.DATA_READER).
            forSites("E", "L");
        assertEquals("Wrong 1st site", "E", ((TestSite) m.getSites().get(0)).getIdent());
        assertEquals("Wrong 2nd site", "L", ((TestSite) m.getSites().get(1)).getIdent());
    }

    public void testSitesAddedAsObjectsAreAutomaticallyCached() throws Exception {
        expect(mockSiteMapping.isInstance(notNull())).andReturn(true);
        expect(mockSiteMapping.getSharedIdentity(notNull())).andReturn("Z");
        // getApplicationInstances _not_ called
        mocks.replayMocks();

        SuiteRoleMembership m = createMockMappingMembership(SuiteRole.DATA_ANALYST).
            forSites(new TestSite("F"));
        assertEquals("Wrong actual site", "F", ((TestSite) m.getSites().get(0)).getIdent());
        mocks.verifyMocks();
    }

    public void testSiteObjectsAddedLastWin() throws Exception {
        SuiteRoleMembership m = createMembership(SuiteRole.AE_REPORTER).
            forSites("A").forSites(new TestSite("B"));
        assertEquals("Wrong number of sites", 1, m.getSiteIdentifiers().size());
        assertEquals("Wrong site won", "B", m.getSiteIdentifiers().get(0));
        assertEquals("Wrong site won", "B", ((TestSite) m.getSites().get(0)).getIdent());
    }

    public void testSiteIdentifiersAddedLastWin() throws Exception {
        SuiteRoleMembership m = createMembership(SuiteRole.AE_REPORTER).
            forSites(new TestSite("B")).forSites("A");
        assertEquals("Wrong number of sites", 1, m.getSiteIdentifiers().size());
        assertEquals("Wrong site won", "A", m.getSiteIdentifiers().get(0));
        assertEquals("Wrong site won", "A", ((TestSite) m.getSites().get(0)).getIdent());
    }

    ////// forStudies

    public void testAddingStudiesByIdentifierVarargs() throws Exception {
        SuiteRoleMembership m = createMembership(SuiteRole.DATA_READER).forStudies("A", "B", "C");
        assertEquals("Wrong number of studies", 3, m.getStudyIdentifiers().size());
        assertEquals("Wrong 1st study", "A", m.getStudyIdentifiers().get(0));
        assertEquals("Wrong 2nd study", "B", m.getStudyIdentifiers().get(1));
        assertEquals("Wrong 3rd study", "C", m.getStudyIdentifiers().get(2));
    }

    public void testAddingStudiesByObjectVarargs() throws Exception {
        SuiteRoleMembership m = createMembership(SuiteRole.DATA_READER).
            forStudies(new TestStudy("G"), new TestStudy("Z"));
        assertEquals("Wrong number of studies", 2, m.getStudyIdentifiers().size());
        assertEquals("Wrong 1st study", "G", m.getStudyIdentifiers().get(0));
        assertEquals("Wrong 2nd study", "Z", m.getStudyIdentifiers().get(1));
    }

    public void testAddingStudiesByIdentifierCollection() throws Exception {
        SuiteRoleMembership m = createMembership(SuiteRole.DATA_READER).
            forStudies(Arrays.asList("A", "B", "C"));
        assertEquals("Wrong number of studies", 3, m.getStudyIdentifiers().size());
        assertEquals("Wrong 1st study", "A", m.getStudyIdentifiers().get(0));
        assertEquals("Wrong 2nd study", "B", m.getStudyIdentifiers().get(1));
        assertEquals("Wrong 3rd study", "C", m.getStudyIdentifiers().get(2));
    }

    public void testAddingStudiesByObjectList() throws Exception {
        SuiteRoleMembership m = createMembership(SuiteRole.DATA_READER).
            forStudies(Arrays.asList(new TestStudy("G"), new TestStudy("Z")));
        assertEquals("Wrong number of studies", 2, m.getStudyIdentifiers().size());
        assertEquals("Wrong 1st study", "G", m.getStudyIdentifiers().get(0));
        assertEquals("Wrong 2nd study", "Z", m.getStudyIdentifiers().get(1));
    }

    public void testStudiesAddedAsIdentifiersAreResolvedToStudies() throws Exception {
        SuiteRoleMembership m = createMembership(SuiteRole.DATA_READER).forStudies("E", "L");
        assertEquals("Wrong number of studies", 2, m.getStudies().size());
        assertEquals("Wrong 1st study", "E", ((TestStudy) m.getStudies().get(0)).getIdent());
        assertEquals("Wrong 2nd study", "L", ((TestStudy) m.getStudies().get(1)).getIdent());
    }

    public void testStudiesAddedAsObjectsAreAutomaticallyCached() throws Exception {
        expect(mockStudyMapping.isInstance(notNull())).andReturn(true);
        expect(mockStudyMapping.getSharedIdentity(notNull())).andReturn("Z");
        // getApplicationInstances _not_ called
        mocks.replayMocks();

        SuiteRoleMembership m = createMockMappingMembership(SuiteRole.DATA_ANALYST).
            forStudies(new TestStudy("F"));
        assertEquals("Wrong actual study", "F", ((TestStudy) m.getStudies().get(0)).getIdent());
        mocks.verifyMocks();
    }

    public void testStudyObjectsAddedLastWin() throws Exception {
        SuiteRoleMembership m = createMembership(SuiteRole.AE_REPORTER).
            forStudies("A").forStudies(new TestStudy("B"));
        assertEquals("Wrong number of studies", 1, m.getStudyIdentifiers().size());
        assertEquals("Wrong study won", "B", m.getStudyIdentifiers().get(0));
        assertEquals("Wrong study won", "B", ((TestStudy) m.getStudies().get(0)).getIdent());
    }

    public void testStudyIdentifiersAddedLastWin() throws Exception {
        SuiteRoleMembership m = createMembership(SuiteRole.AE_REPORTER).
            forStudies(new TestStudy("B")).forStudies("A");
        assertEquals("Wrong number of studies", 1, m.getStudyIdentifiers().size());
        assertEquals("Wrong study won", "A", m.getStudyIdentifiers().get(0));
        assertEquals("Wrong study won", "A", ((TestStudy) m.getStudies().get(0)).getIdent());
    }

    ////// all scope objects

    public void testForAllSites() throws Exception {
        SuiteRoleMembership m = createMembership(SuiteRole.AE_REPORTER).forAllSites();
        assertTrue("Should be for all sites", m.isAllSites());
    }
    
    public void testAllSitesWinsWhenLast() throws Exception {
        SuiteRoleMembership m = createMembership(SuiteRole.DATA_READER).forSites("B", "T").forAllSites();
        assertTrue("Should be for all sites", m.isAllSites());
    }

    public void testAllSitesLosesWhenNotLast() throws Exception {
        SuiteRoleMembership m = createMembership(SuiteRole.DATA_READER).forAllSites().forSites("B", "T");
        assertFalse("Shouldn't be for all sites", m.isAllSites());
    }

    public void testForAllStudies() throws Exception {
        SuiteRoleMembership m = createMembership(SuiteRole.AE_REPORTER).forAllStudies();
        assertTrue("Should be for all studies", m.isAllStudies());
    }

    public void testAllStudiesWinsWhenLast() throws Exception {
        SuiteRoleMembership m = createMembership(SuiteRole.DATA_READER).forStudies("B", "T").forAllStudies();
        assertTrue("Should be for all studies", m.isAllStudies());
    }

    public void testAllStudiesLosesWhenNotLast() throws Exception {
        SuiteRoleMembership m = createMembership(SuiteRole.DATA_READER).forAllStudies().forStudies("B", "T");
        assertFalse("Shouldn't be for all studies", m.isAllStudies());
    }

    public void testCannotGetSiteIdentifiersWhenIsAllSites() throws Exception {
        SuiteRoleMembership m = createMembership(SuiteRole.AE_REPORTER).forAllSites();
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
        SuiteRoleMembership m = createMembership(SuiteRole.DATA_ANALYST).forAllStudies();
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
        SuiteRoleMembership m = createMembership(SuiteRole.AE_REPORTER).forAllSites();
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
        SuiteRoleMembership m = createMembership(SuiteRole.DATA_ANALYST).forAllStudies();
        try {
            m.getStudies();
            fail("Exception not thrown");
        } catch (SuiteAuthorizationAccessException aae) {
            assertEquals(
                "This Data Analyst has access to every study.  You can't list study instances for it.", 
                aae.getMessage());
        }
    }

    ////// adding and removing

    public void testAddSiteWhenBlank() throws Exception {
        SuiteRoleMembership m = createMembership(SuiteRole.DATA_ANALYST).addSite("F");
        assertEquals("Wrong number of sites", 1, m.getSiteIdentifiers().size());
        assertEquals("Wrong 1st site", "F", m.getSiteIdentifiers().get(0));
    }

    public void testAddSiteAppendsIfExistingRole() throws Exception {
        SuiteRoleMembership m = createMembership(SuiteRole.DATA_ANALYST).forSites("B").addSite("F");
        assertEquals("Wrong number of sites", 2, m.getSiteIdentifiers().size());
        assertEquals("Wrong 1st site", "B", m.getSiteIdentifiers().get(0));
        assertEquals("Wrong 1st site", "F", m.getSiteIdentifiers().get(1));
    }

    public void testAddSiteIgnoresIfExistingSite() throws Exception {
        SuiteRoleMembership m = createMembership(SuiteRole.DATA_ANALYST).forSites("B").addSite("B");
        assertEquals("Wrong number of sites", 1, m.getSiteIdentifiers().size());
        assertEquals("Wrong 1st site", "B", m.getSiteIdentifiers().get(0));
    }

    public void testAddSiteWhenAllResetsToNotAll() throws Exception {
        SuiteRoleMembership m = createMembership(SuiteRole.DATA_ANALYST).forAllSites().addSite("F");
        assertFalse("Should not be for all", m.isAllSites());
        assertEquals("Wrong number of sites", 1, m.getSiteIdentifiers().size());
        assertEquals("Wrong 1st site", "F", m.getSiteIdentifiers().get(0));
    }

    public void testAddSiteObject() throws Exception {
        SuiteRoleMembership m = createMembership(SuiteRole.DATA_ANALYST).
            forSites("B").addSite(new TestSite("F"));
        assertEquals("Wrong number of sites", 2, m.getSiteIdentifiers().size());
        assertEquals("Wrong 1st site", "B", m.getSiteIdentifiers().get(0));
        assertEquals("Wrong 1st site", "F", m.getSiteIdentifiers().get(1));
    }

    public void testAddSiteWithUnknownObjectTypeFails() throws Exception {
        try {
            createMembership(SuiteRole.DATA_ANALYST).addSite(new Object());
            fail("Exception not thrown");
        } catch (SuiteAuthorizationValidationException save) {
            assertEquals("Wrong message",
                "Attempted to add an instance of java.lang.Object as a site scope object.  This is not an acceptable type; check your mapping.",
                save.getMessage());
        }
    }

    public void testAddStudyWhenBlank() throws Exception {
        SuiteRoleMembership m = createMembership(SuiteRole.DATA_ANALYST).addStudy("F");
        assertEquals("Wrong number of studies", 1, m.getStudyIdentifiers().size());
        assertEquals("Wrong 1st study", "F", m.getStudyIdentifiers().get(0));
    }

    public void testAddStudyAppendsIfExistingRole() throws Exception {
        SuiteRoleMembership m = createMembership(SuiteRole.DATA_ANALYST).forStudies("B").addStudy("F");
        assertEquals("Wrong number of studies", 2, m.getStudyIdentifiers().size());
        assertEquals("Wrong 1st study", "B", m.getStudyIdentifiers().get(0));
        assertEquals("Wrong 1st study", "F", m.getStudyIdentifiers().get(1));
    }

    public void testAddStudyIgnoresIfExistingStudy() throws Exception {
        SuiteRoleMembership m = createMembership(SuiteRole.DATA_ANALYST).forStudies("B").addStudy("B");
        assertEquals("Wrong number of studies", 1, m.getStudyIdentifiers().size());
        assertEquals("Wrong 1st site", "B", m.getStudyIdentifiers().get(0));
    }

    public void testAddStudyWhenAllResetsToNotAll() throws Exception {
        SuiteRoleMembership m = createMembership(SuiteRole.DATA_ANALYST).forAllStudies().addStudy("F");
        assertFalse("Should not be for all", m.isAllStudies());
        assertEquals("Wrong number of studies", 1, m.getStudyIdentifiers().size());
        assertEquals("Wrong 1st study", "F", m.getStudyIdentifiers().get(0));
    }

    public void testAddStudyObject() throws Exception {
        SuiteRoleMembership m = createMembership(SuiteRole.DATA_ANALYST).
            forStudies("B").addStudy(new TestStudy("F"));
        assertEquals("Wrong number of studies", 2, m.getStudyIdentifiers().size());
        assertEquals("Wrong 1st study", "B", m.getStudyIdentifiers().get(0));
        assertEquals("Wrong 1st study", "F", m.getStudyIdentifiers().get(1));
    }

    public void testAddStudyWithUnknownObjectTypeFails() throws Exception {
        try {
            createMembership(SuiteRole.DATA_ANALYST).addStudy(new Object());
            fail("Exception not thrown");
        } catch (SuiteAuthorizationValidationException save) {
            assertEquals("Wrong message",
                "Attempted to add an instance of java.lang.Object as a study scope object.  This is not an acceptable type; check your mapping.",
                save.getMessage());
        }
    }

    public void testRemoveNonExistentSiteDoesNothing() throws Exception {
        SuiteRoleMembership m = createMembership(SuiteRole.DATA_ANALYST).forSites("F", "I").removeSite("B");
        assertEquals("Wrong number of sites", 2, m.getSiteIdentifiers().size());
        assertEquals("Wrong 1st site", "F", m.getSiteIdentifiers().get(0));
        assertEquals("Wrong 2nd site", "I", m.getSiteIdentifiers().get(1));
    }

    public void testRemoveSiteWhenEmptyDoesNothing() throws Exception {
        SuiteRoleMembership m = createMembership(SuiteRole.DATA_ANALYST).removeSite("B");
        assertEquals("Wrong number of sites", 0, m.getSiteIdentifiers().size());
    }

    public void testRemoveExistingSiteWorks() throws Exception {
        SuiteRoleMembership m = createMembership(SuiteRole.DATA_ANALYST).forSites("B", "I").removeSite("B");
        assertEquals("Wrong number of sites", 1, m.getSiteIdentifiers().size());
        assertEquals("Wrong 1st site", "I", m.getSiteIdentifiers().get(0));
    }

    public void testRemoveSiteObjectWorks() throws Exception {
        SuiteRoleMembership m = createMembership(SuiteRole.DATA_ANALYST).
            forSites("B", "F").removeSite(new TestSite("F"));
        assertEquals("Wrong number of sites", 1, m.getSiteIdentifiers().size());
        assertEquals("Wrong 1st site", "B", m.getSiteIdentifiers().get(0));
    }

    public void testRemoveAllSitesScope() throws Exception {
        SuiteRoleMembership m = createMembership(SuiteRole.DATA_ANALYST).forAllSites().notForAllSites();
        assertFalse("Should not be for all sites", m.isAllSites());
        assertTrue("Should have no specific sites either", m.getSiteIdentifiers().isEmpty());
    }

    public void testRemoveNonExistentStudyDoesNothing() throws Exception {
        SuiteRoleMembership m = createMembership(SuiteRole.DATA_ANALYST).forStudies("F", "I").removeStudy("B");
        assertEquals("Wrong number of studies", 2, m.getStudyIdentifiers().size());
        assertEquals("Wrong 1st study", "F", m.getStudyIdentifiers().get(0));
        assertEquals("Wrong 2nd study", "I", m.getStudyIdentifiers().get(1));
    }

    public void testRemoveStudyWhenEmptyDoesNothing() throws Exception {
        SuiteRoleMembership m = createMembership(SuiteRole.DATA_ANALYST).removeStudy("B");
        assertEquals("Wrong number of studies", 0, m.getStudyIdentifiers().size());
    }

    public void testRemoveExistingStudyWorks() throws Exception {
        SuiteRoleMembership m = createMembership(SuiteRole.DATA_ANALYST).forStudies("B", "I").removeStudy("B");
        assertEquals("Wrong number of studies", 1, m.getStudyIdentifiers().size());
        assertEquals("Wrong 1st study", "I", m.getStudyIdentifiers().get(0));
    }

    public void testRemoveStudyObjectWorks() throws Exception {
        SuiteRoleMembership m = createMembership(SuiteRole.DATA_ANALYST).
            forStudies("B", "F").removeStudy(new TestStudy("F"));
        assertEquals("Wrong number of studies", 1, m.getStudyIdentifiers().size());
        assertEquals("Wrong 1st study", "B", m.getStudyIdentifiers().get(0));
    }

    public void testRemoveAllStudiesScope() throws Exception {
        SuiteRoleMembership m = createMembership(SuiteRole.DATA_ANALYST).forAllStudies().notForAllStudies();
        assertFalse("Should not be for all studies", m.isAllStudies());
        assertTrue("Should have no specific studies either", m.getStudyIdentifiers().isEmpty());
    }

    ////// validation

    public void testAnUnscopedRoleWithNoScopesIsValid() throws Exception {
        assertValidAndComplete(createMembership(SuiteRole.SYSTEM_ADMINISTRATOR));
    }

    public void testAnUnscopedRoleWithAllSiteScopeIsInvalid() throws Exception {
        assertInvalid(createMembership(SuiteRole.SYSTEM_ADMINISTRATOR).forAllSites(),
            "The System Administrator role is not scoped to site.");
    }

    public void testAnUnscopedRoleWithSpecificSiteScopeIsInvalid() throws Exception {
        assertInvalid(createMembership(SuiteRole.SYSTEM_ADMINISTRATOR).forSites("L"),
            "The System Administrator role is not scoped to site.");
    }

    public void testAnUnscopedRoleWithAllStudyScopeIsInvalid() throws Exception {
        assertInvalid(createMembership(SuiteRole.SYSTEM_ADMINISTRATOR).forAllStudies(),
            "The System Administrator role is not scoped to study.");
    }

    public void testAnUnscopedRoleWithSpecificStudyScopeIsInvalid() throws Exception {
        assertInvalid(createMembership(SuiteRole.SYSTEM_ADMINISTRATOR).forStudies("L"),
            "The System Administrator role is not scoped to study.");
    }

    public void testASiteOnlyRoleWithSpecificSiteScopesIsValid() throws Exception {
        assertValidAndComplete(createMembership(SuiteRole.STUDY_CREATOR).forSites("G"));
    }

    public void testASiteOnlyRoleWithAllSiteScopeIsValid() throws Exception {
        assertValidAndComplete(createMembership(SuiteRole.STUDY_CREATOR).forAllSites());
    }

    public void testHavingStudyScopeWithASiteOnlyRoleIsInvalid() throws Exception {
        assertInvalid(createMembership(SuiteRole.STUDY_CREATOR).forSites("A").forStudies("CRM114"),
            "The Study Creator role is not scoped to study.");
    }

    public void testHavingAllStudyScopeWithASiteOnlyRoleIsInvalid() throws Exception {
        assertInvalid(createMembership(SuiteRole.STUDY_CREATOR).forSites("A").forAllStudies(),
            "The Study Creator role is not scoped to study.");
    }

    public void testASiteOnlyRoleWithoutSiteScopeIsValidButNotComplete() throws Exception {
        assertValidButIncomplete(createMembership(SuiteRole.STUDY_CREATOR),
            "The Study Creator role is scoped to site.  Please specify the site scope.");
    }

    public void testAStudyAndSiteRoleWithStudiesAndSitesIsValidAndComplete() throws Exception {
        assertValidAndComplete(createMembership(SuiteRole.DATA_ANALYST).forStudies("CRM114").forSites("B"));
    }

    public void testAStudyAndSiteRoleWithAllStudiesIsValid() throws Exception {
        assertValidAndComplete(createMembership(SuiteRole.DATA_ANALYST).forAllStudies().forSites("B"));
    }

    public void testAStudyAndSiteRoleWithAllSitesIsValid() throws Exception {
        assertValidAndComplete(createMembership(SuiteRole.DATA_ANALYST).forAllSites().forStudies("CRM114"));
    }

    public void testAStudyAndSiteRoleWithoutSitesIsInvalid() throws Exception {
        assertValidButIncomplete(createMembership(SuiteRole.DATA_ANALYST).forStudies("CRM114"),
            "The Data Analyst role is scoped to site.  Please specify the site scope.");
    }

    public void testAStudyAndSiteRoleWithoutStudiesIsInvalid() throws Exception {
        assertValidButIncomplete(createMembership(SuiteRole.DATA_ANALYST).forSites("T"),
            "The Data Analyst role is scoped to study.  Please specify the study scope.");
    }

    public void testAStudyAndSiteRoleWithoutAnyScopeIsInvalid() throws Exception {
        assertValidButIncomplete(createMembership(SuiteRole.DATA_ANALYST),
            "The Data Analyst role is scoped to site and study.  Please specify the site and study scopes.");
    }

    private void assertValid(SuiteRoleMembership membership) {
        try {
            membership.validate();
        } catch (SuiteAuthorizationValidationException save) {
            fail("Incorrectly invalid: " + save.getMessage());
        }
    }

    private void assertInvalid(SuiteRoleMembership membership, String expectedMessage) {
        try {
            membership.validate();
            fail("Incorrectly valid");
        } catch (SuiteAuthorizationValidationException save) {
            assertEquals("Wrong message", expectedMessage, save.getMessage());
        }
    }

    private void assertComplete(SuiteRoleMembership membership) {
        try {
            membership.checkComplete();
        } catch (SuiteAuthorizationValidationException save) {
            fail("Incorrectly incomplete: " + save.getMessage());
        }
    }

    private void assertIncomplete(SuiteRoleMembership membership, String expectedMessage) {
        try {
            membership.checkComplete();
            fail("Incorrectly complete");
        } catch (SuiteAuthorizationValidationException save) {
            assertEquals("Wrong message", expectedMessage, save.getMessage());
        }
    }

    private void assertValidAndComplete(SuiteRoleMembership membership) {
        assertValid(membership);
        assertComplete(membership);
    }

    private void assertValidButIncomplete(SuiteRoleMembership membership, String expectedIncompletenessMessage) {
        assertValid(membership);
        assertIncomplete(membership, expectedIncompletenessMessage);
    }

    ////// diff

    public void testAddOneSite() throws Exception {
        List<SuiteRoleMembership.Difference> actual =
            createMembership(SuiteRole.USER_ADMINISTRATOR).forSites("A").diff(
                createMembership(SuiteRole.USER_ADMINISTRATOR).forSites("A", "B"));
        assertEquals("Wrong number of changes", 1, actual.size());
        assertAdd("Wrong difference", actual.get(0),
            ScopeDescription.createForOne(ScopeType.SITE, "B"));
    }

    public void testNoDifferences() throws Exception {
        List<SuiteRoleMembership.Difference> actual =
            createMembership(SuiteRole.USER_ADMINISTRATOR).forSites("A").diff(
                createMembership(SuiteRole.USER_ADMINISTRATOR).forSites("A"));
        assertEquals("Wrong number of changes", 0, actual.size());
    }

    public void testConvertFromIndividualSitesToAllSites() throws Exception {
        List<SuiteRoleMembership.Difference> actual =
            createMembership(SuiteRole.USER_ADMINISTRATOR).forSites("A", "B").diff(
                createMembership(SuiteRole.USER_ADMINISTRATOR).forAllSites());
        assertEquals("Wrong number of changes", 3, actual.size());
        assertAdd("Wrong 1st difference", actual.get(0),
            ScopeDescription.createForAll(ScopeType.SITE));
        assertDelete("Wrong 2nd difference", actual.get(1),
            ScopeDescription.createForOne(ScopeType.SITE, "A"));
        assertDelete("Wrong 3rd difference", actual.get(2),
            ScopeDescription.createForOne(ScopeType.SITE, "B"));
    }

    public void testConvertFromAllSitesToIndividualSites() throws Exception {
        List<SuiteRoleMembership.Difference> actual =
            createMembership(SuiteRole.USER_ADMINISTRATOR).forAllSites().diff(
                createMembership(SuiteRole.USER_ADMINISTRATOR).forSites("G", "T"));
        assertEquals("Wrong number of changes", 3, actual.size());
        assertDelete("Wrong 1st difference", actual.get(0),
            ScopeDescription.createForAll(ScopeType.SITE));
        assertAdd("Wrong 2nd difference", actual.get(1),
            ScopeDescription.createForOne(ScopeType.SITE, "G"));
        assertAdd("Wrong 3rd difference", actual.get(2),
            ScopeDescription.createForOne(ScopeType.SITE, "T"));
    }

    public void testChangeSitesAndStudiesSimultaneously() throws Exception {
        List<SuiteRoleMembership.Difference> actual =
            createMembership(SuiteRole.DATA_ANALYST).forAllSites().forStudies("CRM").diff(
                createMembership(SuiteRole.DATA_ANALYST).forSites("G", "T").forStudies("114", "Z"));
        assertEquals("Wrong number of changes", 6, actual.size());
        assertDelete("Wrong 1st difference", actual.get(0),
            ScopeDescription.createForAll(ScopeType.SITE));
        assertAdd("Wrong 2nd difference", actual.get(1),
            ScopeDescription.createForOne(ScopeType.SITE, "G"));
        assertAdd("Wrong 3rd difference", actual.get(2),
            ScopeDescription.createForOne(ScopeType.SITE, "T"));
        assertDelete("Wrong 4th difference", actual.get(3),
            ScopeDescription.createForOne(ScopeType.STUDY, "CRM"));
        assertAdd("Wrong 5th difference", actual.get(4),
            ScopeDescription.createForOne(ScopeType.STUDY, "114"));
        assertAdd("Wrong 6th difference", actual.get(5),
            ScopeDescription.createForOne(ScopeType.STUDY, "Z"));
    }

    public void testChangeToBlank() throws Exception {
        List<SuiteRoleMembership.Difference> actual =
            createMembership(SuiteRole.DATA_ANALYST).forAllSites().forStudies("CRM").diff(
                createMembership(SuiteRole.DATA_ANALYST));
        assertEquals("Wrong number of changes", 2, actual.size());
        assertDelete("Wrong 1st difference", actual.get(0),
            ScopeDescription.createForAll(ScopeType.SITE));
        assertDelete("Wrong 2nd difference", actual.get(1),
            ScopeDescription.createForOne(ScopeType.STUDY, "CRM"));
    }

    public void testDifferenceFromBaseline() throws Exception {
        List<SuiteRoleMembership.Difference> actual =
            createMembership(SuiteRole.DATA_ANALYST).
                forAllSites().forStudies("CRM", "114").diffFromNothing();
        assertEquals("Wrong number of changes", 3, actual.size());
        assertAdd("Wrong 1st add", actual.get(0), ScopeDescription.createForAll(ScopeType.SITE));
        assertAdd("Wrong 2nd add", actual.get(1), ScopeDescription.createForOne(ScopeType.STUDY, "CRM"));
        assertAdd("Wrong 3rd add", actual.get(2), ScopeDescription.createForOne(ScopeType.STUDY, "114"));
    }

    public void testDiffToNull() throws Exception {
        List<SuiteRoleMembership.Difference> actual =
            createMembership(SuiteRole.DATA_ANALYST).
                forAllSites().forStudies("CRM", "114").diff(null);
        assertEquals("Wrong number of changes", 3, actual.size());
        assertDelete("Wrong 1st del", actual.get(0), ScopeDescription.createForAll(ScopeType.SITE));
        assertDelete("Wrong 2nd del", actual.get(1), ScopeDescription.createForOne(ScopeType.STUDY, "CRM"));
        assertDelete("Wrong 3rd del", actual.get(2), ScopeDescription.createForOne(ScopeType.STUDY, "114"));
    }

    private static void assertAdd(String message, SuiteRoleMembership.Difference actual, ScopeDescription expectedSD) {
        assertDifference(message, actual, SuiteRoleMembership.Difference.Kind.ADD, expectedSD);
    }

    private static void assertDelete(String message, SuiteRoleMembership.Difference actual, ScopeDescription expectedSD) {
        assertDifference(message, actual, SuiteRoleMembership.Difference.Kind.DELETE, expectedSD);
    }

    private static void assertDifference(
        String message, SuiteRoleMembership.Difference actual, SuiteRoleMembership.Difference.Kind expectedKind, ScopeDescription expectedSD
    ) {
        assertEquals(message + ": wrong kind", expectedKind, actual.getKind());
        assertEquals(message + ": wrong scope description", expectedSD, actual.getScopeDescription());
    }

    ////// optional mappings
    
    public void testAttemptingToUseAnUnavailableMappingGivesAHelpfulErrorMessage() throws Exception {
        try {
            new SuiteRoleMembership(SuiteRole.DATA_READER, null, null).
                forSites(new TestSite("B"));
            fail("Exception not thrown");
        } catch (SuiteAuthorizationAccessException e) {
            assertEquals("Wrong message",
                "No site mapping was provided.  Either provide one or stick to the identifier-based methods.",
                e.getMessage());
        }
    }

    public void testUsingIdentifiersWhenNoMappingsWorksFine() throws Exception {
        SuiteRoleMembership m = new SuiteRoleMembership(SuiteRole.DATA_READER, null, null).
            forSites("B");
        assertEquals("Wrong number of sites", 1, m.getSiteIdentifiers().size());
    }

    ////// clone

    public void testCloneReturnsNotSame() throws Exception {
        SuiteRoleMembership original = createMembership(SuiteRole.REGISTRAR);
        SuiteRoleMembership actual = original.clone();
        assertNotSame("Not cloned", actual, original);
    }

    public void testCloneIsForSameRole() throws Exception {
        assertEquals(SuiteRole.STUDY_CALENDAR_TEMPLATE_BUILDER,
            createMembership(SuiteRole.STUDY_CALENDAR_TEMPLATE_BUILDER).clone().getRole());
    }

    public void testCloneCopiesAllSitesFlag() throws Exception {
        SuiteRoleMembership actual = createMembership(SuiteRole.REGISTRAR).forAllSites().clone();
        assertTrue("Not copied", actual.isAllSites());
    }

    public void testCloneCopiesAllStudiesFlag() throws Exception {
        SuiteRoleMembership actual = createMembership(SuiteRole.REGISTRAR).forAllStudies().clone();
        assertTrue("Not copied", actual.isAllStudies());
    }

    public void testCloneAllSitesFlagUnlinked() throws Exception {
        SuiteRoleMembership original = createMembership(SuiteRole.REGISTRAR).forAllSites();
        SuiteRoleMembership actual = original.clone();
        original.forSites("A");
        assertTrue("Clone reflects source after cloning", actual.isAllSites());
    }

    public void testCloneAllStudiesFlagUnlinked() throws Exception {
        SuiteRoleMembership original = createMembership(SuiteRole.REGISTRAR).forAllStudies();
        SuiteRoleMembership actual = original.clone();
        original.forStudies("T");
        assertTrue("Clone reflects source after cloning", actual.isAllStudies());
    }

    public void testCloneCopiesReturnsNotSameSiteIdentifierList() throws Exception {
        SuiteRoleMembership original = createMembership(SuiteRole.DATA_ANALYST).
            forStudies("B").forSites("H", "Q");
        SuiteRoleMembership actual = original.clone();
        assertNotSame(original.getSiteIdentifiers(), actual.getSiteIdentifiers());
    }

    public void testCloneCopiesReturnsNotSameStudyIdentifierList() throws Exception {
        SuiteRoleMembership original = createMembership(SuiteRole.DATA_ANALYST).
            forStudies("B").forSites("H", "Q");
        SuiteRoleMembership actual = original.clone();
        assertNotSame(original.getStudyIdentifiers(), actual.getStudyIdentifiers());
    }

    public void testCloneCopiesSiteIdentifierList() throws Exception {
        SuiteRoleMembership actual = createMembership(SuiteRole.DATA_ANALYST).
            forStudies("B").forSites("H", "Q").clone();
        assertEquals("Wrong number of identifiers", 2, actual.getSiteIdentifiers().size());
        assertEquals("Wrong 1st site", "H", actual.getSiteIdentifiers().get(0));
        assertEquals("Wrong 2nd site", "Q", actual.getSiteIdentifiers().get(1));
    }

    public void testCloneCopiesStudyIdentifierList() throws Exception {
        SuiteRoleMembership actual = createMembership(SuiteRole.DATA_ANALYST).
            forStudies("B").forSites("H", "Q").clone();
        assertEquals("Wrong number of identifiers", 1, actual.getStudyIdentifiers().size());
        assertEquals("Wrong 1st study", "B", actual.getStudyIdentifiers().get(0));
    }

    public void testCloneIncludesMappings() throws Exception {
        SuiteRoleMembership actual = createMembership(SuiteRole.DATA_ANALYST).clone();
        actual.forSites(new TestSite("T")).forStudies(new TestStudy("Y"));
        // no exception
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public void testCloneWorksWithoutMappings() throws Exception {
        SuiteRoleMembership actual = new SuiteRoleMembership(SuiteRole.DATA_ANALYST, null, null).clone();
        // no exception
    }

    ////// HELPERS

    private SuiteRoleMembership createMembership(SuiteRole role) {
        return new SuiteRoleMembership(role, new TestSiteMapping(), new TestStudyMapping());
    }

    private SuiteRoleMembership createMockMappingMembership(SuiteRole role) {
        return new SuiteRoleMembership(role, mockSiteMapping, mockStudyMapping);
    }
}
