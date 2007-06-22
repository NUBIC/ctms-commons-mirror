package gov.nih.nci.cabig.ctms.tools.hibernate;

import gov.nih.nci.cabig.ctms.testing.CommonsTestCase;
import gov.nih.nci.cabig.ctms.domain.SampleCodedEnum;

import java.sql.Types;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.Properties;

import static org.easymock.classextension.EasyMock.*;

/**
 * @author Rhett Sutphin
 */
public class CodedEnumTypeTest extends CommonsTestCase {
    private ResultSet rs;
    private CodedEnumType type;
    private PreparedStatement stmt;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        type = new CharacterCodedEnumType();
        Properties p = new Properties();
        p.setProperty(CodedEnumType.ENUM_CLASS_PARAM_KEY, SampleCodedEnum.class.getName());
        type.setParameterValues(p);

        rs = registerMockFor(ResultSet.class);
        stmt = registerMockFor(PreparedStatement.class);
    }

    public void testParameterDefaults() throws Exception {
        assertEquals("getByCode",
            type.getParameterValues().getProperty(CodedEnumType.FACTORY_METHOD_PARAM_KEY));
        assertEquals("getCode",
            type.getParameterValues().getProperty(CodedEnumType.KEY_METHOD_PARAM_KEY));
    }

    public void testSqlTypes() throws Exception {
        assertEquals(1, type.sqlTypes().length);
        assertEquals(Types.CHAR, type.sqlTypes()[0]);
    }

    public void testReturnedClass() throws Exception {
        assertSame(SampleCodedEnum.class, type.returnedClass());
    }

    public void testNullSafeGetWhenNull() throws Exception {
        String expectedName = "sample";
        expect(rs.getString("sample")).andReturn(null);
        replayMocks();

        assertNull(type.nullSafeGet(rs, new String[] { expectedName }, null));
        verifyMocks();
    }

    public void testNullSafeGetWhenValidCode() throws Exception {
        String expectedName = "sample";
        expect(rs.getString("sample")).andReturn("A");
        replayMocks();

        assertSame(SampleCodedEnum.AIRSHIP, type.nullSafeGet(rs, new String[] { expectedName }, null));
        verifyMocks();
    }

    public void testNullSafeSetWhenNull() throws Exception {
        int expectedIndex = 2;
        stmt.setObject(2, null, Types.CHAR);
        replayMocks();

        type.nullSafeSet(stmt, null, expectedIndex);
        verifyMocks();
    }
    
    public void testNullSafeSetWhenNotNull() throws Exception {
        int expectedIndex = 2;
        stmt.setObject(2, 'C', Types.CHAR);
        replayMocks();

        type.nullSafeSet(stmt, SampleCodedEnum.COMPASS, expectedIndex);
        verifyMocks();
    }

    public void testEqualsIsNullSafe() throws Exception {
        assertFalse(type.equals(SampleCodedEnum.AIRSHIP, null));
        assertFalse(type.equals(null, SampleCodedEnum.AIRSHIP));
        assertTrue(type.equals(null, null));
    }

    public void testEquals() throws Exception {
        assertTrue(type.equals(SampleCodedEnum.AIRSHIP, SampleCodedEnum.AIRSHIP));
        assertFalse(type.equals(SampleCodedEnum.GRIZZLY, SampleCodedEnum.AIRSHIP));
    }

    // matches SampleCodedEnum -- could promote to production level if needed
    @SuppressWarnings("RawUseOfParameterizedType")
    private static class CharacterCodedEnumType extends CodedEnumType {
        @Override
        protected int codeSqlType() {
            return Types.CHAR;
        }

        @Override
        protected Class codeJavaType() {
            return Character.TYPE;
        }

        @Override
        protected Object getKeyObject(ResultSet rs, String colname) throws SQLException {
            String s = rs.getString(colname);
            return s == null ? null : s.charAt(0);
        }
    }
}
