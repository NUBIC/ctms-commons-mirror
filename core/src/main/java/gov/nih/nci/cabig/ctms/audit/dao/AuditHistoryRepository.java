package gov.nih.nci.cabig.ctms.audit.dao;

import gov.nih.nci.cabig.ctms.audit.domain.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * provide methods to retrieve audit history details for an entity
 *
 * @author Saurabh Agarwal
 */
@Repository
@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
public class AuditHistoryRepository extends HibernateDaoSupport {
    /**
     * Checks if entity was created minutes before the specefied date.
     * By default method will check if entity was created one minute before the given data
     *
     * @param entityClass the entity class
     * @param entityId    the primary key of entity
     * @param calendar    the date
     * @param minutes     minutes . Default value is 1. i.e. method will check if the entity was created one minute before the given date.
     * @return true if entity was created minutes before the specefied date.
     * @throws IllegalArgumentException if all the parameter except minutes is null;
     */
    public boolean checkIfEntityWasCreatedMinutesBeforeSpecificDate(final Class entityClass, final Integer entityId,
                                                                    final Calendar calendar, int minutes)

    {

        if (calendar == null || entityClass == null || entityId == null) {
            throw new IllegalArgumentException("invalid uses of method. All method parameters must not be null");
        }
        if (Integer.valueOf(minutes).equals(Integer.valueOf(0))) {
            minutes = 1;
        }
        final StringBuffer queryBuffer = new StringBuffer(
                "select distinct e from DataAuditEvent e  where "
                        + "e.reference.className = :className " +
                        "and e.reference.id= :id " +
                        "and e.operation=:operation"
                        + "and e.info.time>=:startDate and e.info.time<=:endDate order by e.id asc");

        final Calendar newCalendar = (Calendar) calendar.clone();
        newCalendar.add(Calendar.MINUTE, -minutes);
        final Date startDate = DateUtils.truncate(calendar.getTime(), Calendar.DATE);
        final Date endDate = DateUtils.truncate(newCalendar.getTime(), Calendar.DATE);

        final List<DataAuditEvent> dataAuditEvents = (List<DataAuditEvent>) getHibernateTemplate().execute(
                new HibernateCallback() {

                    public Object doInHibernate(final Session session) throws HibernateException {
                        final Query query = session.createQuery(queryBuffer.toString());
                        query.setParameter("className", entityClass.getName());
                        query.setParameter("id", entityId);
                        query.setParameter("endDate", endDate);
                        query.setParameter("startDate", startDate);
                        query.setParameter("operation", Operation.CREATE);
                        return query.list();
                    }
                });


        return dataAuditEvents != null && !dataAuditEvents.isEmpty();

    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<AuditHistory> getAuditDetailsForEntity(final Class entityClass, final Integer entityId,
                                                       final Calendar calendar)

    {
        final List<gov.nih.nci.cabig.ctms.audit.domain.DataAuditEvent> dataAuditEvents = getDataAuditEvents(
                entityClass, entityId, calendar);

        final List<AuditHistory> auditHistories = new ArrayList<AuditHistory>();

        Field[] fields = entityClass.getDeclaredFields();
        List<String> attributeFieldsName = new ArrayList<String>();

        for (Field field : fields) {
            if (field.getType().isPrimitive() || field.getType().isInstance("")
                    || field.getType().isInstance(new Date())) {
                attributeFieldsName.add(field.getName());

            }
        }
        for (DataAuditEvent auditEvent : dataAuditEvents) {

            AuditHistory auditHistory = createAuditHistory(auditEvent);
            List<String> attributeFields = new ArrayList<String>();
            attributeFields.addAll(attributeFieldsName);
            List<AuditHistoryDetail> auditHistoryDetails = getEventValues(attributeFields, auditEvent);
            auditHistory.addValues(auditHistoryDetails);
            auditHistories.add(auditHistory);

        }
        return auditHistories;

    }

    /**
     * Copies values from DataAuditEvent object and creates an audit history object.
     *
     * @param auditEvent the audit event
     * @return the audit history
     */
    private AuditHistory createAuditHistory(final DataAuditEvent auditEvent) {
        AuditHistory auditHistory = new AuditHistory(auditEvent.getReference().getClassName(), auditEvent
                .getReference().getId(), auditEvent.getOperation(), auditEvent.getId());
        auditHistory.setIp(auditEvent.getInfo().getIp());
        auditHistory.setUsername(auditEvent.getInfo().getUsername());
        auditHistory.setTime(auditEvent.getInfo().getTime());

        return auditHistory;
    }

    /**
     * Returns the event values.
     *
     * @param attributeFieldsName the attribute fields name
     * @param dataAuditEvent      the data audit event
     * @return the event values
     */
    private List<AuditHistoryDetail> getEventValues(final List<String> attributeFieldsName,
                                                    final DataAuditEvent dataAuditEvent) {
        if (attributeFieldsName.isEmpty() || dataAuditEvent == null) {
            return new ArrayList<AuditHistoryDetail>();
        }
        List<AuditHistoryDetail> auditHistoryDetails = new ArrayList<AuditHistoryDetail>();

        for (final DataAuditEventValue eventValue : dataAuditEvent.getValues()) {

            if (attributeFieldsName.contains(eventValue.getAttributeName())) {
                AuditHistoryDetail auditHistoryDetail = new AuditHistoryDetail(eventValue.getAttributeName(),
                        eventValue.getPreviousValue(), eventValue.getCurrentValue());
                auditHistoryDetails.add(auditHistoryDetail);
                attributeFieldsName.remove(eventValue.getAttributeName());
            }

        }
        if (!attributeFieldsName.isEmpty()) {
            // first retrieve the nearest data audit event
            DataAuditEvent newDataAuditEvent = getNearestAuditEvent(dataAuditEvent);
            auditHistoryDetails.addAll(getEventValues(attributeFieldsName, newDataAuditEvent));
        }

        return auditHistoryDetails;
    }

    /**
     * Returns the nearest audit event.
     *
     * @param dataAuditEvent the data audit event
     * @return the nearest audit event
     */
    @SuppressWarnings("unchecked")
    private DataAuditEvent getNearestAuditEvent(final DataAuditEvent dataAuditEvent) {

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

    /**
     * Gets the list of data audit events for a domain object.
     *
     * @param entityClass the entity class
     * @param entityId    the entity id
     * @param calendar    the calendar
     * @return the list of data audit events
     */
    @SuppressWarnings("unchecked")
    private List<DataAuditEvent> getDataAuditEvents(final Class entityClass, final Integer entityId,
                                                    final Calendar calendar) {

        final StringBuffer queryBuffer = new StringBuffer(
                "select distinct e from DataAuditEvent e left join fetch e.values where "
                        + "e.reference.className = :className and e.reference.id= :id "
                        + "and e.info.time>=:startDate and e.info.time<=:endDate order by e.id asc");

        final Calendar newCalendar = (Calendar) calendar.clone();
        newCalendar.add(Calendar.DAY_OF_MONTH, +1);
        final Date startDate = DateUtils.truncate(calendar.getTime(), Calendar.DATE);
        final Date endDate = DateUtils.truncate(newCalendar.getTime(), Calendar.DATE);

        final List<DataAuditEvent> dataAuditEvents = (List<DataAuditEvent>) getHibernateTemplate().execute(
                new HibernateCallback() {

                    public Object doInHibernate(final Session session) throws HibernateException {
                        final Query query = session.createQuery(queryBuffer.toString());
                        query.setParameter("className", entityClass.getName());
                        query.setParameter("id", entityId);
                        query.setParameter("endDate", endDate);
                        query.setParameter("startDate", startDate);
                        return query.list();
                    }
                });

        return dataAuditEvents;

    }
}
