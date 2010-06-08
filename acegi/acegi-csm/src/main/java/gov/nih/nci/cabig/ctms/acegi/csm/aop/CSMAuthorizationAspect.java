package gov.nih.nci.cabig.ctms.acegi.csm.aop;

import gov.nih.nci.cabig.ctms.acegi.csm.authorization.CSMAuthorizationCheck;
import gov.nih.nci.cabig.ctms.acegi.csm.authorization.ObjectResultHandler;
import gov.nih.nci.cabig.ctms.acegi.csm.authorization.PrivilegeAndObject;
import gov.nih.nci.cabig.ctms.acegi.csm.authorization.PrivilegeAndObjectRetrievalStrategy;
import org.acegisecurity.AccessDeniedException;
import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContextHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CSMAuthorizationAspect {

	private Logger logger = LoggerFactory.getLogger(CSMAuthorizationAspect.class);

	private List<CSMAuthorizationCheck> authorizationChecks;

	private PrivilegeAndObjectRetrievalStrategy privilegeAndObjectRetrievalStrategy;

	private ObjectResultHandler objectResultHandler;

	public Object advise(ProceedingJoinPoint pjp) throws Throwable {

		logger.debug("############# advising...");

		Authentication authentication = SecurityContextHolder.getContext()
				.getAuthentication();

		PrivilegeAndObject pao = getPrivilegeAndObjectRetrievalStrategy()
				.retrieve(pjp);

		boolean isAuthorized = false;
		for (CSMAuthorizationCheck check : getAuthorizationChecks()) {
			logger.debug("checking authoriziation...");

			isAuthorized = check.checkAuthorization(authentication, pao
					.getPrivilege(), pao.getObject());
			if (isAuthorized) {
				break;
			}

		}

		if (!isAuthorized) {
			throw new AccessDeniedException("Access Denied");
		}

		return getObjectResultHandler().handle(authentication, pjp,
				pjp.proceed());
	}

	public List<CSMAuthorizationCheck> getAuthorizationChecks() {
		return authorizationChecks;
	}

	public void setAuthorizationChecks(
			List<CSMAuthorizationCheck> authorizationChecks) {
		this.authorizationChecks = authorizationChecks;
	}

	public ObjectResultHandler getObjectResultHandler() {
		return objectResultHandler;
	}

	public void setObjectResultHandler(ObjectResultHandler objectResultHandler) {
		this.objectResultHandler = objectResultHandler;
	}

	public PrivilegeAndObjectRetrievalStrategy getPrivilegeAndObjectRetrievalStrategy() {
		return privilegeAndObjectRetrievalStrategy;
	}

	public void setPrivilegeAndObjectRetrievalStrategy(
			PrivilegeAndObjectRetrievalStrategy privilegeAndObjectRetrievalStrategy) {
		this.privilegeAndObjectRetrievalStrategy = privilegeAndObjectRetrievalStrategy;
	}

}
