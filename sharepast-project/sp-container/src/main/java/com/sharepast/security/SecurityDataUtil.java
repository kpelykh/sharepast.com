package com.sharepast.security;

import com.sharepast.dal.dao.UserDAO;
import com.sharepast.dal.domain.user.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 8/26/11
 * Time: 11:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class SecurityDataUtil {
    private static final Logger LOG = LoggerFactory.getLogger(SecurityDataUtil.class);

    @Autowired
    private UserDAO userDao;

    public User getCurrentUser() {
        Subject subject = SecurityUtils.getSubject();
        if (subject == null) {
            if (LOG.isDebugEnabled())
                LOG.debug("no current subject - no current account");
            return null;
        }
        Object email = subject.getPrincipal();
        if (email == null) {
            if (LOG.isDebugEnabled())
                LOG.debug("current subject exists, but no current principal");
            return null;
        }
        User user = userDao.findByEmail(email.toString());
        if (user == null) {
            LOG.error(String.format("current subject is %s, but no user with this email!", email.toString()));
            return null;
        }
        return user;
    }

}
