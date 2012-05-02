package java.com.sharepast.tests.dal;

import com.sharepast.dao.IUserDAO;
import com.sharepast.util.UTC;
import com.sharepast.domain.user.UserStatus;
import com.sharepast.domain.user.Gender;
import com.sharepast.domain.user.User;
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
    private IUserDAO userDao;

    public User findOrCreateAccount(String username, String email, String pass, String salt, String... roles) {
        User user = userDao.findByEmail(email);

        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setUsername(username);
            user.setFirstName("Sergey");
            user.setLastName("Abdurahmanovich");
            user.setPassword(pass);
            user.setGender(Gender.MALE);
            user.setStatus(UserStatus.ACTIVE);
            user.setDateCreated(UTC.now());
            user = userDao.persist(user);
        } else {
            user.setFirstName("Archibald");
            user.setLastName("Pesconini");
            user.setPassword(pass);
            user = userDao.persist(user);
        }

        /*if (roles != null) {
            for (String roleName : roles) {
                Group role = findOrCreateRole(roleName);

                role = securityRoleDao.persist(role);
                user.getGroups().add(role);
            }
            user = userDao.persist(user);
        }*/

        LOG.info("generated user acount #" + user.getId());

        return user;
    }


   /* public SecurityRole findOrCreateRole(String name, String... permissions) {
        SecurityRole role = securityRoleDao.findOrCreate(name);

        if (permissions != null)
            for (String pName : permissions) {
                SecurityPermission p = securityPermissionDao.findOrCreate(pName);
                role.getPermissions().add(p);
                securityRoleDao.persist(role);
            }

        LOG.info("generated user role " + name + " #" + role.getId());

        return role;
    }*/
}
