package gov.nih.nci.cabig.ctms.domain;

import gov.nih.nci.cabig.ctms.testing.CommonsTestCase;

/**
 * @author Rhett Sutphin
 */
public class CodedEnumHelperTest extends CommonsTestCase {
    public void testGetByClassAndCode() throws Exception {
        // implicitly testing, here
        assertEquals(SampleCodedEnum.AIRSHIP, SampleCodedEnum.getByCode('A'));
        assertEquals(SampleCodedEnum.COMPASS, SampleCodedEnum.getByCode('C'));
    }
    
    public void testToStringHelper() throws Exception {
        assertEquals("R: Radio", CodedEnumHelper.toStringHelper(SampleCodedEnum.RADIO));
        assertEquals("S: Human skull", CodedEnumHelper.toStringHelper(SampleCodedEnum.HUMAN_SKULL));
    }
}
