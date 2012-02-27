package com.sharepast.service;

import com.sharepast.domain.user.User;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Created by IntelliJ IDEA.
 * User: kpelykh
 * Date: 2/21/12
 * Time: 10:58 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IUserService  extends IService<User, Long>, UserDetailsService {

    User findUserByUsername( String username );

    User createUser(User user);

}