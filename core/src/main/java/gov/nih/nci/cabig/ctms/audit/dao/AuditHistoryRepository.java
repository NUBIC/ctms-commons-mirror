package gov.nih.nci.cabig.ctms.audit.dao;

import gov.nih.nci.cabig.ctms.audit.dao.query.DataAuditEventQuery;
import gov.nih.nci.cabig.ctms.audit.domain.*;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.*;

/**
 * provide methods to retrieve audit history details for an entity
 *
 * @author Saurabh Agarwal
 */
@Repository
@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
public class AuditHistoryRepository {

    private AuditHistoryDao auditHistoryDao;

    /**
     * Checks if entity was created minutes before the specefied date.
     * By default method will check if entity was created one minute before the given data
     *
     * @param entityClass the entity class
     * @param entityId    the primary key of entity
     * @param calendar    the date
     * @param minutes     time before the entity was created
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
        final Calendar newCalendar = (Calendar) calendar.clone();
        newCalendar.add(Calendar.MINUTE, -minutes);
        DataAuditEventQuery dataAuditEventQuery = new DataAuditEventQuery();
        dataAuditEventQuery.filterByClassName(entityClass.getName());
        dataAuditEventQuery.filterByStartDateAfter(newCalendar.getTime());
        dataAuditEventQuery.filterByEndDateBefore(calendar.getTime());
        dataAuditEventQuery.filterByEntityId(entityId);
        dataAuditEventQuery.filterByOperation(Operation.CREATE);
        final List<DataAuditEvent> dataAuditEvents = auditHistoryDao.findDataAuditEvents(dataAuditEventQuery);
        return dataAuditEvents != null && !dataAuditEvents.isEmpty();

    }

    /**
     * Returns list of data audit event object matching query criteria
     *
     * @param query
     */
    public List<DataAuditEvent> findDataAuditEvents(final DataAuditEventQuery query) {
        return auditHistoryDao.findDataAuditEvents(query);
    }


    /**
     * Checks if entity was created by a url.
     *
     * @param entityClass
     * @param entityId
     * @param url
     * @return true if entity was created by the specified url; false otherwise
     * @throws IllegalArgumentException if any of method argument is null
     */
    public boolean checkIfEntityWasCreatedByUrl(final Class entityClass, final Integer entityId, final String url) {

        if (url == null || entityClass == null || entityId == null) {
            throw new IllegalArgumentException("invalid uses of method. All method parameters must not be null");
        }
        DataAuditEventQuery dataAuditEventQuery = new DataAuditEventQuery();
        dataAuditEventQuery.filterByClassName(entityClass.getName());
        dataAuditEventQuery.filterByURL(url);
        dataAuditEventQuery.filterByEntityId(entityId);
        dataAuditEventQuery.filterByOperation(Operation.CREATE);
        final List<DataAuditEvent> dataAuditEvents = auditHistoryDao.findDataAuditEvents(dataAuditEventQuery);
        return dataAuditEvents != null && !dataAuditEvents.isEmpty();

    }

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
            DataAuditEvent newDataAuditEvent = auditHistoryDao.getNearestAuditEvent(dataAuditEvent);
            auditHistoryDetails.addAll(getEventValues(attributeFieldsName, newDataAuditEvent));
        }

        return auditHistoryDetails;
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

        DataAuditEventQuery dataAuditEventQuery = new DataAuditEventQuery();
        dataAuditEventQuery.leftJoinFetch("e.values");

        dataAuditEventQuery.filterByClassName(entityClass.getName());
        dataAuditEventQuery.filterByEntityId(entityId);


        if (calendar != null) {
            final Calendar newCalendar = (Calendar) calendar.clone();
            newCalendar.add(Calendar.DAY_OF_MONTH, +1);
            Date startDate = DateUtils.truncate(calendar.getTime(), Calendar.DATE);
            Date endDate = DateUtils.truncate(newCalendar.getTime(), Calendar.DATE);
            dataAuditEventQuery.filterByStartDateAfter(startDate);
            dataAuditEventQuery.filterByEndDateBefore(endDate);

        }
        final List<DataAuditEvent> dataAuditEvents = auditHistoryDao.findDataAuditEvents(dataAuditEventQuery);

        return dataAuditEvents;

    }

    @SuppressWarnings("unchecked")
    public List<DataAuditEvent> getAuditDetailsForEntity(final Class entityClass, final Integer entityId)

    {
        final List<gov.nih.nci.cabig.ctms.audit.domain.DataAuditEvent> dataAuditEvents = getDataAuditEvents(
                entityClass, entityId, null);
        return dataAuditEvents;

    }


    @Required
    public void setAuditHistoryDao(final AuditHistoryDao auditHistoryDao) {
        this.auditHistoryDao = auditHistoryDao;
    }
}
