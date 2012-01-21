package com.sharepast.dal.dao.hibernate;

import com.sharepast.dal.dao.SecurityPermissionDAO;
import com.sharepast.dal.domain.SecurityPermission;
import com.sharepast.persistence.orm.hibernate.CriteriaDetails;
import com.sharepast.persistence.orm.hibernate.HibernateDao;
import com.sharepast.persistence.orm.hibernate.QueryDetails;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 3/23/11
 * Time: 11:33 PM
 * To change this template use File | Settings | File Templates.
 */
@Repository("securityPermissionDao")
public class SecurityPermissionHibernateDAO extends HibernateDao<SecurityPermission> implements SecurityPermissionDAO {

    public SecurityPermissionHibernateDAO() {
    }

    public Class<SecurityPermission> getManagedClass() {
        return SecurityPermission.class;
    }

    public SecurityPermission findOrCreate(String name) {

        SecurityPermission permission = find(name);

        if (permission == null) {
            permission = new SecurityPermission();
            permission.setName(name);
            permission = persist(permission);
        }
        return permission;
    }


    public SecurityPermission find(final String name) {
        return findByCriteria(new CriteriaDetails() {

            public Criteria completeCriteria(Criteria criteria) {
                return criteria.add(Restrictions.eq("name", name));
            }
        });
    }

    public List<SecurityPermission> getPermissionsForRole(final long roleId) {

        return listByQuery(new QueryDetails() {
            public String getQueryString() {
                return "select securityRole.permissions from SecurityRole securityRole where securityRole.id = :roleId";
            }

            public Query completeQuery(Query query) {
                return query.setLong("roleId", roleId);
            }
        });
    }
}
