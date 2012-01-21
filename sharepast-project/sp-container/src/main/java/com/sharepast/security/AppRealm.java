package com.sharepast.security;

import com.sharepast.constants.AppConstants;
import com.sharepast.dal.dao.UserDAO;
import com.sharepast.dal.domain.AppSecurityContextNameEnum;
import com.sharepast.dal.domain.user.User;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.realm.SimpleAccountRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The Spring/Hibernate sample application's one and only configured Apache Shiro Realm.
 * <p/>
 * <p>Because a Realm is really just a security-specific DAO, we could have just made Hibernate calls directly
 * in the implementation and named it a 'HibernateRealm' or something similar.</p>
 * <p/>
 * <p>But we've decided to make the calls to the database using a UserDAO, since a DAO would be used in other areas
 * of a 'real' application in addition to here. We felt it better to use that same DAO to show code re-use.</p>
 */

public class AppRealm extends SimpleAccountRealm {

    protected UserDAO userDAO = null;

    public AppRealm() {
        setName( AppConstants.REALM_NAME ); //This name must match the name in the User class's getPrincipals() method
    }

    @Autowired
    public void setUserDAO(UserDAO userDao) {
        this.userDAO = userDao;
    }

    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
        User user = userDAO.findByEmail(token.getUsername());
        if (user != null) {
            return new SimpleAuthenticationInfo(user.getEmail(), user.getPassword(), getName());
        } else {
            return null;
        }
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String userName = (String) principals.fromRealm(getName()).iterator().next();
        AppAvatar account;

        User user = userDAO.findByEmail(userName);
        if (user != null) {
            account = new AppAvatar( user );
        } else {
            return null;
        }
        add(account);
        return account;

    }

    public void addContextPermission(String email, AppSecurityContextNameEnum ctxName, long id) {
        AuthorizationInfo info = getAuthorizationCache().get(email);
        if (info != null) {
            info.getObjectPermissions().add(AppSecurityUtil.createContextPermission(ctxName, id));
        }
    }

    public synchronized Collection getSubjectPermissions(String email) {
        AuthorizationInfo info = getAuthorizationCache().get(email);
        if (info != null) {
            List<Permission> pms = new ArrayList<Permission>(64);
            Collection pm = info.getObjectPermissions();
            if (pm != null)
                pms.addAll(pm);
            pm = info.getStringPermissions();
            if (pm != null)
                pms.addAll(pm);
            return pms;
        }
        return null;
    }

    public void removeContextPermission(String email, AppSecurityContextNameEnum ctxName, long id) {
        AuthorizationInfo info = getAuthorizationCache().get( email );
        if ( info != null )
            info.getObjectPermissions().remove( AppSecurityUtil.createContextPermission( ctxName, id ) );
    }
}