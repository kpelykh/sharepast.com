package com.sharepast.dal.util;

import com.sharepast.dal.domain.user.User;
import com.sharepast.dal.exceptions.BadPasswordException;
import com.sharepast.dal.security.domain.SecurityRole;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 3/24/11
 * Time: 1:47 AM
 * To change this template use File | Settings | File Templates.
 */
public interface DataGenerator {

    @Transactional
    public User findOrCreateAccount(String username, String email, String pass, String salt, String... roles)
            throws BadPasswordException;

    @Transactional
    public SecurityRole findOrCreateRole(String name, String... permissions);

}
