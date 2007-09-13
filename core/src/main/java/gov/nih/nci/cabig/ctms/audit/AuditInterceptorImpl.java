package gov.nih.nci.cabig.ctms.audit;

import gov.nih.nci.cabig.ctms.audit.dao.DataAuditRepository;
import gov.nih.nci.cabig.ctms.audit.domain.DataAuditEvent;
import gov.nih.nci.cabig.ctms.audit.domain.DataAuditEventValue;
import gov.nih.nci.cabig.ctms.audit.domain.DataAuditInfo;
import gov.nih.nci.cabig.ctms.audit.domain.Operation;
import gov.nih.nci.cabig.ctms.audit.exception.AuditSystemException;
import gov.nih.nci.cabig.ctms.audit.util.AuditUtil;
import gov.nih.nci.cabig.ctms.lang.ComparisonTools;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.EmptyInterceptor;
import org.hibernate.EntityMode;
import org.hibernate.type.AbstractComponentType;
import org.hibernate.type.CollectionType;
import org.hibernate.type.EntityType;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Required;

/**
 * @author Rhett Sutphin
 * @author Saurabh Agrawal
 */
public class AuditInterceptorImpl extends EmptyInterceptor {

	/** The Constant log. */
	private static final Log log = LogFactory.getLog(AuditInterceptorImpl.class);

	private static final ThreadLocal<AuditSession> sessions = new ThreadLocal<AuditSession>();

	/** The Constant ENTITY_MODE. */
	private static final EntityMode ENTITY_MODE = EntityMode.POJO;

	/** The data audit dao. */
	private DataAuditRepository dataAuditRepository;

	private List<String> auditableEntities = new ArrayList<String>();

	private final String HIBERNATE_BACK_REF_STRING = "Backref";

	private boolean auditAll = false;

	private void appendEventValues(final DataAuditEvent parent, final Type propertyType, final String propertyName,
			final Object previousState, final Object currentState) {
		List<DataAuditEventValue> values = Collections.emptyList();
		if (ignoreCurrentStateForThisType(propertyType) || propertyName.indexOf(HIBERNATE_BACK_REF_STRING) > 0) {
			// none
		}
		else if (propertyType.isComponentType()) {
			values = decomposeComponent((AbstractComponentType) propertyType, propertyName, previousState, currentState);
		}
		else {
			String prevValue = scalarAuditableValue(previousState);
			String curValue = scalarAuditableValue(currentState);
			if (prevValue != null && curValue != null && prevValue.equals(curValue)) {
				// do not log it..it might be because of lazy ManyToOne or
				// OneToOne
				// fetching..
			}
			else if (prevValue == null && curValue == null) {
				// do not log it..
			}
			else {
				values = Arrays.asList(new DataAuditEventValue(propertyName, prevValue, curValue));

			}
		}
		for (DataAuditEventValue value : values) {
			parent.addValue(value);
		}
	}

	private boolean auditable(final Object entity) {
		if (auditAll) {
			return true;
		}
		else if (auditableEntities.contains(entity.getClass().getName())) {
			return true;
		}
		// auditable &= entity instanceof Object;
		// auditable &= entity.getClass().getName().indexOf("auditing") < 0;
		// if (auditable) {
		// return true;
		// }
		//	
		else {
			log.debug("No auditing for instances of " + entity.getClass().getName());
			return false;
		}
	}

	private void closeAuditSession() {
		getAuditSession().close();
		sessions.set(null);
	}

	// TODO: this only handles one level of components
	private List<DataAuditEventValue> decomposeComponent(final AbstractComponentType propertyType,
			final String propertyName, final Object previousState, final Object currentState) {
		Object[] componentPrevState = previousState == null ? null : propertyType.getPropertyValues(previousState,
				ENTITY_MODE);
		Object[] componentCurState = currentState == null ? null : propertyType.getPropertyValues(currentState,
				ENTITY_MODE);
		List<Integer> differences = findDifferences(componentCurState, componentPrevState, null);
		List<DataAuditEventValue> values = new ArrayList<DataAuditEventValue>(differences.size());
		for (Integer index : differences) {
			String compPropertyName = propertyName + '.' + propertyType.getPropertyNames()[index];
			values.add(new DataAuditEventValue(compPropertyName, previousState == null ? null
					: scalarAuditableValue(componentPrevState[index]), currentState == null ? null
					: scalarAuditableValue(componentCurState[index])));
		}
		return values;
	}

	// package-level for testing
	List<Integer> findDifferences(final Object[] currentState, final Object[] previousState, final Type[] types) {

		List<Integer> differences = new ArrayList<Integer>();
		if (currentState == null || previousState == null) {
			int len = currentState == null ? previousState.length : currentState.length;
			while (differences.size() < len) {
				differences.add(differences.size());
			}
		}
		else {
			for (int i = 0; i < currentState.length; i++) {
				if (types != null && ignoreCurrentStateForThisType(types[i])) {
					// do nothing
				}
				else if (!ComparisonTools.nullSafeEquals(currentState[i], previousState[i])) {
					differences.add(i);
				}
			}
		}
		return differences;
	}

