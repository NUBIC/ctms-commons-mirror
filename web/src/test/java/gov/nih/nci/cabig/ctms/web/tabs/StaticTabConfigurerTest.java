package gov.nih.nci.cabig.ctms.web.tabs;

import junit.framework.TestCase;
import gov.nih.nci.cabig.ctms.web.chrome.Task;
import gov.nih.nci.cabig.ctms.CommonsSystemException;

/**
 * @author Rhett Sutphin
 */
public class StaticTabConfigurerTest extends TestCase {
    public void testObjectsAddedUsingTheirClassNames() throws Exception {
        Task expectedTask = new Task();
        CommonsSystemException expectedException = new CommonsSystemException("");
        StaticTabConfigurer configurer = new StaticTabConfigurer(expectedTask, expectedException);
        assertSame(expectedTask, configurer.getBeanFactory().getBean("task"));
        assertSame(expectedException, configurer.getBeanFactory().getBean("commonsSystemException"));
    }
    
    public void testAddingNamedObjectsWorks() throws Exception {
        StaticTabConfigurer configurer = new StaticTabConfigurer();
        configurer.addBean("named", 6);
        assertEquals(configurer.getBeanFactory().getBean("named"), 6);
    }
}
