package gov.nih.nci.ccts.grid.service;

import gov.nih.nci.cagrid.introduce.servicetools.ServiceConfiguration;

import org.globus.wsrf.config.ContainerConfig;
import java.io.File;
import javax.naming.InitialContext;

import org.apache.axis.MessageContext;
import org.globus.wsrf.Constants;


/** 
 * DO NOT EDIT:  This class is autogenerated!
 * 
 * This class holds all service properties which were defined for the service to have
 * access to.
 * 
 * @created by Introduce Toolkit version 1.2
 * 
 */
public class RegistrationConsumerConfiguration implements ServiceConfiguration {

	public static RegistrationConsumerConfiguration  configuration = null;

	public static RegistrationConsumerConfiguration getConfiguration() throws Exception {
		if (RegistrationConsumerConfiguration.configuration != null) {
			return RegistrationConsumerConfiguration.configuration;
		}
		MessageContext ctx = MessageContext.getCurrentContext();

		String servicePath = ctx.getTargetService();

		String jndiName = Constants.JNDI_SERVICES_BASE_NAME + servicePath + "/serviceconfiguration";
		try {
			javax.naming.Context initialContext = new InitialContext();
			RegistrationConsumerConfiguration.configuration = (RegistrationConsumerConfiguration) initialContext.lookup(jndiName);
		} catch (Exception e) {
			throw new Exception("Unable to instantiate service configuration.", e);
		}

		return RegistrationConsumerConfiguration.configuration;
	}
	
	private String etcDirectoryPath;
	
	
	
	public String getEtcDirectoryPath() {
		return ContainerConfig.getBaseDirectory() + File.separator + etcDirectoryPath;
	}
	
	public void setEtcDirectoryPath(String etcDirectoryPath) {
		this.etcDirectoryPath = etcDirectoryPath;
	}

	
}
