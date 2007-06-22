package gov.nih.nci.cabig.ctms.tools.hibernate;

import gov.nih.nci.cabig.ctms.testing.CommonsTestCase;
import static org.easymock.classextension.EasyMock.*;

import java.sql.ResultSet;
import java.sql.Types;

/**
 * @author Rhett Sutphin
 */
public class IntegerCodedEnumTypeTest extends CommonsTestCase {
    private IntegerCodedEnumType type;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        type = new IntegerCodedEnumType();
    }

    public void testGetKeyObject() throws Exception {
        ResultSet rs = createMock(ResultSet.class);
        expect(rs.getInt("col")).andReturn(7);
        replay(rs);

        assertEquals(7, type.getKeyObject(rs, "col"));
        verifyMocks();
    }

    public void testSqlType() throws Exception {
        assertEquals(Types.INTEGER, type.codeSqlType());
    }

    public void testKeyClass() throws Exception {
        assertEquals(Integer.TYPE, type.codeJavaType());
    }
}
