package gov.nih.nci.cabig.ctms.dao;

import gov.nih.nci.cabig.ctms.domain.DomainObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * @author Rhett Sutphin
 */
public abstract class AbstractDomainObjectDao<T extends DomainObject> extends HibernateDaoSupport 
    implements DomainObjectDao<T>
{
    protected final Logger log = LoggerFactory.getLogger(getClass());

    public abstract Class<T> domainClass();

    @SuppressWarnings("unchecked")
    public T getById(int id) {
        return (T) getHibernateTemplate().get(domainClass(), id);
    }
}
