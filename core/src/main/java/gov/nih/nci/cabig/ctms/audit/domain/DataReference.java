package gov.nih.nci.cabig.ctms.audit.domain;

import gov.nih.nci.cabig.ctms.audit.util.AuditUtil;

/**
 * @author Rhett Sutphin
 */
public class DataReference {

	public static DataReference create(Object entity) {

		return new DataReference(entity.getClass().getName(), AuditUtil.getObjectId(entity));
	}

	private String className;

	private Integer id;

	public DataReference() {
	}

	public DataReference(Class<?> clazz, Integer id) {
		this(clazz.getName(), id);
	}

	public DataReference(String className, Integer id) {
		this.className = className;
		this.id = id;
	}

	public String getClassName() {
		return className;
	}

	public Integer getId() {
		return id;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public void setId(Integer id) {
		this.id = id;
	}

}
