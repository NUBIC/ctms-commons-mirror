package gov.nih.nci.cabig.ctms.editors;

import gov.nih.nci.cabig.ctms.testing.CommonsTestCase;
import gov.nih.nci.cabig.ctms.testing.TestObject;
import static org.easymock.classextension.EasyMock.*;

/**
 * @author Rhett Sutphin
 */
public class DaoBasedEditorTest extends CommonsTestCase {
    private static final Integer ID = 13;
    private static final TestObject OBJECT = new TestObject(ID);

    private TestObject.MockableDao dao;
    private DaoBasedEditor editor;

    @Override
    @SuppressWarnings("unchecked")
    protected void setUp() throws Exception {
        super.setUp();
        dao = registerDaoMockFor(TestObject.MockableDao.class);
        editor = new DaoBasedEditor(dao);
    }

    public void testSetAsTextWithValidId() throws Exception {
        expect(dao.getById(ID)).andReturn(OBJECT);

        replayMocks();
        editor.setAsText(ID.toString());
        verifyMocks();

        assertSame(OBJECT, editor.getValue());
    }

    public void testSetAsTextWithInvalidId() throws Exception {
        Integer expectedId = 23;
        expect(dao.getById(expectedId)).andReturn(null);

        replayMocks();
        try {
            editor.setAsText(expectedId.toString());
            fail("Exception not thrown");
        } catch (IllegalArgumentException iae) {
            verifyMocks();
            assertEquals("There is no " + TestObject.class.getSimpleName() + " with id=" + expectedId, iae.getMessage());
        }
    }

    public void testSetAsTextNull() throws Exception {
        replayMocks();
        editor.setAsText(null);
        verifyMocks();
        assertNull(editor.getValue());
    }

    public void testGetAsText() throws Exception {
        editor.setValue(OBJECT);
        assertEquals(ID.toString(), editor.getAsText());
    }

    public void testSetValueWithoutId() throws Exception {
        editor.setStrictIdChecking(true);
        try {
            editor.setValue(new TestObject());
            fail("Exception not thrown");
        } catch (IllegalArgumentException iae) {
            assertEquals("This editor can't handle values without IDs", iae.getMessage());
        }
    }

    public void testSetValueNull() throws Exception {
        replayMocks();
        editor.setValue(null);
        verifyMocks();
        assertNull(editor.getAsText());
    }
}
