package gov.nih.nci.cabig.ctms.suite.authorization;

import junit.framework.TestCase;

/**
 * @author Rhett Sutphin
 */
public class ScopeDescriptionTest extends TestCase {
    public void testCreatedAllScopeIsAll() throws Exception {
        assertTrue("Should be all scope",
            ScopeDescription.createForAll(ScopeType.SITE).isAll());
    }

    public void testCreatedSingleScopeIsNotAll() throws Exception {
        assertFalse("Should not be all scope",
            ScopeDescription.createForOne(ScopeType.SITE, "B").isAll());
    }

    public void testCreatedSingleScopeReturnsTheCorrectIdentifier() throws Exception {
        assertEquals("Wrong ident", "T",
            ScopeDescription.createForOne(ScopeType.STUDY, "T").getIdentifier());
    }

    public void testCreateForSingleDoesNotAllowNullIdentifier() throws Exception {
        try {
            ScopeDescription.createForOne(ScopeType.STUDY, null);
            fail("Exception not thrown");
        } catch (IllegalArgumentException iae) {
            assertEquals("An identifier is required", iae.getMessage());
        }
    }

    public void testCreateScopeFromSingleSiteProtectionElementName() throws Exception {
        ScopeDescription created = ScopeDescription.createFromCsmName("HealthcareSite.NCI014");
        assertEquals("Wrong ident", "NCI014", created.getIdentifier());
        assertEquals("Wrong scope", ScopeType.SITE, created.getScope());
    }

    public void testCreateScopeFromAllSiteProtectionElementName() throws Exception {
        ScopeDescription created = ScopeDescription.createFromCsmName("HealthcareSite");
        assertEquals("Wrong scope", ScopeType.SITE, created.getScope());
        assertTrue("Should be for all", created.isAll());
    }

    public void testCreateScopeFromSingleStudyProtectionElement() throws Exception {
        ScopeDescription created = ScopeDescription.createFromCsmName("Study.T");
        assertEquals("Wrong ident", "T", created.getIdentifier());
        assertEquals("Wrong scope", ScopeType.STUDY, created.getScope());
    }

    public void testCreateScopeFromSingleStudyProtectionElementNameThatContainsDots() throws Exception {
        ScopeDescription created = ScopeDescription.createFromCsmName("Study.T.U.V");
        assertEquals("Wrong ident", "T.U.V", created.getIdentifier());
        assertEquals("Wrong scope", ScopeType.STUDY, created.getScope());
    }

    public void testCreateScopeFromAllStudyProtectionElement() throws Exception {
        ScopeDescription created = ScopeDescription.createFromCsmName("Study");
        assertEquals("Wrong scope", ScopeType.STUDY, created.getScope());
        assertTrue("Should be for all", created.isAll());
    }

    public void testCreateScopeForUnparsableCsmNameReturnsNull() throws Exception {
        assertNull(ScopeDescription.createFromCsmName("Foo"));
    }

    public void testGetCsmNameForAllSite() throws Exception {
        assertEquals("HealthcareSite", ScopeDescription.createForAll(ScopeType.SITE).getCsmName());
    }

    public void testGetCsmNameForAllStudy() throws Exception {
        assertEquals("Study", ScopeDescription.createForAll(ScopeType.STUDY).getCsmName());
    }

    public void testGetCsmNameForOneSite() throws Exception {
        assertEquals("HealthcareSite.B",
            ScopeDescription.createForOne(ScopeType.SITE, "B").getCsmName());
    }

    public void testGetCsmNameForOneStudy() throws Exception {
        assertEquals("Study.B", ScopeDescription.createForOne(ScopeType.STUDY, "B").getCsmName());
    }

    public void testGetCsmNameForOneStudyWithDots() throws Exception {
        assertEquals("Study.B.C.D",
            ScopeDescription.createForOne(ScopeType.STUDY, "B.C.D").getCsmName());
    }

    public void testAccessingIdentForAllScopeFails() throws Exception {
        try {
            ScopeDescription.createForAll(ScopeType.STUDY).getIdentifier();
            fail("Exception not thrown");
        } catch (SuiteAuthorizationAccessException e) {
            assertEquals(
                "This description indicates access to every study.  You can't get an identifier from it.",
                e.getMessage());
        }
    }

    public void testEqualsForSameScopeAndOneEquivalentInstance() throws Exception {
        assertEquals(
            ScopeDescription.createForOne(ScopeType.SITE, "A"),
            ScopeDescription.createForOne(ScopeType.SITE, "A")
        );
    }

    public void testEqualsForAllInSameScope() throws Exception {
        assertEquals(
            ScopeDescription.createForAll(ScopeType.SITE),
            ScopeDescription.createForAll(ScopeType.SITE)
        );
    }

    public void testNotEqualsForAllWithDifferentScopes() throws Exception {
        assertFalse(
            ScopeDescription.createForAll(ScopeType.SITE).equals(
                ScopeDescription.createForAll(ScopeType.STUDY))
        );
    }

    public void testNotEqualsForOneWithDifferentScopes() throws Exception {
        assertFalse(
            ScopeDescription.createForOne(ScopeType.SITE,  "A").equals(
                ScopeDescription.createForOne(ScopeType.STUDY, "A"))
        );
    }

    public void testNotEqualsForOneWithDifferentIdents() throws Exception {
        assertFalse(
            ScopeDescription.createForOne(ScopeType.STUDY, "A").equals(
                ScopeDescription.createForOne(ScopeType.STUDY, "B"))
        );
    }
}
