package gov.nih.nci.cabig.ccts.util;

import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Ion C. Olaru
 *
 */
public class BooleanDelimiterTest extends TestCase {

    public void testDelimiter() {
        String[] s = BooleanDelimiter.parseBoolean("study_creator");
        assertEquals(1, s.length);
        assertEquals("study_creator", s[0]);

        s = BooleanDelimiter.parseBoolean("study_creator && study_qa_manager");
        assertEquals(2, s.length);
        assertEquals("study_creator", s[0]);
        assertEquals("study_qa_manager", s[1]);

        s = BooleanDelimiter.parseBoolean("study_creator || study_qa_manager & study_calendar_template_builder");
        assertEquals(3, s.length);
        assertEquals("study_creator", s[0]);
        assertEquals("study_qa_manager", s[1]);
        assertEquals("study_calendar_template_builder", s[2]);

        s = BooleanDelimiter.parseBoolean("study_creator=study_qa_manager");
        assertEquals(1, s.length);
        assertEquals("study_creator=study_qa_manager", s[0]);

    }
}