	private AuditSession getAuditSession() {
		if (sessions.get() == null) {
			sessions.set(new AuditSession(dataAuditRepository));
		}
		return sessions.get();
	}

	private String getEntityTypeName(final Object entity) {
		return entity.getClass().getSimpleName();
	}

	private boolean ignoreCurrentStateForThisType(final Type type) {

		boolean ignoreCuurentSateForThisType = false;

		if (type instanceof EntityType) {
			ignoreCuurentSateForThisType = false;
		}
		else if (type instanceof CollectionType) {
			ignoreCuurentSateForThisType = true;
		}
		return ignoreCuurentSateForThisType;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onDelete(final Object entity, final Serializable id, final Object[] state,
			final String[] propertyNames, final Type[] types) {
		if (!auditable(entity)) {
			return;
		}

		DataAuditEvent delete = registerEvent(entity, Operation.DELETE);
		for (int i = 0; i < state.length; i++) {
			appendEventValues(delete, types[i], propertyNames[i], state[i], null);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onFlushDirty(final Object entity, final Serializable id, final Object[] currentState,
			final Object[] previousState, final String[] propertyNames, final Type[] types) {
		if (!auditable(entity)) {
			return false;
		}
		Object dEntity = entity;
		if (getAuditSession().deleted(dEntity)) {
			return false;
		}
		if (previousState == null) {// this may happen when you have changed the
			// parent of a child.
			return false;
		}
		List<Integer> differences = findDifferences(currentState, previousState, types);
		if (differences.size() == 0) {
			return false;
		}

		DataAuditEvent event = registerEvent(dEntity, Operation.UPDATE);

		for (int index : differences) {
			appendEventValues(event, types[index], propertyNames[index], previousState[index], currentState[index]);
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onSave(final Object entity, final Serializable id, final Object[] state,
			final String[] propertyNames, final Type[] types) {
		if (!auditable(entity)) {
			return false;
		}

		DataAuditEvent event = registerEvent(entity, Operation.CREATE);

		for (int i = 0; i < state.length; i++) {
			appendEventValues(event, types[i], propertyNames[i], null, state[i]);
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void postFlush(final Iterator entities) {
		while (entities.hasNext()) {
			Object entity = entities.next();
			if (!auditable(entity)) {
				continue;
			}
			getAuditSession().saveEvent(entity);
		}
		closeAuditSession();
	}

	private DataAuditEvent registerEvent(final Object entity, final Operation operation) {

		DataAuditInfo info = (DataAuditInfo) gov.nih.nci.cabig.ctms.audit.DataAuditInfo.getLocal();
		if (info == null) {
			throw new AuditSystemException("Cannot audit; no local audit info available");
		}
		DataAuditEvent event = new DataAuditEvent(entity, operation, DataAuditInfo.copy(info));
		getAuditSession().addEvent(entity, event, operation);

		return event;
	}

	String scalarAuditableValue(final Object propertyValue) {
		if (propertyValue == null) {
			return null;
		}
		else if (AuditUtil.getObjectId(propertyValue) != null) {
			Integer id = AuditUtil.getObjectId(propertyValue);
			return id == null ? "transient " + getEntityTypeName(propertyValue) : id.toString();
		}
		else if (propertyValue instanceof Collection) {
			StringBuilder audit = new StringBuilder();
			audit.append('[');
			for (Iterator<?> it = ((Iterable<?>) propertyValue).iterator(); it.hasNext();) {
				Object element = it.next();
				audit.append(scalarAuditableValue(element));
				if (it.hasNext()) {
					audit.append(", ");
				}
			}
			audit.append(']');
			return audit.toString();
		}
		else {
			return propertyValue.toString();
		}
	}

	/**
	 * Sets the auditable entities. This is the list of full qualified class name of entities which require auditing.
	 * @param auditableEntities the auditable entities
	 */
	public void setAuditableEntities(final List<String> auditableEntities) {
		this.auditableEntities = auditableEntities;
	}

	/**
	 * Sets the data audit repository.
	 * @param dataAuditRepository the new data audit repository
	 */
	@Required
	public void setDataAuditRepository(final DataAuditRepository dataAuditRepository) {
		this.dataAuditRepository = dataAuditRepository;
	}

	/**
	 * true if auditing is required for all objects, false otherwise
	 * @param auditAll true if auditing is required for all objects, false otherwise
	 */
	public void setAuditAll(final boolean auditAll) {
		this.auditAll = auditAll;
	}
}
