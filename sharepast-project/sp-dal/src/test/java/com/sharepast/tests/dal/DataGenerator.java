package com.sharepast.tests.dal;

import com.sharepast.domain.user.User;
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
    public User findOrCreateAccount(String username, String email, String pass, String salt, String... roles);

}
