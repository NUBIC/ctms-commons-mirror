package gov.nih.nci.cabig.ctms.audit.dao.query;

import gov.nih.nci.cabig.ctms.audit.domain.Operation;

import java.util.Date;

/**
 * @author Saurabh Agrawal
 */
public class DataAuditEventQuery extends AbstractQuery {

    private static String queryString = "select distinct e from DataAuditEvent e order by e.id desc";

    private static String CLASS_NAME = "className";

    private static String URL = "url";

    private static String ENTITY_ID = "entityId";
    private static String START_DATE = "startDate";
    private static String END_DATE = "endDate";
    private static String OPERATION = "operation";

    public DataAuditEventQuery() {
        super(queryString);
    }

    public void filterByClassName(final String className) {
        andWhere("e.reference.className=:" + CLASS_NAME);
        setParameter(CLASS_NAME, className);
    }

    public void filterByURL(final String url) {
        andWhere("e.info.url=:" + URL);
        setParameter(URL, url);
    }

    public void filterByOperation(final Operation operation) {
        andWhere("e.operation=:" + OPERATION);
        setParameter(OPERATION, operation);
    }

    public void filterByEntityId(Integer entityId) {
        andWhere("e.reference.id= :" + ENTITY_ID);
        setParameter(ENTITY_ID, entityId);
    }

    public void filterByStartDateAfter(Date startDate) {
        andWhere("e.info.time>=:" + START_DATE);
        setParameter(START_DATE, startDate);

    }

    public void filterByEndDateBefore(Date endDate) {
        andWhere("e.info.time<=:" + END_DATE);
        setParameter(END_DATE, endDate);

    }
}
