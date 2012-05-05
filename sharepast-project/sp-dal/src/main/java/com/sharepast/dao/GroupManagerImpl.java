package com.sharepast.dao;

import com.sharepast.domain.user.Group;
import com.sharepast.domain.user.Permission;
import com.sharepast.domain.user.User;
import com.sharepast.genericdao.hibernate.CriteriaDetails;
import com.sharepast.genericdao.hibernate.GenericDAOImpl;
import com.sharepast.genericdao.hibernate.QueryDetails;
import com.sharepast.genericdao.search.Search;
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
 * Date: 1/25/12
 * Time: 2:42 PM
 * To change this template use File | Settings | File Templates.
 */

@Repository("groupManager")
@Transactional( propagation = Propagation.REQUIRED, rollbackFor = HibernateException.class)
public class GroupManagerImpl extends GenericDAOImpl<Group, Integer> implements GroupManager {

    @Autowired
    private UserDAO userDao;
    
    private static final Logger LOG = LoggerFactory.getLogger(GroupManagerImpl.class);

    @Override
    @Transactional(readOnly = true)
    public List<Permission> findAllPermissions() {
        return listByQuery(Permission.class, new QueryDetails() {
            public String getQueryString() {
                return "from Permission";
            }
        });
    }

    @Transactional(readOnly = true)
    public Group findByName(final String roleName) {
        Assert.hasText(roleName);
        Search s = new Search().addFilterEqual("name", roleName);
        return searchUnique(s);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> findAllGroups() {
        return search(new Search().addField("name"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> findUsersInGroup(final String roleName) {
        Assert.hasText(roleName);
        return listByQuery(String.class, new QueryDetails() {
            public String getQueryString() {
                return "select u.username from User u join u.groups r where r.name = :groupName";
            }
            @Override
            public Query completeQuery(Query query) {
                return query.setParameter("groupName", roleName);
            }
        });
    }

    @Override
    public void createGroup(String roleName, List<GrantedAuthority> authorities) {
        Assert.hasText(roleName);
        Assert.notNull(authorities);

        LOG.info(String.format("Creating new group %s with authorities %s",
                roleName,
                AuthorityUtils.authorityListToSet(authorities))
        );

        Group group = findByName(roleName);
        if (group == null) {
            group = new Group();
            group.setName(roleName);

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
                    Permission auth = new Permission();
                    auth.setName(authority);                    
                    authSet.add(auth);
                    LOG.warn(String.format("There's no permission '%s', creating new on cascade", authority));
                }
            }
            group.setPermissions(authSet);
            save(group);
        }
    }

    @Override
    public void deleteGroup(String roleName) {
        Assert.hasText(roleName);
        
        LOG.info(String.format("Deleting group %s", roleName));

        Group group = findByName(roleName);
        if (group == null) {
            LOG.warn(String.format("Error deleting group: couldn't find group '%s' in DB", roleName));
            return;
        }

        remove(group);
    }

    @Override
    public void renameGroup(String oldName, String newName) {
        Assert.hasText(oldName, "old name for group is empty");
        Assert.hasText(newName, "new name for group is empty");

        LOG.debug(String.format("Changing group name from %s to %s", oldName, newName));

        Group group = findByName(oldName);
        Assert.notNull(group, String.format("Error renaming group: couldn't find group '%s' in DB", oldName));

        group.setName(newName);
        save(group);
    }

    @Override
    public void addUserToGroup(String username, String roleName) {
        Assert.hasText(username, "username is empty");
        Assert.hasText(roleName, "group name is empty");

        LOG.info(String.format("Adding user %s to group %s", username, roleName));

        Group group = findByName(roleName);
        Assert.notNull(group, String.format("Error adding user to group: couldn't find group '%s' in DB", roleName));

        User user = userDao.findByUsername(username);
        Assert.notNull(group, String.format("Error adding user to group: couldn't find user '%s' in DB", username));
        
        if (user.getGroups().contains(group)) {
            LOG.warn(String.format("User %s already has group %s", username, roleName));
            return;
        }
        
       user.getGroups().add(group);

       userDao.save(user);
    }

    @Override
    public void removeUserFromGroup(String username, String roleName) {
        Assert.hasText(username, "username is empty");
        Assert.hasText(roleName, "group name is empty");

        LOG.info(String.format("Removing user %s to group %s", username, roleName));

        Group group = findByName(roleName);
        Assert.notNull(group, String.format("Removing adding user from group: couldn't find group '%s' in DB", roleName));

        User user = userDao.findByUsername(username);
        Assert.notNull(group, String.format("Error Removing user from group: couldn't find user '%s' in DB", username));

        if (!user.getGroups().contains(group)) {
            LOG.warn(String.format("User %s doesn't have group %s, nothing ot remove", username, roleName));
            return;
        }

        user.getGroups().remove(group);

        userDao.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GrantedAuthority> findGroupAuthorities(String roleName) {
        Assert.hasText(roleName, "group name is empty");

        Group group = findByName(roleName);
        Assert.notNull(group, String.format("Couldn't find group '%s' in DB", roleName));

        return new ArrayList<GrantedAuthority>(group.getPermissions());
    }

    @Override
    public void addGroupAuthority(String roleName, final GrantedAuthority authority) {
        Assert.hasText(roleName, "group name is empty");
        Assert.notNull(authority, "GrantedAuthority object is null");

        Group group = findByName(roleName);
        Assert.notNull(group, String.format("Couldn't find group '%s' in DB", roleName));

        Permission authFromDb = findByCriteria(Permission.class, new CriteriaDetails() {
            public Criteria completeCriteria(Criteria criteria) {
                return criteria.add(Restrictions.eq("name", authority.getAuthority()));
            }
        });

        if (authFromDb != null && group.getPermissions().contains(authFromDb)) {
            LOG.warn(String.format("Group '%s' already has this authority.", roleName));
            return;
        }

        if (authFromDb != null) {
            group.getPermissions().add(authFromDb);
        } else {
            group.getPermissions().add(new Permission(authority.getAuthority()));
        }

        save(group);
    }

    @Override
    public void removeGroupAuthority(String roleName, final GrantedAuthority authority) {
        Assert.hasText(roleName, "group name is empty");
        Assert.notNull(authority, "GrantedAuthority object is null");

        Group group = findByName(roleName);
        Assert.notNull(group, String.format("Couldn't find group '%s' in DB", roleName));

        Permission authFromDb = findByCriteria(Permission.class, new CriteriaDetails() {
            public Criteria completeCriteria(Criteria criteria) {
                return criteria.add(Restrictions.eq("name", authority.getAuthority()));
            }
        });
        
        if (authFromDb != null && !group.getPermissions().contains(authFromDb)) {
            LOG.warn(String.format("Group '%s' doesn't have this authority.", roleName));
            return;
        }

        if (authFromDb != null) {
            group.getPermissions().remove(authFromDb);
        }

        save(group);
                
    }
}
