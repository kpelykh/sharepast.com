package com.sharepast.security;

import com.sharepast.domain.user.StaticRoles;
import com.sharepast.domain.user.User;
import com.sharepast.service.IUserService;
import com.sharepast.util.security.CustomSecurityExpressionHandler;
import com.sharepast.util.spring.SpringConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 * User: kpelykh
 * Date: 2/22/12
 * Time: 12:33 AM
 * To change this template use File | Settings | File Templates.
 */
public class Subject {
    /*if (throwable != null) {
        if (throwable instanceof UnauthorizedException) {
            errors.add(messageSource.getMessage("unauthorized.exception", null, Locale.getDefault()));
        }
        if (throwable instanceof IncorrectCredentialsException) {
            errors.add(messageSource.getMessage("incorrect.credentials.exception", null, Locale.getDefault()));
        } else if (throwable instanceof UnauthenticatedException) {
            errors.add(messageSource.getMessage("unauthenticated.exception", null, Locale.getDefault()));
        } else if (throwable instanceof UnknownAccountException) {
            errors.add(messageSource.getMessage("unknown.account.exception", null, Locale.getDefault()));
        } else if (throwable instanceof CredentialsException) {
            errors.add(messageSource.getMessage("credentials.exception", null, Locale.getDefault()));
        } else if (throwable instanceof LockedAccountException) {
            errors.add(messageSource.getMessage("locked.account.exception", null, Locale.getDefault()));
        } else if (throwable instanceof ExcessiveAttemptsException) {
            errors.add(messageSource.getMessage("excessive.attempts.exception", null, Locale.getDefault()));
        } else if (throwable instanceof AuthenticationException) {
            errors.add(messageSource.getMessage("authentication.exception", null, Locale.getDefault()));
        } else {
            errors.add(messageSource.getMessage("general.exception", null, Locale.getDefault()));
        }
    }
    */
    private static final Logger LOG = LoggerFactory.getLogger(Subject.class);

    private static CustomSecurityExpressionHandler customSecurityExpressionHandler;

    static {
        customSecurityExpressionHandler = SpringConfigurator.getInstance().getBean(CustomSecurityExpressionHandler.class, "customSecurityExpressionHandler");
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
            IUserService userDao = SpringConfigurator.getInstance().getBean(IUserService.class);
            User user = userDao.findUserByUsername(((org.springframework.security.core.userdetails.User)principal).getUsername());
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
        return hasRole(StaticRoles.ROLE_ADMIN);
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

    public static boolean hasRole(StaticRoles role) {
        return getSecurityExpressionRoot().hasRole(role.name());
    }

    public static boolean hasAnyRole(StaticRoles... roles) {
        List<String> tmp = new ArrayList<String>();
        for (StaticRoles role : roles) {
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
