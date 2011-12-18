package com.sharepast.dal.util;

import com.sharepast.dal.dao.UserDAO;
import com.sharepast.dal.domain.user.Gender;
import com.sharepast.dal.domain.user.User;
import com.sharepast.dal.domain.user.UserStatus;
import com.sharepast.dal.exceptions.BadPasswordException;
import com.sharepast.dal.security.dao.SecurityPermissionDAO;
import com.sharepast.dal.security.dao.SecurityRoleDAO;
import com.sharepast.dal.security.domain.SecurityPermission;
import com.sharepast.dal.security.domain.SecurityRole;
import com.sharepast.persistence.orm.type.UTC;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 3/23/11
 * Time: 10:04 PM
 * To change this template use File | Settings | File Templates.
 */

public class TestDataGenerator implements DataGenerator {

    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(TestDataGenerator.class);

    public static final String TEST_BUSINESS_ADMIN_ACCOUNT_USERNAME = "testme";
    public static final String TEST_BUSINESS_ADMIN_ACCOUNT_EMAIL = "test@myapp.com";
    public static final String TEST_BUSINESS_ADMIN_ACCOUNT_PASS = "testme";
    public static final String TEST_BUSINESS_ADMIN_ACCOUNT_SALT = null;
    public static final String TEST_ACCOUNT_ROLE = "USER_ROLE";

    public TestDataGenerator() {
    }

    @Autowired(required = true)
    private UserDAO userDao;

    @Autowired(required = true)
    private SecurityRoleDAO securityRoleDao;

    @Autowired
    private SecurityPermissionDAO securityPermissionDao;

    public User findOrCreateAccount(String username, String email, String pass, String salt, String... roles)
            throws BadPasswordException {
        User user = userDao.findByEmail(email);

        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setUsername(username);
            user.setFirstName("Sergey");
            user.setLastName("Abdurahmanovich");
            user.setSalt(salt);
            user.setPassword(pass);
            user.setGender(Gender.MALE);
            user.setStatus(UserStatus.ACTIVE);
            user.setDateCreated(UTC.now());
            user = userDao.persist(user);
        } else {
            user.setFirstName("Archibald");
            user.setLastName("Pesconini");

            user.setSalt(salt);
            user.setPassword(pass);

            user = userDao.persist(user);
        }

        if (roles != null) {
            for (String roleName : roles) {
                SecurityRole role = findOrCreateRole(roleName);

                role = securityRoleDao.persist(role);
                user.getRoles().add(role);
            }
            user = userDao.persist(user);
        }

        LOG.info("generated user acount #" + user.getId());

        return user;
    }


    public SecurityRole findOrCreateRole(String name, String... permissions) {
        SecurityRole role = securityRoleDao.findOrCreate(name);

        if (permissions != null)
            for (String pName : permissions) {
                SecurityPermission p = securityPermissionDao.findOrCreate(pName);
                role.getPermissions().add(p);
                securityRoleDao.persist(role);
            }

        LOG.info("generated user role " + name + " #" + role.getId());

        return role;
    }
}
