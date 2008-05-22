package gov.nih.nci.cabig.ctms.audit.dao;

import gov.nih.nci.cabig.ctms.audit.dao.query.DataAuditEventQuery;
import gov.nih.nci.cabig.ctms.audit.domain.DataAuditEvent;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author Saurabh Agrawal
 */
@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
public class AuditHistoryDao extends HibernateDaoSupport {

    @SuppressWarnings("unchecked")
    public List<DataAuditEvent> findDataAuditEvents(final DataAuditEventQuery query) {
        String queryString = query.getQueryString();
        logger.debug("query: " + queryString);
        return (List<DataAuditEvent>) getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(final Session session) throws HibernateException, SQLException {
                org.hibernate.Query hiberanteQuery = session.createQuery(query.getQueryString());
                Map<String, Object> queryParameterMap = query.getParameterMap();
                for (String key : queryParameterMap.keySet()) {
                    Object value = queryParameterMap.get(key);
                    hiberanteQuery.setParameter(key, value);

                }
                return hiberanteQuery.list();
            }

        });
    }

    /**
     * Returns the nearest audit event.
     *
     * @param dataAuditEvent the data audit event
     * @return the nearest audit event
     */
    @SuppressWarnings("unchecked")
    public DataAuditEvent getNearestAuditEvent(final DataAuditEvent dataAuditEvent) {

        final StringBuffer queryBuffer = new StringBuffer(
                "select distinct au from DataAuditEvent au left join fetch au.values where "
                        + "au.id=(select max(e.id) from DataAuditEvent e  where e.reference.className = :className and e.id<:id) ");

        DataAuditEvent event = (DataAuditEvent) getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(final Session session) throws HibernateException {
                final Query query = session.createQuery(queryBuffer.toString());
                query.setParameter("className", dataAuditEvent.getReference().getClassName());
                query.setParameter("id", dataAuditEvent.getId());
                return query.uniqueResult();
            }
        });
        return event;
    }
}
