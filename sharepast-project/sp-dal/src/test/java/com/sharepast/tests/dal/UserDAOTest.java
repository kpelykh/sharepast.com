/**
 * (C) Copyright 2010-2012, ZettaSet Inc. All rights reserved.
 * ZettaSet PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sharepast.tests.dal;

import com.google.common.collect.Sets;
import com.sharepast.dao.UserDAO;
import com.sharepast.domains.user.StaticGroups;
import com.sharepast.domains.user.User;
import com.sharepast.spring.SpringConfiguration;
import com.sharepast.spring.config.BaseConfig;
import com.sharepast.spring.config.HibernateConfiguration;
import com.sharepast.tests.common.SpringContextSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: kpelykh
 * Date: 3/14/12
 * Time: 12:28 PM
 * To change this template use File | Settings | File Templates.
 */
@Transactional( propagation = Propagation.REQUIRED)
public class UserDAOTest extends SpringContextSupport {

    private static final Logger LOG = LoggerFactory.getLogger(UserDAOTest.class);
    private static final boolean IS_ENABLED = true; //to enable the test.

    private UserDAO userDao;

    @Configuration
    @Import({BaseConfig.class, HibernateConfiguration.class})
    @ComponentScan({"com.sharepast.dao"})
    static class SpringContext {}

    @Override
    public Class getConfiguration() {
        return SpringContext.class;
    }

    @BeforeClass
    public void setUp()
    {
        userDao = SpringConfiguration.getInstance().getBean(UserDAO.class);
        Assert.assertNotNull(userDao);
    }

    @AfterClass
    public void dbCleanup() {
        User user = userDao.findByUsername("testng002");
        userDao.remove(user);
    }

    @Test(enabled = IS_ENABLED)
    public void testCreateUser() {
        User user = new User();
        user.setUsername("testng002");
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setEmail("testng002@zettaset.com");
        user.setEnabled(false);
        user.setFirstName("Konstantin");
        user.setLastName("testng002");
        user.setPassword("secretpassword");
        user.getGroups().add(StaticGroups.ROLE_USER.getGroup());

        LOG.info(String.format("Persisting user %s", user.toString()));
        assertTrue(userDao.save(user));
        assertNotNull(user.getId());
        assertTrue(user.getId() >= 50);
    }


    @Test(enabled = IS_ENABLED, dependsOnMethods = "testCreateUser")
    public void testDaoMethods() {
        LOG.info("Testing UserDAO methods");
        User user = userDao.findByUsername("testng002");
        assertNotNull(user);        

        assertEquals(user.getGroups(), Sets.newHashSet(StaticGroups.ROLE_USER.getGroup()));
        assertEquals(user.getFirstName(), "Konstantin");
        assertTrue(user.isAccountNonLocked());
        assertFalse(user.isEnabled());
        assertEquals(user.getPassword(), "secretpassword");

        assertTrue(userDao.isEmailAvailable("unused@zettaset.com"));
        assertFalse(userDao.isEmailAvailable("testng002@zettaset.com"));
        
        assertTrue(userDao.isUsernameAvailable("untakenusername"));
        assertFalse(userDao.isUsernameAvailable("testng002"));
                
    }

    
    @Test(enabled = IS_ENABLED, dependsOnMethods = "testDaoMethods")
    public void testBaseDaoMethods() {
        User user = userDao.findByUsername("testng002");
        assertNotNull(user);

        List<User> userList = userDao.findAll();
        assertTrue(userList.size()>0);

        assertTrue(userDao.count() == userList.size());

        user.setFirstName("NewFirstname");
        userDao.save(user);
    }


    @Test(enabled = IS_ENABLED, dependsOnMethods = "testBaseDaoMethods")
    public void testDeleteMethods() {
        User user = userDao.findByUsername("testng002");
        assertEquals(user.getFirstName(), "NewFirstname");

        long count = userDao.count();

        userDao.remove(user);

        assertEquals(userDao.count(), count-1);

        assertTrue(userDao.isEmailAvailable("test@zettaset.com"));
        assertTrue(userDao.isUsernameAvailable("testng002"));

        assertNull(userDao.findByUsername("testng002"));
        
    }

}
