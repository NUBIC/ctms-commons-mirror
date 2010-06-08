package gov.nih.nci.cabig.ctms.acegi.csm.authorization;

import org.aspectj.lang.Signature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegexSignatureToPrivilegeMapping implements
		SignatureToPrivilegeMapping {
	
	private static final Logger logger = LoggerFactory.getLogger(RegexSignatureToPrivilegeMapping.class);
	
	
	private String privilege;
	private String pattern;

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public void setPrivilege(String privilege) {
		this.privilege = privilege;
	}

	public String getPrivilege() {
		return this.privilege;
	}

	public boolean matches(Signature sig) {
		
		String sigStr = sig.getDeclaringTypeName() + "." + sig.getName();
		logger.debug("Comparing " + getPattern() + " to " + sigStr);
		return sigStr.matches(getPattern());
	}

}
