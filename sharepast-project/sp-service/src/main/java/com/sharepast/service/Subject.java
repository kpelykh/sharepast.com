package com.sharepast.service;

import com.sharepast.domain.user.StaticGroups;
import com.sharepast.domain.user.User;
import com.sharepast.spring.SpringConfiguration;
import com.sharepast.util.security.CustomSecurityExpressionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: kpelykh
 * Date: 2/22/12
 * Time: 12:33 AM
 * To change this template use File | Settings | File Templates.
 */
public class Subject {

    private static final Logger LOG = LoggerFactory.getLogger(Subject.class);

    private static CustomSecurityExpressionHandler customSecurityExpressionHandler;

    static {
        customSecurityExpressionHandler = SpringConfiguration.getInstance().getBean(CustomSecurityExpressionHandler.class);
        Assert.notNull(customSecurityExpressionHandler);
    }


    public static User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            if (LOG.isDebugEnabled())
                LOG.debug("no current Authentication - no current account");
            return null;
        }
        Object principal = auth.getPrincipal();
        if (principal == null) {
            if (LOG.isDebugEnabled())
                LOG.debug("current Authentication exists, but no current principal");
            return null;
        }

        if (principal instanceof org.springframework.security.core.userdetails.User) {
            IUserService userDao = SpringConfiguration.getInstance().getBean(IUserService.class);
            User user = userDao.findUserByUsername(((org.springframework.security.core.userdetails.User) principal).getUsername());
            if (user == null) {
                LOG.error(String.format("current subject is %s, but no user with this username!", principal.toString()));
                return null;
            }
            return user;
        } else {
            //Anonymous user
            return null;
        }

    }

    public static boolean hasAdminRole() {
        return hasRole(StaticGroups.ROLE_ADMIN);
    }

    public static boolean isRememberMe() {
        return getSecurityExpressionRoot().isRememberMe();
    }

    //Returns true when user is not anonymous and authenticated with anything except RememberMe service
    public static boolean isFullyAuthenticated() {
        return getSecurityExpressionRoot().isFullyAuthenticated();
    }

    public static boolean isAnonymous() {
        return getSecurityExpressionRoot().isAnonymous();
    }

    //Returns true when authenticated user is not anonymous. Will return true if user was authenticated with RememberMe
    public static boolean isAuthenticated() {
        return getSecurityExpressionRoot().isAuthenticated();
    }

    public static boolean hasRole(StaticGroups role) {
        return getSecurityExpressionRoot().hasRole(role.name());
    }

    public static boolean hasPermission(Object obj, String permission) {
        return getSecurityExpressionRoot().hasPermission(obj, permission);
    }

    public static boolean hasPermission(String permission) {
        return getSecurityExpressionRoot().hasPermission(null, permission);
    }

    public static boolean hasAnyRole(StaticGroups... roles) {
        List<String> tmp = new ArrayList<String>();
        for (StaticGroups role : roles) {
            tmp.add(role.name());
        }

        return getSecurityExpressionRoot().hasAnyRole(tmp.toArray(new String[0]));
    }

    private static SecurityExpressionRoot getSecurityExpressionRoot() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Assert.notNull("no current Authentication - no current account");

        return  customSecurityExpressionHandler.createSecurityExpressionRoot(auth);

    }
}
