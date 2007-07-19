package gov.nih.nci.cabig.ctms.domain;

import java.util.Collection;
import java.util.Set;
import java.util.LinkedHashSet;

/**
 * @author Rhett Sutphin
 */
public class DomainObjectTools {
    public static Collection<Integer> collectIds(Collection<? extends DomainObject> objs) {
        Set<Integer> ids = new LinkedHashSet<Integer>();
        for (DomainObject obj : objs) ids.add(obj.getId());
        return ids;
    }

    public static <T extends DomainObject> boolean equalById(T t1, T t2) {
        if (t1 == t2) return true;
        if (t1 == null) {
            // t2 must be non-null, so
            return false;
        } else if (t2 == null) {
            // ditto
            return false;
        } else {
            return t1.getId() == null
                ? t2.getId() == null
                : t1.getId().equals(t2.getId());
        }
    }
}
