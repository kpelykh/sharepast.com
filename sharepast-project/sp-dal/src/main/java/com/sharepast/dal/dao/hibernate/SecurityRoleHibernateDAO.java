package com.sharepast.dal.dao.hibernate;

import com.sharepast.dal.dao.SecurityRoleDAO;
import com.sharepast.dal.domain.SecurityRole;
import com.sharepast.dal.domain.user.User;
import com.sharepast.persistence.orm.hibernate.CriteriaDetails;
import com.sharepast.persistence.orm.hibernate.HibernateDao;
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

