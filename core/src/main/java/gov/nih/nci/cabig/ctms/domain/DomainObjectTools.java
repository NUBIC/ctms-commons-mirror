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
}
