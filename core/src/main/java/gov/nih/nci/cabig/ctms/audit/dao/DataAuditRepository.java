package gov.nih.nci.cabig.ctms.audit.dao;

import gov.nih.nci.cabig.ctms.audit.domain.DataAuditEvent;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Required;
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
public class DataAuditRepository {

	/** The session factory. */
	// Spring team advise to use sessionFactory directly rather than using
	// hibernateTemplate if we are using Hibernate-3.x and Spring 2.x
	// http://blog.interface21.com/main/2007/06/26/so-should-you-still-use-springs-hibernatetemplate-andor-jpatemplate/
	private SessionFactory sessionFactory;

	/**
	 * {@inheritDoc}
	 */
	public void save(final DataAuditEvent auditEvent) {
		try {
			final Session session = sessionFactory.getCurrentSession();
			session.saveOrUpdate(auditEvent);
			session.flush();
		}
		finally {
			sessionFactory.close();
			// although Spring documents states that "When you call
			// SessionFactory.getCurrentSession(), you are actually calling into
			// a Spring proxy instead of Hibernate directly. Spring internally
			// uses the TransactionSynchronizationManager, which keeps track of
			// any thread-bound resources (such as the Hibernate Session).
			// Sessions that are left open, are closed at the same time the
			// transaction commits.
		}
	}

	/**
	 * Sets the session factory.
	 * 
	 * @param sessionFactory the new session factory
	 */
	@Required
	public void setSessionFactory(final SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}
