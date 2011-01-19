package gov.nih.nci.cabig.ccts.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Array;
import java.util.Arrays;

/**
 *
 * @author Ion C. Olaru
 *
 */
public class BooleanDelimiter {
    protected final static Log log = LogFactory.getLog(BooleanDelimiter.class);

    private final static String delimiter = "([&|\\||(|)\\s])+"; // "&|() "
    private static String[] operands;

    public static String[] parseBoolean(String s) {
        log.debug(">>> INPUT: " + s);
        operands = s.split(delimiter);
        log.debug(">>> OUTPUT: " + Arrays.toString(operands));
        return operands;
    }

}