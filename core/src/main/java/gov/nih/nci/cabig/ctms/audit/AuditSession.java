package gov.nih.nci.cabig.ctms.audit;

import gov.nih.nci.cabig.ctms.audit.dao.DataAuditRepository;
import gov.nih.nci.cabig.ctms.audit.domain.DataAuditEvent;
import gov.nih.nci.cabig.ctms.audit.domain.DataReference;
import gov.nih.nci.cabig.ctms.audit.domain.Operation;
import gov.nih.nci.cabig.ctms.audit.exception.AuditSystemException;
import gov.nih.nci.cabig.ctms.audit.util.AuditUtil;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.Map;

public class AuditSession {

    /** The events map. */
    private final Map<Object, DataAuditEvent> events = new IdentityHashMap<Object, DataAuditEvent>();

    /** The data audit dao. */
    private final DataAuditRepository dataAuditRepository;

    /**
     * Instantiates a new audit session.
     *
     * @param dataAuditRepository the data audit dao
     */
    public AuditSession(final DataAuditRepository dataAuditRepository) {
        this.dataAuditRepository = dataAuditRepository;

    }

    /**
     * Adds the event.
     *
     * @param entity the entity
     * @param event the event
     * @param operation
     */
    public void addEvent(final Object entity, final DataAuditEvent event, final Operation operation) {

        if (events.containsKey(entity)) {
            if (!operation.equals(Operation.UPDATE)) {
                // ignore the cyclic dependency if any
                DataAuditEvent existingEvent = events.get(entity);
                throw new AuditSystemException("There is already an event (" + existingEvent.getOperation() + ") for "
                    + entity + ".  Cannot register a new one (" + event.getOperation() + ')');
            }
        }

        else {

            events.put(entity, event);
        }
    }

    /**
     * Close.
     */
    public void close() {
        Collection<Object> keysCopy = new LinkedList<Object>(events.keySet());
        for (Object entity : keysCopy) {
            saveEvent(entity);
        }

        if (events.size() > 0) {
            throw new AuditSystemException("There are " + events.size()
                + " audit event(s) outstanding at the end of the hibernate session");
        }
    }

    /**
     * Checks if operation on an entity is deleted or not.
     *
     * @param entity the entity
     *
     * @return true, if operation is deleted; false otherwise
     */
    public boolean deleted(final Object entity) {
        DataAuditEvent event = events.get(entity);
        return event != null && event.getOperation() == Operation.DELETE;
    }

    /**
     * Save event.
     *
     * @param entity the entity
     */
    public void saveEvent(final Object entity) {
        if (!events.containsKey(entity)) {
            return;
        }

        if (AuditUtil.getObjectId(entity) == null) {
            events.remove(entity);
            throw new AuditSystemException("No ID for entity " + entity + "; cannot properly audit");
        }

        DataAuditEvent event = events.get(entity);
        if (event.getReference().getId() == null) {
            event.setReference(DataReference.create(entity));
        }
        events.remove(entity);
        dataAuditRepository.save(event);
    }

}
