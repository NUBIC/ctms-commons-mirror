package gov.nih.nci.cabig.ctms.domain;

import gov.nih.nci.cabig.ctms.testing.CommonsTestCase;

/**
 * @author Rhett Sutphin
 */
public class EnumHelperTest extends CommonsTestCase {
    public void testSentenceCasedName() throws Exception {
        assertEquals("Grizzly", EnumHelper.sentenceCasedName(SampleCodedEnum.GRIZZLY));
        assertEquals("Human skull", EnumHelper.sentenceCasedName(SampleCodedEnum.HUMAN_SKULL));
    }
}
