package gov.nih.nci.cabig.ctms.acegi.csm.authorization;

import org.acegisecurity.Authentication;

public interface ObjectResultHandler {
	
	Object handle(Authentication authentication, Object secureObject, Object returnedObject);

}
