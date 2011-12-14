package kp.app.dal.util;

import kp.app.dal.domain.user.User;
import kp.app.dal.exceptions.BadPasswordException;
import kp.app.dal.security.domain.SecurityRole;
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
