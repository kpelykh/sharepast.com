package com.sharepast.commons.util.security;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
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
public class CustomSecurityExpressionHandler implements ApplicationContextAware {

    private final AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();
    private RoleHierarchy roleHierarchy;
    private BeanResolver br;
    private PermissionEvaluator permissionEvaluator = new DenyAllPermissionEvaluator();
    private final ExpressionParser expressionParser = new SpelExpressionParser();


    public final ExpressionParser getExpressionParser() {
        return expressionParser;
    }

    public SecurityExpressionRoot createSecurityExpressionRoot(Authentication authentication) {
        SecurityExpressionRoot root = new  CustomSecurityExpressionRoot(authentication);
        root.setPermissionEvaluator(permissionEvaluator);
        root.setRoleHierarchy(roleHierarchy);
        root.setTrustResolver(trustResolver);
        return root;
    }

    public final EvaluationContext createEvaluationContext(Authentication authentication) {
        SecurityExpressionRoot root = createSecurityExpressionRoot(authentication);
        StandardEvaluationContext ctx = new StandardEvaluationContext();
        ctx.setBeanResolver(br);
        ctx.setRootObject(root);
        return ctx;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        br = new BeanFactoryResolver(applicationContext);
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
