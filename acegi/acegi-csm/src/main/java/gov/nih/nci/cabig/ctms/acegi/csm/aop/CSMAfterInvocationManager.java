package gov.nih.nci.cabig.ctms.acegi.csm.aop;

import gov.nih.nci.cabig.ctms.acegi.csm.authorization.ObjectResultHandler;
import org.acegisecurity.AccessDeniedException;
import org.acegisecurity.AfterInvocationManager;
import org.acegisecurity.Authentication;
import org.acegisecurity.ConfigAttribute;
import org.acegisecurity.ConfigAttributeDefinition;

import java.util.Iterator;

public class CSMAfterInvocationManager implements
		AfterInvocationManager {

	private String processConfigAttribute;

	private ObjectResultHandler objectResultHandler;

	public ObjectResultHandler getObjectResultHandler() {
		return objectResultHandler;
	}

	public void setObjectResultHandler(ObjectResultHandler objectResultHandler) {
		this.objectResultHandler = objectResultHandler;
	}

	public String getProcessConfigAttribute() {
		return processConfigAttribute;
	}

	public void setProcessConfigAttribute(String processConfigAttribute) {
		this.processConfigAttribute = processConfigAttribute;
	}



	public Object decide(Authentication authentication, Object secureObject,
			ConfigAttributeDefinition config, Object returnedObject)
			throws AccessDeniedException {

		Object retVal = returnedObject;
		if (retVal != null) {


			for (Iterator i = config.getConfigAttributes(); i.hasNext();) {
				ConfigAttribute att = (ConfigAttribute) i.next();
				if (supports(att)) {
					retVal = getObjectResultHandler().handle(authentication, secureObject, returnedObject);

					break;
				}
			}


		}
		return retVal;
	}


	public boolean supports(ConfigAttribute attribute) {
		if ((attribute.getAttribute() != null)
				&& attribute.getAttribute().equals(getProcessConfigAttribute())) {
			return true;
		} else {
			return false;
		}
	}

	public boolean supports(Class config) {
		return true;
	}




}
