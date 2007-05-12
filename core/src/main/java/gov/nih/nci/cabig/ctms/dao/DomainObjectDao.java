package gov.nih.nci.cabig.ctms.dao;

import gov.nih.nci.cabig.ctms.domain.DomainObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * @author Rhett Sutphin
 */
public abstract class DomainObjectDao<T extends DomainObject> extends HibernateDaoSupport {
    protected final Log log = LogFactory.getLog(getClass());

    public abstract Class<T> domainClass();

    @SuppressWarnings("unchecked")
    public T getById(int id) {
        return (T) getHibernateTemplate().get(domainClass(), id);
    }
}
