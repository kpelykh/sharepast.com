package com.sharepast.dao.hibernate;

import com.sharepast.dao.IGroupManager;
import com.sharepast.dao.IUserDAO;
import com.sharepast.domain.user.Permission;
import com.sharepast.domain.user.Role;
import com.sharepast.domain.user.User;
import com.sharepast.persistence.hibernate.CriteriaDetails;
import com.sharepast.persistence.hibernate.HibernateDao;
import com.sharepast.persistence.hibernate.QueryDetails;
import com.sharepast.util.ToStringHelper;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: kpelykh
 * Date: 2/21/12
 * Time: 11:08 PM
 * To change this template use File | Settings | File Templates.
 */
@Repository("groupManager")
@Transactional( propagation = Propagation.REQUIRED, rollbackFor = HibernateException.class)
public class GroupManagerImpl extends HibernateDao<Role> implements IGroupManager {

    @Autowired
    private IUserDAO userDao;

    private static final Logger LOG = LoggerFactory.getLogger(GroupManagerImpl.class);

    public Class<Role> getEntityClass() {
        return Role.class;
    }

    @Transactional(readOnly = true)
    public Role findByName(final String roleName) {
        Assert.hasText(roleName);
        return findByCriteria(new CriteriaDetails() {
            public Criteria completeCriteria(Criteria criteria) {
                return criteria.add(Restrictions.eq("name", roleName));
            }
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> findAllGroups() {
        return listByQuery(String.class, new QueryDetails() {
            public String getQueryString() {
                return "select name from Role";
            }
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> findUsersInGroup(final String roleName) {
        Assert.hasText(roleName);
        return listByQuery(String.class, new QueryDetails() {
            public String getQueryString() {
                return "select u.username from User u join u.roles r where r.name = :roleName";
            }
            @Override
            public Query completeQuery(Query query) {
                return query.setParameter("roleName", roleName);
            }
        });
    }

    @Override
    public void createGroup(String roleName, List<GrantedAuthority> authorities) {
        Assert.hasText(roleName);
        Assert.notNull(authorities);

        LOG.info(String.format("Creating new role %s with authorities %s",
                roleName,
                ToStringHelper.toString(AuthorityUtils.authorityListToSet(authorities))
        ));

        Role role = findByName(roleName);
        if (role == null) {
            role = new Role();
            role.setName(roleName);

            Set<Permission> authSet = new HashSet<Permission>();

            for (GrantedAuthority authority1 : authorities) {
                final String authority = authority1.getAuthority();
                Permission authFromDb = findByCriteria(Permission.class, new CriteriaDetails() {
                    public Criteria completeCriteria(Criteria criteria) {
                        return criteria.add(Restrictions.eq("name", authority));
                    }
                });
                if (authFromDb != null) {
                    authSet.add(authFromDb);
                } else {
                    Permission auth = new Permission(authority);
                    getCurrentSession().saveOrUpdate(auth);
                    authSet.add(auth);
                }
            }
            role.setPermissions(authSet);
            update(role);
        }
    }

    @Override
    public void deleteGroup(String roleName) {
        Assert.hasText(roleName);

        LOG.info(String.format("Deleting role %s", roleName));

        Role role = findByName(roleName);
        if (role == null) {
            LOG.warn(String.format("Error deleting role: couldn't find role '%s' in DB", roleName));
            return;
        }
        delete(role);
    }

    @Override
    public void renameGroup(String oldName, String newName) {
        Assert.hasText(oldName, "old name for role is empty");
        Assert.hasText(newName, "new name for role is empty");

        LOG.debug(String.format("Changing role name from %s to %s", oldName, newName));

        Role role = findByName(oldName);
        Assert.notNull(role, String.format("Error renaming role: couldn't find role '%s' in DB", oldName));

        role.setName(newName);
        update(role);
    }

    @Override
    public void addUserToGroup(String username, String roleName) {
        Assert.hasText(username, "username is empty");
        Assert.hasText(roleName, "role name is empty");

        LOG.info(String.format("Adding user %s to role %s", username, roleName));

        Role role = findByName(roleName);
        Assert.notNull(role, String.format("Error adding user to role: couldn't find role '%s' in DB", roleName));

        User user = userDao.findByUsername(username);
        Assert.notNull(role, String.format("Error adding user to role: couldn't find user '%s' in DB", username));

        if (user.getRoles().contains(role)) {
            LOG.warn(String.format("User %s already has role %s", username, roleName));
            return;
        }

        user.getRoles().add(role);

        userDao.persist(user);
    }

    @Override
    public void removeUserFromGroup(String username, String roleName) {
        Assert.hasText(username, "username is empty");
        Assert.hasText(roleName, "role name is empty");

        LOG.info(String.format("Removing user %s to role %s", username, roleName));

        Role role = findByName(roleName);
        Assert.notNull(role, String.format("Removing adding user from role: couldn't find role '%s' in DB", roleName));

        User user = userDao.findByUsername(username);
        Assert.notNull(role, String.format("Error Removing user from role: couldn't find user '%s' in DB", username));

        if (!user.getRoles().contains(role)) {
            LOG.warn(String.format("User %s doesn't have role %s, nothing ot remove", username, roleName));
            return;
        }

        user.getRoles().remove(role);

        userDao.persist(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GrantedAuthority> findGroupAuthorities(String roleName) {
        Assert.hasText(roleName, "role name is empty");

        Role role = findByName(roleName);
        Assert.notNull(role, String.format("Couldn't find role '%s' in DB", roleName));

        return new ArrayList<GrantedAuthority>(role.getPermissions());
    }

    @Override
    public void addGroupAuthority(String roleName, final GrantedAuthority authority) {
        Assert.hasText(roleName, "role name is empty");
        Assert.notNull(authority, "GrantedAuthority object is null");

        Role role = findByName(roleName);
        Assert.notNull(role, String.format("Couldn't find role '%s' in DB", roleName));

        Permission authFromDb = findByCriteria(Permission.class, new CriteriaDetails() {
            public Criteria completeCriteria(Criteria criteria) {
                return criteria.add(Restrictions.eq("name", authority.getAuthority()));
            }
        });

        if (authFromDb != null && role.getPermissions().contains(authFromDb)) {
            LOG.warn(String.format("Role '%s' already has this authority.", roleName));
            return;
        }

        if (authFromDb != null) {
            role.getPermissions().add(authFromDb);
        } else {
            role.getPermissions().add(new Permission(authority.getAuthority()));
        }

        update(role);
    }

    @Override
    public void removeGroupAuthority(String roleName, final GrantedAuthority authority) {
        Assert.hasText(roleName, "role name is empty");
        Assert.notNull(authority, "GrantedAuthority object is null");

        Role role = findByName(roleName);
        Assert.notNull(role, String.format("Couldn't find role '%s' in DB", roleName));

        Permission authFromDb = findByCriteria(Permission.class, new CriteriaDetails() {
            public Criteria completeCriteria(Criteria criteria) {
                return criteria.add(Restrictions.eq("name", authority.getAuthority()));
            }
        });

        if (authFromDb != null && !role.getPermissions().contains(authFromDb)) {
            LOG.warn(String.format("Role '%s' doesn't have this authority.", roleName));
            return;
        }

        if (authFromDb != null) {
            role.getPermissions().remove(authFromDb);
        }

        update(role);

    }
}
