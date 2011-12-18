package com.sharepast.security;

import com.sharepast.util.spring.ContextListener;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 4/7/11
 * Time: 11:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class AppSecurityConfigurationListener extends ContextListener
{
  private static final Logger LOG = LoggerFactory.getLogger(AppSecurityConfigurationListener.class);

  private String securityManagerBean = "securityManager";

  public AppSecurityConfigurationListener()
  {
    super();
  }

  public void afterStartup( ApplicationContext context ){
      SecurityManager securityManager = context.getBean( securityManagerBean, SecurityManager.class );
      SecurityUtils.setSecurityManager( securityManager );
  }

  public void beforeShutdown( ApplicationContext context )
  {
    try
    {
      SecurityUtils.getSubject().logout();
    }
    catch ( Throwable th )
    {
      LOG.error( th.getMessage(), th );
    }
  }

  public void afterShutdown()
  {
  }
}

