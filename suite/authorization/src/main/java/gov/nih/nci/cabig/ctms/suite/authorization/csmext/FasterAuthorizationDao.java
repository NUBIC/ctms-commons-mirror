package gov.nih.nci.cabig.ctms.suite.authorization.csmext;

import gov.nih.nci.cabig.ctms.suite.authorization.SuiteAuthorizationAccessException;
import gov.nih.nci.logging.api.logger.hibernate.HibernateSessionFactoryHelper;
import gov.nih.nci.security.authorization.domainobjects.Privilege;
import gov.nih.nci.security.authorization.domainobjects.ProtectionElement;
import gov.nih.nci.security.authorization.domainobjects.ProtectionElementPrivilegeContext;
import gov.nih.nci.security.dao.AuthorizationDAOImpl;
import gov.nih.nci.security.exceptions.CSConfigurationException;
import gov.nih.nci.security.exceptions.CSObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Provides faster versions of some methods in {@link AuthorizationDAOImpl},
 * tuned for the way the suite uses CSM.
 *
 * @author Rhett Sutphin
 */
public class FasterAuthorizationDao extends AuthorizationDAOImpl {
    private SessionFactory sf;

    public FasterAuthorizationDao(SessionFactory sf, String applicationContextName) throws CSConfigurationException {
        super(sf, applicationContextName);
        this.sf = sf;  // not exposed by superclass, so we have to capture it here
    }

    /**
     * This is a copy of the superclass method, modified to be faster for the specific requirements
     * of CCTS.  Changes:
     * <ul>
     *   <li>It does fewer separate database queries to load PEs and Privs.  10% speedup.</li>
     *   <li>It uses a simpler query to load the PE-to-Priv mapping.
     *       Specifically, it drops support for hierarchical protection groups.
     *       This gives approximately a 3000% speedup (2500ms to 80ms) in deployments with 100s of PGs.</li>
     * </ul>
     */
    @Override
    @SuppressWarnings({ "RawUseOfParameterizedType" })
    public Set getProtectionElementPrivilegeContextForUser(String userId) throws CSObjectNotFoundException {
        Set<ProtectionElementPrivilegeContext> protectionElementPrivilegeContextSet =
            Collections.emptySet();
        Session s = null;

        try {
            s = HibernateSessionFactoryHelper.getAuditSession(getSessionFactory());
            Map<Long, Set<Long>> peIdsToPrivIds = getProtectionElementIdToPrivilegeIdsForUser(s, userId);

            protectionElementPrivilegeContextSet = buildPePrivContexts(peIdsToPrivIds,
                getProtectionElementsByIds(s, peIdsToPrivIds.keySet()),
                loadAndIndexReferencedPrivileges(s, peIdsToPrivIds));
        } finally {
            if (s != null) s.close();
        }
        return protectionElementPrivilegeContextSet;
    }

    private Map<Long, Set<Long>> getProtectionElementIdToPrivilegeIdsForUser(Session s, String userId) {
        Map<Long, Set<Long>> peToPriv = new LinkedHashMap<Long, Set<Long>>();

        Connection connection;
        PreparedStatement ps;
        ResultSet rs;

        try {
            connection = s.connection();
            ps = SQLQueries.getQueryforUserPEPrivilegeMap(
                userId, this.getApplication().getApplicationId().intValue(), connection);
            rs = ps.executeQuery();

            while (rs.next()) {
                Long peId = rs.getLong(1);
                Long privId = rs.getLong(2);
                if (!peToPriv.containsKey(peId)) {
                    peToPriv.put(peId, new LinkedHashSet<Long>());
                }
                peToPriv.get(peId).add(privId);
            }
        } catch (SQLException e) {
            throw new SuiteAuthorizationAccessException("Loading the PE-Privilege ID map failed", e);
        }
        // don't close the connection because it is borrowed from hibernate

        return peToPriv;
    }

    @SuppressWarnings({ "unchecked" })
    private Collection<Privilege> getPrivilegesByIds(Session s, Collection<Long> privilegeIds) {
        return getAllObjectsByIds(s, Privilege.class, "id", privilegeIds);
    }

    @SuppressWarnings({ "unchecked" })
    private Collection<ProtectionElement> getProtectionElementsByIds(
        Session s, Collection<Long> protectionElementIds
    ) {
        return getAllObjectsByIds(s, ProtectionElement.class, "protectionElementId", protectionElementIds);
    }

    private Map<Long, Privilege> loadAndIndexReferencedPrivileges(
        Session s, Map<Long, Set<Long>> peIdToPrivIds
    ) {
        Map<Long, Privilege> privById = new HashMap<Long, Privilege>();
        {
            Set<Long> allPrivIds = new HashSet<Long>();
            for (Set<Long> privIds : peIdToPrivIds.values()) {
                allPrivIds.addAll(privIds);
            }
            Collection<Privilege> allPrivileges = getPrivilegesByIds(s, allPrivIds);
            for (Privilege priv : allPrivileges) privById.put(priv.getId(), priv);
        }
        return privById;
    }

    private Set<ProtectionElementPrivilegeContext> buildPePrivContexts(
        Map<Long, Set<Long>> peIdToPrivIds,
        Collection<ProtectionElement> allProtectionElements, Map<Long, Privilege> privById
    ) {
        Set<ProtectionElementPrivilegeContext> protectionElementPrivilegeContextSet;
        protectionElementPrivilegeContextSet = new LinkedHashSet<ProtectionElementPrivilegeContext>();
        for (ProtectionElement protectionElement : allProtectionElements) {
            ProtectionElementPrivilegeContext context = new ProtectionElementPrivilegeContext();
            context.setProtectionElement(protectionElement);
            Set<Privilege> privs = new LinkedHashSet<Privilege>();
            for (Long privId : peIdToPrivIds.get(protectionElement.getProtectionElementId())) {
                privs.add(privById.get(privId));
            }
            context.setPrivileges(privs);
            protectionElementPrivilegeContextSet.add(context);
        }
        return protectionElementPrivilegeContextSet;
    }

    // TODO: need to split up IN list
    @SuppressWarnings({ "unchecked" })
    private <T> Collection<T> getAllObjectsByIds(Session s, Class<T> entityClass, String idName, Collection<Long> ids) {
        return s.createCriteria(entityClass).add(Restrictions.in(idName, ids)).list();
    }

    protected SessionFactory getSessionFactory() {
        return sf;
    }
}
