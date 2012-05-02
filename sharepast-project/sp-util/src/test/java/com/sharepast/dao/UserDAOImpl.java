/**
 * (C) Copyright 2010-2012, ZettaSet Inc. All rights reserved.
 * ZettaSet PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.com.sharepast.dao;

import com.sharepast.domain.user.User;
import com.sharepast.genericdao.hibernate.GenericDAOImpl;
import com.sharepast.genericdao.search.Search;
import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

@Repository("userDao")
@Transactional( propagation = Propagation.REQUIRED, rollbackFor = HibernateException.class)
public class UserDAOImpl extends GenericDAOImpl<User, Integer> implements UserDAO {

    private static final Logger LOG = LoggerFactory.getLogger(UserDAOImpl.class);

    public void update(final User user) {
        Assert.notNull(user, "user object must not be null");
        validateUserDetails(user);
        super.save(user);
    }

    public User findByUsername(final String username) {
        return searchUnique(new Search()
                .addFilterEqual("username", username)
                .setMaxResults(1));
    }

    // ------------- helpers ------------------


    public boolean isUsernameAvailable(final String username) {
        return count(new Search()
                .addField("username")
                .addFilterEqual("username", username)) <=0;
    }

    public boolean isEmailAvailable(final String email) {
        return count(new Search()
                .addField("email")
                .addFilterEqual("email", email)) <=0;
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