package gov.nih.nci.ccts.grid.client;

import java.io.InputStream;
import java.io.FileReader;
import java.rmi.RemoteException;

import javax.xml.namespace.QName;

import org.apache.axis.EngineConfiguration;
import org.apache.axis.client.AxisClient;
import org.apache.axis.client.Stub;
import org.apache.axis.configuration.FileProvider;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;

import org.oasis.wsrf.properties.GetResourcePropertyResponse;

import org.globus.gsi.GlobusCredential;

import gov.nih.nci.ccts.grid.stubs.RegistrationConsumerPortType;
import gov.nih.nci.ccts.grid.stubs.types.InvalidRegistrationException;
import gov.nih.nci.ccts.grid.stubs.types.RegistrationConsumptionException;
import gov.nih.nci.ccts.grid.stubs.service.RegistrationConsumerServiceAddressingLocator;
import gov.nih.nci.ccts.grid.common.RegistrationConsumerI;
import gov.nih.nci.cagrid.introduce.security.client.ServiceSecurityClient;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cabig.ccts.domain.Registration;
import gov.nih.nci.cabig.ccts.domain.ScheduledNonTreatmentEpochType;

/**
 * This class is autogenerated, DO NOT EDIT GENERATED GRID SERVICE ACCESS METHODS.
 *
 * This client is generated automatically by Introduce to provide a clean unwrapped API to the
 * service.
 *
 * On construction the class instance will contact the remote service and retrieve it's security
 * metadata description which it will use to configure the Stub specifically for each method call.
 * 
 * @created by Introduce Toolkit version 1.2
 */
public class RegistrationConsumerClient extends RegistrationConsumerClientBase implements RegistrationConsumerI {	

	public RegistrationConsumerClient(String url) throws MalformedURIException, RemoteException {
		this(url,null);	
	}

	public RegistrationConsumerClient(String url, GlobusCredential proxy) throws MalformedURIException, RemoteException {
	   	super(url,proxy);
	}
	
	public RegistrationConsumerClient(EndpointReferenceType epr) throws MalformedURIException, RemoteException {
	   	this(epr,null);
	}
	
	public RegistrationConsumerClient(EndpointReferenceType epr, GlobusCredential proxy) throws MalformedURIException, RemoteException {
	   	super(epr,proxy);
	}

	public static void usage(){
		System.out.println(RegistrationConsumerClient.class.getName() + " -url <service url>");
	}


    private Registration getRegistration(final String registrationMessageFile) throws Exception {
        ScheduledNonTreatmentEpochType   sdfad=null;
        FileReader reader = new FileReader(registrationMessageFile);
        InputStream is = getClass().getResourceAsStream("client-config.wsdd");
        gov.nih.nci.cabig.ccts.domain.Registration registration = (gov.nih.nci.cabig.ccts.domain.Registration)
                Utils.deserializeObject(reader, gov.nih.nci.cabig.ccts.domain.Registration.class, is);

        return registration;
    }



	
	public static void main(String [] args)  {
	    System.out.println("Running the Grid Service Client");
		try{
		if(!(args.length < 2)){
			if(args[0].equals("-url")){
			  RegistrationConsumerClient client = new RegistrationConsumerClient(args[1]);
			        Registration notificationType = client.getRegistration(
                            "/Users/saurabhagrawal/projects/latest/psc-2.1/grid/registration-consumer/test/resources/SampleRegistrationMessage.xml");
                try{
                    client.register(notificationType);

                }catch (Exception e){
                    e.printStackTrace();
                }

			} else {
				usage();
				System.exit(1);
			}
		} else {
			usage();
			System.exit(1);
		}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

  public void rollback(gov.nih.nci.cabig.ccts.domain.Registration registration) throws RemoteException, gov.nih.nci.ccts.grid.stubs.types.InvalidRegistrationException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"rollback");
    gov.nih.nci.ccts.grid.stubs.RollbackRequest params = new gov.nih.nci.ccts.grid.stubs.RollbackRequest();
    gov.nih.nci.ccts.grid.stubs.RollbackRequestRegistration registrationContainer = new gov.nih.nci.ccts.grid.stubs.RollbackRequestRegistration();
    registrationContainer.setRegistration(registration);
    params.setRegistration(registrationContainer);
    gov.nih.nci.ccts.grid.stubs.RollbackResponse boxedResult = portType.rollback(params);
    }
  }

  public void commit(gov.nih.nci.cabig.ccts.domain.Registration registration) throws RemoteException, gov.nih.nci.ccts.grid.stubs.types.InvalidRegistrationException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"commit");
    gov.nih.nci.ccts.grid.stubs.CommitRequest params = new gov.nih.nci.ccts.grid.stubs.CommitRequest();
    gov.nih.nci.ccts.grid.stubs.CommitRequestRegistration registrationContainer = new gov.nih.nci.ccts.grid.stubs.CommitRequestRegistration();
    registrationContainer.setRegistration(registration);
    params.setRegistration(registrationContainer);
    gov.nih.nci.ccts.grid.stubs.CommitResponse boxedResult = portType.commit(params);
    }
  }

  public gov.nih.nci.cabig.ccts.domain.Registration register(gov.nih.nci.cabig.ccts.domain.Registration registration) throws RemoteException, gov.nih.nci.ccts.grid.stubs.types.InvalidRegistrationException, gov.nih.nci.ccts.grid.stubs.types.RegistrationConsumptionException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"register");
    gov.nih.nci.ccts.grid.stubs.RegisterRequest params = new gov.nih.nci.ccts.grid.stubs.RegisterRequest();
    gov.nih.nci.ccts.grid.stubs.RegisterRequestRegistration registrationContainer = new gov.nih.nci.ccts.grid.stubs.RegisterRequestRegistration();
    registrationContainer.setRegistration(registration);
    params.setRegistration(registrationContainer);
    gov.nih.nci.ccts.grid.stubs.RegisterResponse boxedResult = portType.register(params);
    return boxedResult.getRegistration();
    }
  }

  public org.oasis.wsrf.properties.GetMultipleResourcePropertiesResponse getMultipleResourceProperties(org.oasis.wsrf.properties.GetMultipleResourceProperties_Element params) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"getMultipleResourceProperties");
    return portType.getMultipleResourceProperties(params);
    }
  }

  public org.oasis.wsrf.properties.GetResourcePropertyResponse getResourceProperty(javax.xml.namespace.QName params) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"getResourceProperty");
    return portType.getResourceProperty(params);
    }
  }

  public org.oasis.wsrf.properties.QueryResourcePropertiesResponse queryResourceProperties(org.oasis.wsrf.properties.QueryResourceProperties_Element params) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"queryResourceProperties");
    return portType.queryResourceProperties(params);
    }
  }

}
