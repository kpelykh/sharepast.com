/**
 * (C) Copyright 2010-2012, ZettaSet Inc. All rights reserved.
 * ZettaSet PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.com.sharepast.spring.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.provisioning.GroupManager;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: kpelykh
 * Date: 3/6/12
 * Time: 2:13 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * A custom PermissionEvaluator implementation that uses a Map to
 * check whether a domain Object and access level exists for a particular user.
 * This also uses RoleHiearchy to retrieve the highest role possible for the user.
 */
public class PermissionEvaluator implements org.springframework.security.access.PermissionEvaluator {

    private static final Logger LOG = LoggerFactory.getLogger(PermissionEvaluator.class);

    @Resource(name="roleHierarchy")
    private RoleHierarchy roleHierarchy;

    @Autowired GroupManager groupManager;

    /**`
     * Evaluates whether the user has permission by delegating to
     * hasPermission(String role, Object permission, Object domain)
     */
    public boolean hasPermission(Authentication authentication,
                                 Object targetDomainObject, Object permission) {

        String role = getGroup(authentication);

        LOG.debug("****************");
        LOG.debug("role: " + role);
        LOG.debug("targetDomainObject: " + targetDomainObject);
        LOG.debug("permission: " + permission);
        LOG.debug("****************");

        // Delegate to another hasPermission signature
        return hasPermission(role, permission, targetDomainObject);
    }

    /**
     * Another hasPermission signature. We will not implement this.
     */
    public boolean hasPermission(Authentication authentication,
                                 Serializable targetId, String targetType, Object permission) {
        LOG.debug("Evaluating expression using hasPermission signature #2");

        return false;
    }

    /**
     * Retrieves the user's highest role
     */
    private String getGroup(Authentication authentication) {
        String highestRole = null;

        try {
            Collection<? extends GrantedAuthority> auths = roleHierarchy.getReachableGrantedAuthorities(authentication.getAuthorities());

            for (GrantedAuthority auth: auths) {
                highestRole = auth.getAuthority();
                break;
            }
            LOG.debug("Highest role hiearchy: " + roleHierarchy.getReachableGrantedAuthorities(authentication.getAuthorities()));

        } catch (Exception e) {
            LOG.debug("No authorities assigned");
        }

        return highestRole;
    }

    /**
     * Evaluates whether the user has permission
     */
    private Boolean hasPermission(String roleName, Object requiredPerm, Object domain) {
        //LOG.debug("Check if role exists: " + roleName);

        //Role role = groupMngr.findByName(roleName);
        //if (role == null) {
        //    throw new IllegalStateException(String.format("Role %s doesn't exist", roleName));
        //}

        //Set<Permission> permissions = role.getPermissions();

        List<GrantedAuthority> permissions = groupManager.findGroupAuthorities(roleName);

        //LOG.debug("Check if permission exists: " + requiredPerm);

        for (GrantedAuthority rolePermission : permissions) {
                if (rolePermission.equals(requiredPerm)) {
                    //LOG.debug("Permission exists: " + requiredPerm);
                    LOG.debug("Permission Granted!");
                    return true;
                }
            }


        // By default, do not give permission
        LOG.debug("Permission Denied!");
        return false;
    }
}
