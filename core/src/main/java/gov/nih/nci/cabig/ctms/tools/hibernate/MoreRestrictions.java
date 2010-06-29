package gov.nih.nci.cabig.ctms.tools.hibernate;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Hibernate criteria builder whose "in" can automatically segment the value list for (e.g.) Oracle
 * compatibility.
 *
 * @author Rhett Sutphin
 * @see Restrictions
 */
public class MoreRestrictions {
    private static final Logger log = LoggerFactory.getLogger(MoreRestrictions.class);

    private static final int DEFAULT_MAX_LENGTH = 900;

    public static Criterion in(String propertyName, Collection<?> values) {
        return in(propertyName, values, DEFAULT_MAX_LENGTH);
    }

    public static Criterion in(String propertyName, Collection<?> values, int maxLength) {
        if (maxLength < 1) throw new IllegalArgumentException("maxLength must be positive");
        Disjunction result = Restrictions.disjunction();
        for (Collection<?> part : partitionCollection(values, maxLength)) {
            result.add(Restrictions.in(propertyName, part));
        }
        return result;
    }

    private static List<Collection<?>> partitionCollection(Collection<?> values, int maxLength) {
        int partitionCount = positiveIntegerCeiling(values.size(), maxLength);

        // A possibly premature optimization.
        // Seems worthwhile since most uses will not require segmentation.
        if (partitionCount < 2) return Arrays.<Collection<?>>asList(values);

        int balancedLength = positiveIntegerCeiling(values.size(), partitionCount);

        if (log.isDebugEnabled()) {
            log.debug("Partitioning list of {} values into {} list{} of at most {} in order to stay under {} values per clause",
                new Object[] { values.size(), partitionCount, partitionCount == 1 ? "" : 's', balancedLength, maxLength });
        }

        List<Collection<?>> partitioned = new ArrayList<Collection<?>>(partitionCount);
        Object[] valuesArray = values.toArray();
        while (partitioned.size() * balancedLength < values.size()) {
            int from = partitioned.size() * balancedLength;
            int to = Math.min(from + balancedLength, values.size());
            partitioned.add(Arrays.asList(valuesArray).subList(from, to));
        }
        return partitioned;
    }

    private static int positiveIntegerCeiling(int dividend, int divisor) {
        return dividend / divisor + (dividend % divisor == 0 ? 0 : 1);
    }
}
