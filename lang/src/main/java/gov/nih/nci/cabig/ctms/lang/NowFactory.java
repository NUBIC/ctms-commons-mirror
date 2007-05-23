package gov.nih.nci.cabig.ctms.lang;

import java.util.Date;

/**
 * Provides an injectable interface for determining "now."  This makes it easier to write
 * consistent tests for code that needs to set some value to the current date.
 *
 * @author Rhett Sutphin
 */
public class NowFactory {
    public Date getNow() {
        return new Date();
    }
}
