package com.sharepast.security;

import com.sharepast.util.Util;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.authz.permission.WildcardPermission;
import org.apache.shiro.subject.Subject;
import org.restlet.Request;
import org.restlet.data.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 8/31/11
 * Time: 12:08 AM
 * To change this template use File | Settings | File Templates.
 */
public class UriLock {
    private static final Logger LOG = LoggerFactory.getLogger(UriLock.class);

    protected AppSecurityContextEnum ctx;

    protected AppResourceActionEnum actions[];
    private String[] roles;
    private Permission[] permissions;

    /**
     * use to determine if user should have all ("and") or at least one ("or") role
     */
    private boolean or = false;

    /**
     * we know this user from a cookie, but may not be authenticated in the current session
     */
    private boolean isUser = false;

    /**
     * this user authenticated in the current session
     */
    private boolean isAuthenticated = false;

    private boolean isRemembered  = false;



    public void setContext(AppSecurityContextEnum ctx) {
        this.ctx = ctx;
    }

    public void setRoles(String... roles) {
        this.roles = roles;
    }

    public void setPermissions(String... permissionNames) {
        if (permissionNames != null) {
            this.permissions = new Permission[permissionNames.length];
            int count = 0;

            for (String permission : permissionNames)
                this.permissions[count++] = new WildcardPermission(permission);
        }
    }

    public void setAuthenticated(boolean isAuthenticated) {
        this.isAuthenticated = isAuthenticated;
    }

    public void setRemembered(boolean remembered) {
        isRemembered = remembered;
    }

    public void applyLock(Request request) {
        Method requestMethod = request.getMethod();

        Subject subject = SecurityUtils.getSubject();

        if (subject == null)
            throw new UnauthenticatedException();

        if (isUser && Util.isEmpty(subject.getPrincipals())) {
            LOG.warn("requested lock on user, but no user found");
            throw new UnauthenticatedException();
        }

        if (isAuthenticated && !subject.isAuthenticated()) {
            LOG.warn(String.format("requested lock on authenticated, but '%s' is not authenticated", subject.getPrincipal()));
            throw new UnauthenticatedException();
        }

        if (ctx != null) {
            String ctxParamName = ctx.getParamName();

            WildcardPermission perm = AppSecurityUtil.createContextPermission(ctx.getContextName(), request.getAttributes().get(ctxParamName));

            if (actions != null) {
                for (AppResourceActionEnum action : actions)
                    if (matchAction(requestMethod, action)) {
                        if (!subject.isAuthenticated() && !subject.isRemembered()) {
                            throw new UnauthenticatedException("subject should be authenticated or remembered in order to be in a context");
                        } else {
                            subject.checkPermission(perm);
                        }
                        break;
                    }
            } else {
                subject.checkPermission(perm);
            }
        }

        if (roles != null) {
            if (isAuthenticated && !subject.isAuthenticated()) {
                LOG.warn(String.format("requested lock on role(s), but '%s' is not authenticated", subject.getPrincipal()));
                throw new UnauthenticatedException();
            }

            int roleCount = 0;
            for (String role : roles)
                if (subject.hasRole(role))
                    roleCount++;

            if ((or && roleCount == 0) || (!or && roleCount != roles.length)) {
                LOG.error(String.format("User %s doesn't have the required roles %s. OR flag is %s", "" + subject.getPrincipal(), Arrays.toString(roles), or + ""));
                throw new UnauthorizedException(String.format("User %s doesn't have the required roles", "" + subject.getPrincipal()));
            }
        }

        if (permissions != null) {
            if (isAuthenticated && !subject.isAuthenticated()) {
                LOG.error(String.format("requested lock on permission(s), but '%s' is not authenticated", subject.getPrincipal()));
                throw new UnauthenticatedException();
            }

            int permissionCount = 0;
            for (Permission permission : permissions)
                if (subject.isPermitted(permission))
                    permissionCount++;

            if ((or && permissionCount == 0) || (!or && permissionCount != permissions.length)) {
                LOG.error(String.format("User %s doesn't have the required permissions. or flag is %s, and permissions are %s", "" + subject.getPrincipal(), or + "", Arrays.toString(permissions)));
                throw new UnauthorizedException(String.format("User %s doesn't have the required permissions", "" + subject.getPrincipal()));
            }
        }

    }

    protected boolean matchAction(Method requestMethod, AppResourceActionEnum action) {
        return requestMethod.equals(action.getValue());
    }

    public void setActions(AppResourceActionEnum[] actions) {
        this.actions = actions;
    }
}
