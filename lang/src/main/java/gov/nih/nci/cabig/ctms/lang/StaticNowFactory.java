package gov.nih.nci.cabig.ctms.lang;

import java.util.Date;

/**
 * Test implementation of {@link NowFactory}.
 *
 * TODO: move to the testing module when it exists.
 *
 * @author Rhett Sutphin
 */
public class StaticNowFactory extends NowFactory {
    private Date now;

    @Override
    public Date getNow() {
        return now;
    }

    public void setNow(Date now) {
        this.now = now;
    }
}
