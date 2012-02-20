package com.sharepast.security;

import com.sharepast.util.spring.SpringConfigurator;
import com.sharepast.dal.domain.AppSecurityContextNameEnum;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.permission.WildcardPermission;
import org.apache.shiro.subject.Subject;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 8/31/11
 * Time: 12:21 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AppSecurityUtil extends SecurityUtils {


    private static volatile AppRealm realm;

    /**
     * checks if currently logged account has a role
     */
    public static boolean hasRole(String role) {
        Subject subject = getSubject();
        return subject == null ? false : subject.hasRole(role);
    }

    /**
     * helper method to create a context permission out of context name and object id
     */
    public static WildcardPermission createContextPermission(AppSecurityContextNameEnum ctxName, Object id) {
        WildcardPermission perm = new WildcardPermission(ctxName.name() + ":" + id, true);
        return perm;
    }

    public static AppRealm getRealm() {
        if (realm == null)
            synchronized (AppSecurityUtil.class) {
                if (realm == null)
                    realm = SpringConfigurator.getInstance().getBean(AppRealm.class, "appRealm");
            }

        return realm;
    }

    /**
     * helper method to add new a context permission to an account, identified by email
     */
    public static void addContextPermission(String email, AppSecurityContextNameEnum ctxName, long id) {
        getRealm().addContextPermission(email, ctxName, id);
    }

    /**
     * helper method to get all account permissions
     */
    public static Collection getAccountPermissions(String email) {
        return getRealm().getSubjectPermissions(email);
    }

    /**
     * helper method to add new a context permission to an account, identified by email
     */
    public static void removeContextPermission(String email, AppSecurityContextNameEnum ctxName, long id) {
        getRealm().removeContextPermission(email, ctxName, id);
    }

}
