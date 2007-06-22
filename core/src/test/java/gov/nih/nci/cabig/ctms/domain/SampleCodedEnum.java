package gov.nih.nci.cabig.ctms.domain;

import static gov.nih.nci.cabig.ctms.domain.CodedEnumHelper.*;

/**
 * @author Rhett Sutphin
 */
public enum SampleCodedEnum implements CodedEnum<Character> {
    GRIZZLY('G'),
    AIRSHIP('A'),
    COMPASS('C'),
    RADIO('R'),
    HUMAN_SKULL('S')
    ;

    private char code;

    SampleCodedEnum(char code) {
        this.code = code;
        register(this);
    }

    public Character getCode() {
        return code;
    }

    public static SampleCodedEnum getByCode(char code) {
        return getByClassAndCode(SampleCodedEnum.class, code);
    }

    public String getDisplayName() {
        return sentenceCasedName(this);
    }
}
