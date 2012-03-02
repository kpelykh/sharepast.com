package com.sharepast.dao.hibernate;

import com.sharepast.dao.IUserDAO;
import com.sharepast.domain.user.User;
import com.sharepast.persistence.hibernate.CriteriaDetails;
import com.sharepast.persistence.hibernate.HibernateDao;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

@Repository("userDao")
@Transactional( propagation = Propagation.REQUIRED)
public final class UserDaoImpl extends HibernateDao<User> implements IUserDAO {

    private static final Logger LOG = LoggerFactory.getLogger(UserDaoImpl.class);

    public Class<User> getEntityClass() {
        return User.class;
    }

    public void update(final User user) {
        Assert.notNull(user, "user object must not be null");
        validateUserDetails(user);
        super.update(user);
    }

    public User findByUsername(final String username) {
        return findByCriteria(new CriteriaDetails() {
            public Criteria completeCriteria(Criteria criteria) {
                return criteria.add(Restrictions.eq("username", username));
            }
        });
    }

    public User findByEmail(final String email) {
        return findByCriteria(new CriteriaDetails() {
            public Criteria completeCriteria(Criteria criteria) {
                return criteria.add(Restrictions.eq("email", email));
            }
        });
    }

    // ------------- helpers ------------------


    public boolean isUsernameAvailable(final String username) {
        List users = getCurrentSession().createSQLQuery("SELECT u.username FROM users u WHERE u.username = :username")
                .setParameter("username", username)
                .list();
        return users.size() <= 0;
    }

    public boolean isEmailAvailable(String email) {
        List users = getCurrentSession().createSQLQuery("SELECT u.email FROM users u WHERE u.email = :email")
                .setParameter("email", email).list();
        return users.size() <= 0;
    }

    private void validateUserDetails(User user) {
        Assert.hasText(user.getUsername(), "Username may not be empty or null");
        validateAuthorities(user.getAuthorities());
    }

    private void validateAuthorities(List<? extends GrantedAuthority> authorities) {
        Assert.notNull(authorities, "Authorities list must not be null");

        for (GrantedAuthority authority : authorities) {
            Assert.notNull(authority, "Authorities list contains a null entry");
            Assert.hasText(authority.getAuthority(), "getAuthority() method must return a non-empty string");
        }
    }

}