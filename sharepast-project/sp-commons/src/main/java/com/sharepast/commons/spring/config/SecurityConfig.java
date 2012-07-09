package com.sharepast.commons.spring.config;

import com.sharepast.commons.util.security.CustomSecurityExpressionHandler;
import com.sharepast.commons.util.security.PasswordHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.authentication.RememberMeAuthenticationProvider;
import org.springframework.security.authentication.dao.SaltSource;
import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

/**
 * Created with IntelliJ IDEA.
 * User: kpelykh
 * Date: 5/24/12
 * Time: 10:36 AM
 * To change this template use File | Settings | File Templates.
 */
@Configuration
public class SecurityConfig {

    private static final String REMEMBER_ME_KEY = "5f4dcc3b5aa765d61d8327deb882cf99";

    @Bean
    public PasswordHelper passwordHelper(MessageDigestPasswordEncoder passwordEncoder, SaltSource saltSource) {
        return new PasswordHelper(passwordEncoder, saltSource);
    }

    @Bean(name = "spPermissionEvaluator")
    public PermissionEvaluator permissionEvaluator() {
        return new com.sharepast.commons.spring.security.PermissionEvaluator();
    }

    //An expression handler used to secure methods. This overrides the DefaultMethodSecurityExpressionHandler to include roleHierarchy.
    @Bean(name = "methodSecurityExpressionHandler")
    public DefaultMethodSecurityExpressionHandler defaultMethodSecurityExpressionHandler( RoleHierarchy roleHierarchy ) {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();

        //To use hasPermission() expressions, we have to configure a PermissionEvaluator See 15.3.2 Built-In Expression
        //@http://static.springsource.org/spring-security/site/docs/3.0.x/reference/el-access.html#el-permission-evaluator -->

        expressionHandler.setPermissionEvaluator( permissionEvaluator() );
        expressionHandler.setRoleHierarchy( roleHierarchy );

        return expressionHandler;

    }


    @Bean(name = "webSecurityExpressionHandler")
    public DefaultWebSecurityExpressionHandler defaultWebSecurityExpressionHandler( RoleHierarchy roleHierarchy ) {
        DefaultWebSecurityExpressionHandler expressionHandler = new DefaultWebSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator( permissionEvaluator() );
        expressionHandler.setRoleHierarchy( roleHierarchy );
        return expressionHandler;
    }

    //Used to expose SecurityExpressionRoot in Subject class
    @Bean(name = "customSecurityExpressionHandler")
    public CustomSecurityExpressionHandler securityExpressionHandler( RoleHierarchy roleHierarchy ) {
        CustomSecurityExpressionHandler expressionHandler = new CustomSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator( permissionEvaluator() );
        expressionHandler.setRoleHierarchy(roleHierarchy);
        return expressionHandler;

    }

    //******* Remember me configuration ********//

    @Bean(name = "persistentTokenRepository")
    public PersistentTokenRepository persistentTokenRepository(DataSource dataSource) {
        JdbcTokenRepositoryImpl persistentTokenRepository = new JdbcTokenRepositoryImpl();
        persistentTokenRepository.setDataSource(dataSource);
        return persistentTokenRepository;

    }

    @Bean(name ="rememberMeAuthenticationProvider")
    public RememberMeAuthenticationProvider rememberMeAuthenticationProvider() {
        return new RememberMeAuthenticationProvider(REMEMBER_ME_KEY);
    }

    @Bean(name = "rememberMeServices")
    public RememberMeServices rememberMeServices(UserDetailsService userDetailsService, PersistentTokenRepository tokenRepository) {
        PersistentTokenBasedRememberMeServices rememberMeServices = new PersistentTokenBasedRememberMeServices(REMEMBER_ME_KEY, userDetailsService, tokenRepository);
        rememberMeServices.setAlwaysRemember(true);
        rememberMeServices.setTokenValiditySeconds(86400);
        return rememberMeServices;
    }

}
