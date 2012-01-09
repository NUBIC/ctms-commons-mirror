package gov.nih.nci.ccts.grid.service;

import gov.nih.nci.ccts.grid.common.LabConsumerServiceI;

import java.rmi.RemoteException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.xml.rpc.handler.soap.SOAPMessageContext;

import org.apache.axis.MessageContext;
import org.apache.axis.transport.http.HTTPConstants;
import org.globus.wsrf.config.ContainerConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * TODO:I am the service side implementation class. IMPLEMENT AND DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.2
 * 
 */
public class LabConsumerServiceImpl extends LabConsumerServiceImplBase {

	private static final String SPRING_CLASSPATH_EXPRESSION = "springClasspathExpression";

	private static final String DEFAULT_SPRING_CLASSPATH_EXPRESSION = "classpath:applicationContext-lab.xml";

	private static final String LAB_CONSUMER_BEAN_NAME = "labConsumerBeanName";

	private static final String DEFAULT_LAB_CONSUMER_BEAN_NAME = "labConsumer";

	private LabConsumerServiceI consumer;

	public LabConsumerServiceImpl() throws RemoteException {
		super();
	}

  public gov.nih.nci.cabig.ccts.domain.loadlabs.Acknowledgement loadLabs(gov.nih.nci.cabig.ccts.domain.loadlabs.LoadLabsRequest loadLabsRequest) throws RemoteException {
		initialize();
		return consumer.loadLabs(loadLabsRequest);
	}

	/**
	 * Will try to locate a pre-existent {@link ApplicationContext}; if failed,
	 * will create it explicitly.
	 * 
	 * @see http://jira.semanticbits.com/browse/CAAERS-4291
	 */
	private synchronized void initialize() {
		if (this.consumer == null) {
			ApplicationContext ctx = null;
			String exp = ContainerConfig.getConfig().getOption(
					SPRING_CLASSPATH_EXPRESSION,
					DEFAULT_SPRING_CLASSPATH_EXPRESSION);
			String bean = ContainerConfig.getConfig().getOption(
					LAB_CONSUMER_BEAN_NAME, DEFAULT_LAB_CONSUMER_BEAN_NAME);

			// see http://jira.semanticbits.com/browse/CAAERS-4291
			// let's see if ApplicationContext is already available as a
			// WebApplicationContext
			// if so, use it; otherwise, fall back to standard approach for
			// backward compatibility.
			SOAPMessageContext messageContext = MessageContext
					.getCurrentContext();
			if (messageContext != null) {
				HttpServlet srv = (HttpServlet) messageContext
						.getProperty(HTTPConstants.MC_HTTP_SERVLET);
				if (srv != null) {
					ServletContext servletContext = srv.getServletContext();
					ctx = WebApplicationContextUtils
							.getWebApplicationContext(servletContext);
				}
			}

			if (ctx == null) {
				System.out
						.println("LabConsumerServiceImpl: unable to find pre-existing spring context in servlet context; falling back to direct context creation.");
				System.out.println("LabConsumerServiceImpl: Creating spring context explicitly...");
				ctx = new ClassPathXmlApplicationContext(exp);

			}

			if (ctx != null)
				this.consumer = (LabConsumerServiceI) ctx.getBean(bean);

		}
	}

}
