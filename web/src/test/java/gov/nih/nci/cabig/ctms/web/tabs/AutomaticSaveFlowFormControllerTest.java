package gov.nih.nci.cabig.ctms.web.tabs;

import gov.nih.nci.cabig.ctms.dao.MutableDomainObjectDao;
import gov.nih.nci.cabig.ctms.domain.AbstractMutableDomainObject;
import gov.nih.nci.cabig.ctms.domain.DomainObject;
import gov.nih.nci.cabig.ctms.web.WebTestCase;
import static org.easymock.classextension.EasyMock.*;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author Rhett Sutphin
 */
public class AutomaticSaveFlowFormControllerTest extends WebTestCase {
    private TestController controller;
    private TestObject command;
    private MutableDomainObjectDao<TestObject> dao;

    @Override
    @SuppressWarnings("unchecked")
    protected void setUp() throws Exception {
        super.setUp();
        dao = createMock(MutableDomainObjectDao.class);
        controller = new TestController();
        command = new TestObject();
        request.getSession().setAttribute(TestController.class.getName() + ".FORM.command", command);
        request.getSession().setAttribute(TestController.class.getName() + ".PAGE.command", 0);
    }

    public void testShouldSaveWhenSaved() throws Exception {
        command.setId(5);
        assertTrue(controller.shouldSave(request, command, controller.getFlow().getTab(0)));
    }

    public void testShouldNotSaveWhenNotSaved() throws Exception {
        command.setId(null);
        assertFalse(controller.shouldSave(request, command, controller.getFlow().getTab(0)));
    }

    public void testWillSaveFlagIncludedInRefdata() throws Exception {
        Map<?, ?> refdata = controller.referenceData(request, command, errors, 0);
        assertTrue(refdata.containsKey("willSave"));
        assertEquals(Boolean.FALSE, refdata.get("willSave"));
    }

    public void testWillSaveFlagTrueIfShouldSave() throws Exception {
        command.setId(7);
        Map<?, ?> refdata = controller.referenceData(request, command, errors, 0);
        assertTrue(refdata.containsKey("willSave"));
        assertEquals(Boolean.TRUE, refdata.get("willSave"));
    }

    public void testSavesInPostProcessIfAppropriate() throws Exception {
        command.setId(3);
        dao.save(command);
        replay(dao);

        controller.postProcessPage(request, command, errors, 0);
        verify(dao);
    }

    public void testDoesNotSaveInPostProcessIfShouldNot() throws Exception {
        command.setId(null);
        replay(dao);

        controller.postProcessPage(request, command, errors, 0);
        verify(dao);
    }
    
    public void testDoesNotSaveInPostProcessIfThereAreErrors() throws Exception {
        command.setId(5); // so it would try to save
        errors.reject("fribazz", "The zooma blorked");
        replay(dao);

        controller.postProcessPage(request, command, errors, 0);
        verify(dao);
    }

    public void testPostProcessReplacesCommandIfProvided() throws Exception {
        command.setId(8);
        controller.setSaveResult(new TestObject(4));
        controller.postProcessPage(request, command, errors, 0);

        Object sessCmd = request.getSession().getAttribute(TestController.class.getName() + ".FORM.command.to-replace");
        assertNotNull("Command to replace not present in session", sessCmd);
        assertEquals("Wrong command to replace", 4, (int) ((DomainObject) sessCmd).getId());
    }

    public void testPostProcessDoesNotReplaceCommandIfSaveReturnsNull() throws Exception {
        command.setId(8);
        controller.setSaveResult(null);
        controller.postProcessPage(request, command, errors, 0);

        Object sessCmd = request.getSession().getAttribute(TestController.class.getName() + ".FORM.command.to-replace");
        assertNull("Should not be a command to replace in the session", sessCmd);
    }

    public void testCurrentFormObjReplacesCommandIfInSession() throws Exception {
        request.getSession().setAttribute(TestController.class.getName() + ".FORM.command.to-replace", new TestObject(7));
        Object actual = controller.currentFormObject(request, command);
        assertTrue("Wrong object returned: " + actual, actual instanceof TestObject);
        assertEquals("Wrong object returned", 7, (int) ((DomainObject) actual).getId());
    }

    public void testCurrentFormObjReturnsOriginalIfNoReplacementPresent() throws Exception {
        request.getSession().removeAttribute(TestController.class.getName() + ".FORM.command.to-replace");
        Object actual = controller.currentFormObject(request, command);
        assertSame("Wrong object returned", command, actual);
    }

    private class TestController extends AutomaticSaveFlowFormController<TestObject, TestObject, MutableDomainObjectDao<TestObject>> {
        private boolean finished;
        private TestObject postSaveCommand;

        public TestController() {
            setFlow(new Flow<TestObject>("Test flow"));
            getFlow().addTab(new SimpleTestTab<TestObject>(1));
            getFlow().addTab(new SimpleTestTab<TestObject>(2));
            getFlow().addTab(new SimpleTestTab<TestObject>(3));
        }

        @Override
        protected TestObject save(TestObject command, Errors errors) {
            super.save(command, errors);
            return postSaveCommand;
        }

        @Override
        protected MutableDomainObjectDao<TestObject> getDao() {
            return dao;
        }

        @Override
        protected TestObject getPrimaryDomainObject(TestObject command) {
            return command;
        }

        @Override
        protected ModelAndView processFinish(
            HttpServletRequest request, HttpServletResponse response, Object command, BindException errors
        ) throws Exception {
            finished = true;
            return null;
        }

        public boolean isFinished() {
            return finished;
        }

        public void setSaveResult(TestObject newCommand) {
            this.postSaveCommand = newCommand;
        }
    }

    private static class TestObject extends AbstractMutableDomainObject {
        public TestObject() { }
        public TestObject(int id) { setId(id); }
    }
}
