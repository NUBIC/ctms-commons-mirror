package gov.nih.nci.cabig.ctms.acegi.csm.authorization;

import org.aspectj.lang.Signature;

public interface SignatureToPrivilegeMapping {
	
	boolean matches(Signature sig);
	
	String getPrivilege();

}
