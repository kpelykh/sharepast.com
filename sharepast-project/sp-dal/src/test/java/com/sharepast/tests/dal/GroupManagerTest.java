package com.sharepast.tests.dal;

import com.sharepast.commons.spring.SpringConfiguration;
import com.sharepast.commons.spring.config.BaseConfig;
import com.sharepast.dao.GroupManager;
import com.sharepast.dao.UserDAO;
import com.sharepast.domain.user.Group;
import com.sharepast.domain.user.Permission;
import com.sharepast.domain.user.StaticGroups;
import com.sharepast.domain.user.User;
import com.sharepast.spring.config.HibernateConfiguration;
import com.sharepast.tests.common.SpringContextSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: kpelykh
 * Date: 3/28/12
 * Time: 12:11 PM
 * To change this template use File | Settings | File Templates.
 */
@Transactional(propagation = Propagation.REQUIRED, noRollbackFor = {IllegalArgumentException.class})
public class GroupManagerTest extends SpringContextSupport {

    private static final Logger LOG = LoggerFactory.getLogger(UserDAOTest.class);
    private static final boolean IS_ENABLED = true; //to enable the test.

    private GroupManager groupManager;
    private UserDAO userDao;

    @Configuration
    @Import({BaseConfig.class, HibernateConfiguration.class})
    @ComponentScan({"com.sharepast.dao"})
    static class SpringContext {
    }

    @Override
    public Class getConfiguration() {
        return SpringContext.class;
    }

    @BeforeClass
    public void setUp() {
        groupManager = SpringConfiguration.getInstance().getBean(GroupManager.class);
        userDao = SpringConfiguration.getInstance().getBean(UserDAO.class);
        Assert.assertNotNull(groupManager);
        Assert.assertNotNull(userDao);
    }
    
    @AfterClass
    public void dbCleanup() {
        User user = userDao.findByUsername("testng001");
        userDao.remove(user);
    }

    @Test(enabled = IS_ENABLED)
    public void createUserAndAssignGroup() {
        User user = new User();
        user.setUsername("testng001");
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setEmail("testng001@sharepast.com");
        user.setEnabled(false);
        user.setFirstName("testng001");
        user.setLastName("testng001");
        user.setPassword("secretpassword");
        user.getGroups().add(StaticGroups.ROLE_USER.getGroup());

        LOG.info(String.format("Persisting user %s", user.toString()));
        assertTrue(userDao.save(user));
        assertNotNull(user.getId());
        assertTrue(user.getId() >= 50);
        groupManager.flush();
    }


    @Test(enabled = IS_ENABLED, dependsOnMethods = "createUserAndAssignGroup")
    public void testGroupManager() {
        List<Permission> authorityList = groupManager.findAllPermissions();
        assertTrue(authorityList.size() > 0);

        List<String> users = groupManager.findUsersInGroup(StaticGroups.ROLE_USER.getGroup().getName());
        assertTrue(users.size() > 0);

        boolean founduser = false;
        for (String userName : users) {
            if (userName.equals("testng001")) {
                founduser = true;
                break;
            }
        }
        assertTrue(founduser);

        Set<Permission> userAuthorities = StaticGroups.ROLE_USER.getGroup().getPermissions();
        for (Permission perm : userAuthorities) {
            assertTrue(perm.getGroups().size()>0);
        }

        Group adminGroup = groupManager.findByName(StaticGroups.ROLE_ADMIN.getGroup().getName());
        assertNotNull(adminGroup);
        assertTrue(adminGroup.getUsers().size()>0);

        List<String> groups = groupManager.findAllGroups();
        assertTrue(groups.size() > 0);

        List<GrantedAuthority> adminPermissions = groupManager.findGroupAuthorities(StaticGroups.ROLE_ADMIN.getGroup().getName());
        assertEquals(adminPermissions, adminGroup.getPermissions());

        assertEquals(groupManager.findGroupAuthorities(StaticGroups.ROLE_USER.getGroup().getName()), userAuthorities);
        groupManager.flush();

    }

    @Test(enabled = IS_ENABLED, dependsOnMethods = "testGroupManager")
    private void createGroupWithNonexistingAuthorities() {
        Permission newAuthority = new Permission("PERM_TEST_NEW");
        Set<Permission> authorities = StaticGroups.ROLE_ADMIN.getGroup().getPermissions();
        authorities.add(newAuthority);

        groupManager.createGroup("NEW_GROUP", new ArrayList<GrantedAuthority>(authorities));
        
        groupManager.addUserToGroup("testng001", "NEW_GROUP");
        Group newGroup = groupManager.findByName("NEW_GROUP");

        User user = userDao.findByUsername("testng001");
        assertTrue(user.getGroups().contains(newGroup));
        
    }


    @Test(enabled = IS_ENABLED, dependsOnMethods = "createGroupWithNonexistingAuthorities")
    public void testCreateGroup() {
        Set<Permission> authorities = StaticGroups.ROLE_ADMIN.getGroup().getPermissions();

        Group newGroup = groupManager.findByName("NEW_GROUP");

        Set<GrantedAuthority> set1 = new HashSet<GrantedAuthority>();
        set1.addAll(newGroup.getPermissions());
        Set<GrantedAuthority> set2 = new HashSet<GrantedAuthority>();
        set2.addAll(authorities);
        assertEquals(set1, set2);

        //new group will contain only one permission PERM_HDFS_READ
        for (GrantedAuthority perm : authorities) {
            if (!perm.getAuthority().equals("PERM_HDFS_READ")) {
                groupManager.removeGroupAuthority("NEW_GROUP", perm);
            }
        }

        groupManager.removeUserFromGroup("testng001", StaticGroups.ROLE_USER.getGroup().getName());

        List<String> users = groupManager.findUsersInGroup(StaticGroups.ROLE_USER.getGroup().getName());
        for (String user : users) {
            if (user.equals("testng001"))
                fail(String.format("The user %s should not be in group %s", user, StaticGroups.ROLE_USER.getGroup().getName()));
        }


        groupManager.addUserToGroup("testng001", "NEW_GROUP");
        User user = userDao.findByUsername("testng001");
        assertEquals(user.getAuthorities().size(), 1);
        assertEquals(user.getAuthorities().get(0).getAuthority(), "NEW_GROUP");
        groupManager.flush();
    }

    @Test(enabled = IS_ENABLED, dependsOnMethods = "testCreateGroup")
    public void testRenameGroup() {

        groupManager.renameGroup("NEW_GROUP", "NEWNAME_GROUP");

        assertNull(groupManager.findByName("NEW_GROUP"));
        assertNotNull(groupManager.findByName("NEWNAME_GROUP"));

        User user = userDao.findByUsername("testng001");
        assertEquals(user.getGroups().toArray(new Group[0])[0].getName(), "NEWNAME_GROUP");
        groupManager.flush();
    }

    @Test(enabled = IS_ENABLED, dependsOnMethods = "testRenameGroup")
    public void testRemoveGroup() {

        User user = userDao.findByUsername("testng001");
        assertEquals(user.getGroups().toArray(new Group[0])[0].getName(), "NEWNAME_GROUP");

        groupManager.deleteGroup("NEWNAME_GROUP");
        groupManager.flush();
        Group group = groupManager.findByName("NEWNAME_GROUP");
        assertNull(group);
    }
}


