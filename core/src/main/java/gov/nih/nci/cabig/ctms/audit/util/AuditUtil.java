package gov.nih.nci.cabig.ctms.audit.util;

import java.lang.reflect.Method;

public class AuditUtil {
	/**
	 * Gets the id of the persisted object
	 * 
	 * @param entity the object to get the id from
	 * @return object Id or null if either id is null or entity does not
	 * implement Integer getId() method.
	 */
	public static Integer getObjectId(final Object entity) {

		Class objectClass = entity.getClass();
		Method[] methods = objectClass.getMethods();

		// FIXME:Saurabh make sure it works for the Long also
		Integer persistedObjectId = null;
		for (Method element : methods) {
			// If the method name equals 'getId' then invoke it to get the id of
			// the object.
			if (element.getName().equals("getId")) {
				try {
					persistedObjectId = (Integer) element.invoke(entity, null);
					break;
				}
				catch (Exception e) {
					return null;
				}
			}
		}
		return persistedObjectId;
	}

}
