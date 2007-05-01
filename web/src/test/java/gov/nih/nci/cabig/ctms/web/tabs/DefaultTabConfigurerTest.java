package gov.nih.nci.cabig.ctms.web.tabs;

import junit.framework.TestCase;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.MutablePropertyValues;

import java.util.Collections;

/**
 * @author Rhett Sutphin
 */
public class DefaultTabConfigurerTest extends TestCase {
    private StaticApplicationContext applicationContext;
    private DefaultTabConfigurer configurer;
    private Flow<Object> flow;
    private TabZero tab0;
    private TabOne tab1;
    private TabAll tabA;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        applicationContext = new StaticApplicationContext();
        applicationContext.registerBeanDefinition("beanZero", createTestBeanDef(0));
        applicationContext.registerBeanDefinition("beanOne", createTestBeanDef(1));
        // deliberately no beanTwo

        configurer = new DefaultTabConfigurer();
        configurer.setApplicationContext(applicationContext);

        tab0 = new TabZero();
        tab1 = new TabOne();
        tabA = new TabAll();

        flow = new Flow<Object>("test");
        flow.addTab(tab0);
        flow.addTab(tab1);
        flow.addTab(tabA);
    }

    private RootBeanDefinition createTestBeanDef(int id) {
        return new RootBeanDefinition(TestBean.class, new MutablePropertyValues(Collections.singletonMap("id", id)));
    }

    private void doInject() {
        configurer.injectDependencies(flow);
    }

    public void testStandardInjection() throws Exception {
        doInject();
        assertNotNull(tab0.getBeanZero());
        assertEquals(0, tab0.getBeanZero().getId());
        assertNotNull(tab1.getBeanOne());
        assertEquals(1, tab1.getBeanOne().getId());
    }

    public void testMultiplePropertyInjection() throws Exception {
        doInject();
        assertNotNull(tabA.getBeanZero());
        assertEquals(0, tabA.getBeanZero().getId());
        assertNotNull(tabA.getBeanOne());
        assertEquals(1, tabA.getBeanOne().getId());
        assertNull("Should not be anything set for beanTwo", tabA.getBeanTwo());
    }

    private static class TabZero extends Tab<Object> {
        private TestBean beanZero;

        public TestBean getBeanZero() {
            return beanZero;
        }

        public void setBeanZero(TestBean beanZero) {
            this.beanZero = beanZero;
        }
    }

    private static class TabOne extends Tab<Object> {
        private TestBean beanOne;

        public TestBean getBeanOne() {
            return beanOne;
        }

        public void setBeanOne(TestBean beanOne) {
            this.beanOne = beanOne;
        }
    }

    private static class TabAll extends Tab<Object> {
        private TestBean beanZero;
        private TestBean beanOne;
        private TestBean beanTwo;

        public TestBean getBeanZero() {
            return beanZero;
        }

        public void setBeanZero(TestBean beanZero) {
            this.beanZero = beanZero;
        }

        public TestBean getBeanOne() {
            return beanOne;
        }

        public void setBeanOne(TestBean beanOne) {
            this.beanOne = beanOne;
        }

        public TestBean getBeanTwo() {
            return beanTwo;
        }

        public void setBeanTwo(TestBean beanTwo) {
            this.beanTwo = beanTwo;
        }
    }

    public static class TestBean {
        private int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}
