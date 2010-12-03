package gov.nih.nci.cabig.ccts.dao;

import org.hibernate.classic.Session;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public class UserDao extends HibernateDaoSupport {

    public boolean userExists(String loginName) {
        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        return session.createSQLQuery(String.format("SELECT * FROM csm_user WHERE login_name = '%s'", new Object[]{loginName})).list().size() > 0;
    }

}
