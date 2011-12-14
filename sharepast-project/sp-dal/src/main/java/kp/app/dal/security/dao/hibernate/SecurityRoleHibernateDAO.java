package kp.app.dal.security.dao.hibernate;

import kp.app.dal.domain.user.User;
import kp.app.dal.security.dao.SecurityRoleDAO;
import kp.app.dal.security.domain.SecurityRole;
import kp.app.persistence.orm.hibernate.CriteriaDetails;
import kp.app.persistence.orm.hibernate.HibernateDao;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 3/23/11
 * Time: 11:24 PM
 * To change this template use File | Settings | File Templates.
 */
@Repository("securityRoleDao")
public class SecurityRoleHibernateDAO extends HibernateDao<SecurityRole> implements SecurityRoleDAO {

  public SecurityRoleHibernateDAO() {}

  public Class<SecurityRole> getManagedClass () {
    return SecurityRole.class;
  }

    public SecurityRole find (final String name) {

    return findByCriteria(new CriteriaDetails() {
      public Criteria completeCriteria (Criteria criteria) {
        return criteria.add(Restrictions.eq("name", name));
      }
    });
  }


  public SecurityRole findOrCreate (String name) {

    SecurityRole role = find(name);

    if (role == null) {
      role = new SecurityRole();
      role.setName(name);
      role = persist(role);
    }

    return role;
  }


  public Set<SecurityRole> getRolesForAccount (final long userId) {

      User user = (User) getSession().get(User.class, userId);
      if (user == null) {
          return Collections.EMPTY_SET;
      }
      return user.getRoles();
  }
}

