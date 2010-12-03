package gov.nih.nci.cabig.ccts.dao;

import org.hibernate.Query;
import org.hibernate.classic.Session;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public class UserDao extends HibernateDaoSupport {

    public boolean userExists(String loginName) {
        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query query = session.createSQLQuery("SELECT login_name FROM csm_user WHERE login_name = :loginName");
        List results = query.setString("loginName", loginName).list();
        return results.size() > 0;
    }

}
