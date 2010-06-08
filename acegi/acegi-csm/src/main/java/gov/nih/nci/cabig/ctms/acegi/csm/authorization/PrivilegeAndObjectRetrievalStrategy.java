package gov.nih.nci.cabig.ctms.acegi.csm.authorization;

import org.aspectj.lang.ProceedingJoinPoint;

public interface PrivilegeAndObjectRetrievalStrategy {
	
	PrivilegeAndObject retrieve(ProceedingJoinPoint pjp);

}
