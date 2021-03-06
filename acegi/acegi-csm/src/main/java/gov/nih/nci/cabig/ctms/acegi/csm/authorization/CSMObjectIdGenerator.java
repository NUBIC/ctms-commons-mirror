package gov.nih.nci.cabig.ctms.acegi.csm.authorization;

public interface CSMObjectIdGenerator {

	/**
	 * Returns a CSM objectId, given an object.
	 * 
	 * @param object from which ID should be generated
	 * @return CSM objectId
	 */
	String generateId(Object object);
	
}
