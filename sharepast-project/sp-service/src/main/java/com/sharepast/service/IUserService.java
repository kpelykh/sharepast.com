package com.sharepast.service;

import com.sharepast.domain.user.User;
import com.sharepast.exception.UsernameExistsException;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Created by IntelliJ IDEA.
 * User: kpelykh
 * Date: 1/24/12
 * Time: 2:21 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IUserService extends IService<User, Integer>, UserDetailsService  {

    User findUserByUsername(String username);

    User findUserByEmail(String email);

    User createUser(User user) throws UsernameExistsException;

}
