package gov.nih.nci.cabig.ctms.domain;

/**
 * Interface for enums which decouples lookup & display from the enum's default
 * {@link Enum#ordinal()} and {@link Enum#name()} methods.
 * <p>
 * Implementations will probably want to use {@link CodedEnumHelper} to reduce
 * repeated code.
 * </p>
 * <h3>Sample implementation</h3>
 * This is the <code>Grade</code> enum from caAERS.
 * <pre>
 * import static gov.nih.nci.cabig.ctms.domain.CodedEnumHelper.*;
 * import gov.nih.nci.cabig.ctms.domain.CodedEnum;
 *
 * public enum Grade implements CodedEnum&lt;Integer&gt; {
 *     NORMAL(0),
 *     MILD(1),
 *     MODERATE(2),
 *     SEVERE(3),
 *     LIFE_THREATENING(4, "Life-threatening or disabling"),
 *     DEATH(5);
 * 
 *     private int code;
 *     private String displayName;
 *
 *     Grade(int code) {
 *         this(code, null);
 *     }
 * 
 *     Grade(int code, String longName) {
 *         this.code = code;
 *         this.displayName = longName;
 *         register(this);
 *     }
 * 
 *     public static Grade getByCode(int code) {
 *         return getByClassAndCode(Grade.class, code);
 *     }
 * 
 *     public Integer getCode() {
 *         return code;
 *     }
 * 
 *     public String getDisplayName() {
 *         return displayName == null ? sentenceCasedName(this) : displayName;
 *     }
 * 
 *     public String toString() {
 *         return toStringHelper(this);
 *     }
 * }</pre>
 *
 * @author Rhett Sutphin
 */
public interface CodedEnum<C> {
    C getCode();
    String getDisplayName();
}
