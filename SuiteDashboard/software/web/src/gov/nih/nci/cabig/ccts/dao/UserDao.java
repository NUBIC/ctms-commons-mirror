package gov.nih.nci.cabig.ccts.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.classic.Session;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public class UserDao extends HibernateDaoSupport {

    private static final Log log = LogFactory.getLog(UserDao.class);

    public boolean userExists(String loginName) {
        String strSQL = "SELECT login_name FROM csm_user WHERE login_name = :loginName";
        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        log.debug(String.format("Firing SQL <%s>", strSQL));
        Query query = session.createSQLQuery(strSQL);
        List results = query.setString("loginName", loginName).list();
        log.debug(String.format("FOUND USERS BY loginName %s:  %d", loginName, results.size()));
        return results.size() > 0;
    }

}
