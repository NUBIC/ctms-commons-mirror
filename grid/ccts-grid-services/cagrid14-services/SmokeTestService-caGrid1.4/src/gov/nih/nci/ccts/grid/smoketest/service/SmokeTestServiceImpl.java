package gov.nih.nci.ccts.grid.smoketest.service;

import java.rmi.RemoteException;

/** 
 * TODO:I am the service side implementation class.  IMPLEMENT AND DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.2
 * 
 */
public class SmokeTestServiceImpl extends SmokeTestServiceImplBase {

	
	public SmokeTestServiceImpl() throws RemoteException {
		super();
	}
	
  public void ping() throws RemoteException {
      System.out.println("Recieved Ping Request..");
  }

}

