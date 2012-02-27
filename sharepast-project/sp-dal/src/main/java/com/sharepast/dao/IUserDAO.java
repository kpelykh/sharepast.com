package com.sharepast.dao;

import com.sharepast.domain.user.User;
import com.sharepast.persistence.ORMDao;

/**
 * Created by IntelliJ IDEA.
 * User: kpelykh
 * Date: 2/21/12
 * Time: 11:06 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IUserDAO extends ORMDao<User, Long> {

    public boolean isUsernameAvailable(final String username);

    public boolean isEmailAvailable(String email);

    public User findByUsername(String username);

    public User findByEmail(String email);
}
