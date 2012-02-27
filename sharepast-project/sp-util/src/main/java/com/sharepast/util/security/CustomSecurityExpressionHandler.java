package com.sharepast.util.security;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.DenyAllPermissionEvaluator;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;

/**
 * Created by IntelliJ IDEA.
 * User: kpelykh
 * Date: 2/21/12
 * Time: 11:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class CustomSecurityExpressionHandler {

    private final AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();
    private RoleHierarchy roleHierarchy;
    private PermissionEvaluator permissionEvaluator = new DenyAllPermissionEvaluator();

    public SecurityExpressionRoot createSecurityExpressionRoot(Authentication authentication) {
        SecurityExpressionRoot root = new  CustomSecurityExpressionRoot(authentication);
        root.setPermissionEvaluator(permissionEvaluator);
        root.setRoleHierarchy(roleHierarchy);
        root.setTrustResolver(trustResolver);
        return root;
    }

    class CustomSecurityExpressionRoot extends SecurityExpressionRoot {
        public CustomSecurityExpressionRoot(Authentication a) {
            super(a);
        }
    }

    public void setRoleHierarchy(RoleHierarchy roleHierarchy) {
        this.roleHierarchy = roleHierarchy;
    }

    public void setPermissionEvaluator(PermissionEvaluator permissionEvaluator) {
        this.permissionEvaluator = permissionEvaluator;
    }
}
