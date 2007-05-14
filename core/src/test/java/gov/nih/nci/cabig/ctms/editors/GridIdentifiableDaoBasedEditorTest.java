package gov.nih.nci.cabig.ctms.editors;

import gov.nih.nci.cabig.ctms.testing.CommonsTestCase;
import gov.nih.nci.cabig.ctms.testing.TestObject;
import static org.easymock.classextension.EasyMock.expect;

/**
 * @author Rhett Sutphin
 */
public class GridIdentifiableDaoBasedEditorTest extends CommonsTestCase {
    private static final Integer ID = 13;
    private static final String GRID_ID = "BIG-FAKE";
    private static final TestObject OBJECT = new TestObject(ID, GRID_ID);

    private GridIdentifiableDaoBasedEditor editor;
    private TestObject.MockableDao dao;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        dao = registerDaoMockFor(TestObject.MockableDao.class);
        editor = new GridIdentifiableDaoBasedEditor(dao);
    }

    public void testSetAsId() throws Exception {
        expect(dao.getById(ID)).andReturn(OBJECT);

        replayMocks();
        editor.setAsText(ID.toString());
        verifyMocks();

        assertSame(OBJECT, editor.getValue());
    }

    public void testSetAsBigId() throws Exception {
        expect(dao.getByGridId(GRID_ID)).andReturn(OBJECT);

        replayMocks();
        editor.setAsText(GRID_ID);
        verifyMocks();

        assertSame(OBJECT, editor.getValue());
    }

    public void testSetAsTextWithInvalidId() throws Exception {
        Integer expectedId = 23;
        expect(dao.getById(expectedId)).andReturn(null);
        expect(dao.getByGridId(expectedId.toString())).andReturn(null);

        replayMocks();
        try {
            editor.setAsText(expectedId.toString());
            fail("Exception not thrown");
        } catch (IllegalArgumentException iae) {
            verifyMocks();
            assertEquals("There is no " + TestObject.class.getSimpleName() + " with id or gridId " + expectedId, iae.getMessage());
        }
    }

    public void testSetAsTextWithInvalidNonNumericId() throws Exception {
        String expectedId = "Zipper";
        expect(dao.getByGridId(expectedId)).andReturn(null);

        replayMocks();
        try {
            editor.setAsText(expectedId);
            fail("Exception not thrown");
        } catch (IllegalArgumentException iae) {
            verifyMocks();
            assertEquals("There is no " + TestObject.class.getSimpleName() + " with id or gridId " + expectedId, iae.getMessage());
        }
    }

    public void testGetAsText() throws Exception {
        editor.setValue(OBJECT);
        assertEquals("as text should be db ID", ID.toString(), editor.getAsText());
    }
}
