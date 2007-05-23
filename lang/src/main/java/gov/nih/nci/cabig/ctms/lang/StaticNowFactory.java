package gov.nih.nci.cabig.ctms.lang;

import java.util.Date;
import java.sql.Timestamp;

/**
 * Test implementation of {@link NowFactory}.
 *
 * TODO: move to the testing module when it exists.
 *
 * @author Rhett Sutphin
 */
public class StaticNowFactory extends NowFactory {
    private Timestamp now;

    @Override
    public Date getNow() {
        // this is not technically necessary (Timestamp extends Date), but
        // the Timestamp docs say you shouldn't consider them type-related.
        return new Date(now.getTime());
    }

    @Override
    public Timestamp getNowTimestamp() {
        return now;
    }

    public void setNowTimestamp(Timestamp now) {
        this.now = now;
    }
}
