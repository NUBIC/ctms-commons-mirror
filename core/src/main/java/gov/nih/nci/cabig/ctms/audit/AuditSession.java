package gov.nih.nci.cabig.ctms.audit;

import gov.nih.nci.cabig.ctms.audit.dao.DataAuditRepository;
import gov.nih.nci.cabig.ctms.audit.domain.DataAuditEvent;
import gov.nih.nci.cabig.ctms.audit.domain.DataReference;
import gov.nih.nci.cabig.ctms.audit.domain.Operation;
import gov.nih.nci.cabig.ctms.audit.exception.AuditSystemException;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * The Class AuditSession.
 * 
 * @author Rhett Sutphin
 */
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
	public AuditSession(DataAuditRepository dataAuditRepository) {
		this.dataAuditRepository = dataAuditRepository;

	}

	/**
	 * Adds the event.
	 * 
	 * @param entity the entity
	 * @param event the event
	 */
	public void addEvent(Object entity, DataAuditEvent event) {
		if (events.containsKey(entity)) {
			DataAuditEvent existingEvent = events.get(entity);
			throw new AuditSystemException("There is already an event (" + existingEvent.getOperation() + ") for "
					+ entity + ".  Cannot register a new one (" + event.getOperation() + ')');
		}
		events.put(entity, event);
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
	public boolean deleted(Object entity) {
		DataAuditEvent event = events.get(entity);
		return event != null && event.getOperation() == Operation.DELETE;
	}

	/**
	 * Gets the id of the persisted object
	 * 
	 * @param obj the object to get the id from
	 * @return object Id
	 */

	private Integer getObjectId(final Object obj) {

		Class objectClass = obj.getClass();
		Method[] methods = objectClass.getMethods();

		// FIXME:Saurabh make sure it works for the Long also
		Integer persistedObjectId = null;
		for (Method element : methods) {
			// If the method name equals 'getId' then invoke it to get the id of
			// the object.
			if (element.getName().equals("getId")) {
				try {
					persistedObjectId = (Integer) element.invoke(obj, null);
					break;
				}
				catch (Exception e) {
					// logger.warn("Audit Log Failed - Could not get persisted
					// object id: " + e.getMessage());
				}
			}
		}
		return persistedObjectId;
	}

	/**
	 * Save event.
	 * 
	 * @param entity the entity
	 */
	public void saveEvent(Object entity) {
		if (!events.containsKey(entity)) {
			return;
		}

		if (getObjectId(entity) == null) {
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
