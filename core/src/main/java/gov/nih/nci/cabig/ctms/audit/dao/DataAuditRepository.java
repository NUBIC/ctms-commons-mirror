package gov.nih.nci.cabig.ctms.audit.dao;

import gov.nih.nci.cabig.ctms.audit.domain.DataAuditEvent;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Repository for persisting {@link DataAuditEvent} objects
 *
 * @author Saurabh Agrawal
 */
@Repository
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class DataAuditRepository extends HibernateDaoSupport {
    public void save(final DataAuditEvent auditEvent) {
        getHibernateTemplate().saveOrUpdate(auditEvent);
    }
}
