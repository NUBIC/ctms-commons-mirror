package gov.nih.nci.cabig.ctms.suite.authorization;

import gov.nih.nci.cabig.ctms.suite.authorization.domain.TestSite;
import gov.nih.nci.cabig.ctms.suite.authorization.domain.TestSiteMapping;
import gov.nih.nci.cabig.ctms.suite.authorization.domain.TestStudy;
import gov.nih.nci.cabig.ctms.suite.authorization.domain.TestStudyMapping;
import static gov.nih.nci.cabig.ctms.suite.authorization.CsmIntegratedTestHelper.getAuthorizationDao;
import gov.nih.nci.security.authorization.domainobjects.Group;
import gov.nih.nci.security.authorization.domainobjects.ProtectionElement;
import gov.nih.nci.security.authorization.domainobjects.ProtectionGroup;
import gov.nih.nci.security.authorization.domainobjects.Role;
import gov.nih.nci.security.exceptions.CSObjectNotFoundException;

import java.util.Set;

/**
 * @author Rhett Sutphin
 */
public class CsmHelperIntegratedTest extends IntegratedTestCase {
    private CsmHelper csmHelper;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        csmHelper = new CsmHelper();
        csmHelper.setAuthorizationManager(getAuthorizationManager());
        csmHelper.setSiteMapping(new TestSiteMapping());
        csmHelper.setStudyMapping(new TestStudyMapping());
    }

    public void testGetExistingProtectionElementForScopeDescription() throws Exception {
        ProtectionElement actual = csmHelper.getOrCreateScopeProtectionElement(
            ScopeDescription.createForOne(ScopeType.STUDY, "CRM114"));
        assertNotNull("This method should never return null", actual);
        assertEquals("Returned the wrong PE", "Study.CRM114", actual.getProtectionElementName());
        assertEquals("Did not return the existing instance", -1L, (long) actual.getProtectionElementId());
    }

    public void testGetExistingProtectionElementForScopeAndObject() throws Exception {
        ProtectionElement actual = csmHelper.getOrCreateScopeProtectionElement(
            ScopeType.STUDY, new TestStudy("CRM114"));
        assertNotNull("This method should never return null", actual);
        assertEquals("Returned the wrong PE", "Study.CRM114", actual.getProtectionElementName());
        assertEquals("Did not return the existing instance", -1L, (long) actual.getProtectionElementId());
    }

    public void testGetNewProtectionElement() throws Exception {
        ProtectionElement actual = csmHelper.getOrCreateScopeProtectionElement(
            ScopeDescription.createForOne(ScopeType.SITE, "60"));
        assertNotNull("This method should never return null", actual);
        assertEquals("Returned the wrong PE", "HealthcareSite.60", actual.getProtectionElementName());
        assertNotNull("Did not return an instance with an ID", actual.getProtectionElementId());
    }

    public void testGetNewProtectionElementCreatesTheProtectionGroup() throws Exception {
        ProtectionElement actual = csmHelper.getOrCreateScopeProtectionElement(
            ScopeDescription.createForOne(ScopeType.SITE, "IL034"));
        assertNotNull("This method should never return null", actual);

        try {
            ProtectionGroup parallel = getAuthorizationDao().getProtectionGroup("HealthcareSite.IL034");
            assertNotNull("Protection group not created", parallel);
        } catch (CSObjectNotFoundException e) {
            fail("Protection group not created");
        }
    }

    public void testGetNewProtectionElementCreatesTheLinkBetweenTheNewPEAndTheNewPG() throws Exception {
        ProtectionElement actual = csmHelper.getOrCreateScopeProtectionElement(
            ScopeDescription.createForOne(ScopeType.SITE, "IL034"));
        assertNotNull("This method should never return null", actual);

        Set actualPgs = getAuthorizationDao().getProtectionGroups(actual.getProtectionElementId().toString());
        assertEquals("Wrong number of associated PGs: " + actualPgs, 1, actualPgs.size());
        assertEquals("Wrong associated PG", "HealthcareSite.IL034",
            ((ProtectionGroup) actualPgs.iterator().next()).getProtectionGroupName());
    }

    public void testGetExistingProtectionElementAddsAMissingProtectionGroup() throws Exception {
        try {
            ProtectionGroup pg = getAuthorizationDao().getProtectionGroup("HealthcareSite.THX11");
            fail("Test setup failure.  PG already exists: " + pg);
        } catch (CSObjectNotFoundException e) {
            // expected
        }

        csmHelper.getOrCreateScopeProtectionElement(
            ScopeDescription.createForOne(ScopeType.SITE, "THX11"));

        try {
            ProtectionGroup pg = getAuthorizationDao().getProtectionGroup("HealthcareSite.THX11");
            assertNotNull("PG not created", pg);
        } catch (CSObjectNotFoundException e) {
            fail("PG not created: " + e.getMessage());
        }
    }

    public void testGetExistingProtectionGroupForScopeDescription() throws Exception {
        ProtectionGroup actual = csmHelper.getOrCreateScopeProtectionGroup(
            ScopeDescription.createForOne(ScopeType.STUDY, "CRM114"));
        assertNotNull("This method should never return null", actual);
        assertEquals("Returned the wrong PG", "Study.CRM114", actual.getProtectionGroupName());
        assertEquals("Did not return the existing instance", -1L, (long) actual.getProtectionGroupId());
    }

    public void testGetExistingProtectionGroupForScopeAndObject() throws Exception {
        ProtectionGroup actual = csmHelper.getOrCreateScopeProtectionGroup(
            ScopeType.STUDY, new TestStudy("CRM114"));
        assertNotNull("This method should never return null", actual);
        assertEquals("Returned the wrong PG", "Study.CRM114", actual.getProtectionGroupName());
        assertEquals("Did not return the existing instance", -1L, (long) actual.getProtectionGroupId());
    }

    public void testGetNewProtectionGroup() throws Exception {
        ProtectionElement actual = csmHelper.getOrCreateScopeProtectionElement(
            ScopeDescription.createForOne(ScopeType.STUDY, "E45"));
        assertNotNull("This method should never return null", actual);
        assertEquals("Returned the wrong PG", "Study.E45", actual.getProtectionElementName());
        assertNotNull("Did not return an instance with an ID", actual.getProtectionElementId());
    }

    public void testGetNewProtectionGroupCreatesTheProtectionElement() throws Exception {
        ProtectionGroup actual = csmHelper.getOrCreateScopeProtectionGroup(
            ScopeDescription.createForOne(ScopeType.SITE, "IL034"));
        assertNotNull("This method should never return null", actual);

        try {
            ProtectionElement parallel = getAuthorizationManager().getProtectionElement("HealthcareSite.IL034");
            assertNotNull("Protection element not created", parallel);
        } catch (CSObjectNotFoundException e) {
            fail("Protection element not created");
        }
    }

    public void testGetNewProtectionGroupCreatesTheLinkBetweenTheNewPEAndTheNewPG() throws Exception {
        ProtectionGroup actual = csmHelper.getOrCreateScopeProtectionGroup(
            ScopeDescription.createForOne(ScopeType.SITE, "IL034"));
        assertNotNull("This method should never return null", actual);

        Set actualPes = getAuthorizationDao().getProtectionElements(actual.getProtectionGroupId().toString());
        assertEquals("Wrong number of associated PEs: " + actualPes, 1, actualPes.size());
        assertEquals("Wrong associated PE", "HealthcareSite.IL034",
            ((ProtectionElement) actualPes.iterator().next()).getProtectionElementName());
    }

    public void testGetExistingProtectionGroupAddsAMissingProtectionElement() throws Exception {
        try {
            ProtectionElement pe = getAuthorizationDao().getProtectionElement("HealthcareSite.PR39");
            fail("Test setup failure.  PE already exists: " + pe);
        } catch (CSObjectNotFoundException e) {
            // expected
        }

        csmHelper.getOrCreateScopeProtectionElement(
            ScopeDescription.createForOne(ScopeType.SITE, "PR39"));

        try {
            ProtectionElement pe = getAuthorizationDao().getProtectionElement("HealthcareSite.PR39");
            assertNotNull("PE not created", pe);
        } catch (CSObjectNotFoundException e) {
            fail("PE not created: " + e.getMessage());
        }
    }

    public void testGetSuiteRoleCsmGroup() throws Exception {
        Group actual = csmHelper.getRoleCsmGroup(SuiteRole.STUDY_SUBJECT_CALENDAR_MANAGER);
        assertNotNull("This method should never return null", actual);
        assertEquals("Wrong group returned", "study_subject_calendar_manager", actual.getGroupName());
    }

    public void testAllSuiteRolesAreAccessibleAsCsmGroups() throws Exception {
        for (SuiteRole role : SuiteRole.values()) {
            csmHelper.getRoleCsmGroup(role);
        }
        // It's sufficient that there aren't any exceptions
    }

    public void testGetSuiteRoleCsmRole() throws Exception {
        Role actual = csmHelper.getRoleCsmRole(SuiteRole.STUDY_SUBJECT_CALENDAR_MANAGER);
        assertNotNull("This method should never return null", actual);
        assertEquals("Wrong object returned", "study_subject_calendar_manager", actual.getName());
    }

    public void testAllSuiteRolesAreAccessibleAsCsmRoles() throws Exception {
        for (SuiteRole role : SuiteRole.values()) {
            csmHelper.getRoleCsmRole(role);
        }
        // It's sufficient that there aren't any exceptions
    }

    public void testAttemptingToUseAnUnavailableMappingGivesAHelpfulErrorMessage() throws Exception {
        csmHelper.setStudyMapping(null);
        try {
            csmHelper.getOrCreateScopeProtectionGroup(ScopeType.STUDY, new TestStudy("Y"));
            fail("Exception not thrown");
        } catch (SuiteAuthorizationProvisioningFailure e) {
            assertEquals("Wrong message",
                "No study mapping was provided.  Either provide one or stick to the identifier-based methods.",
                e.getMessage());
        }
    }
}
