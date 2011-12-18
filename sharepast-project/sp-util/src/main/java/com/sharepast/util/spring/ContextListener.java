package com.sharepast.util.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;

public abstract class ContextListener
implements ApplicationContextAware, ApplicationListener
{
  private ApplicationContext applicationContext;

  public abstract void afterStartup( ApplicationContext context );

  public abstract void beforeShutdown( ApplicationContext context );

  public abstract void afterShutdown();

  public void shutdown( ApplicationContext context )
  {
  }

  public void setApplicationContext( ApplicationContext applicationContext )
  {
    this.applicationContext = applicationContext;
  }

  public void onApplicationEvent( ApplicationEvent applicationEvent )
  {
    Class<? extends ApplicationEvent> cls = applicationEvent.getClass();

    if( ContextStartedEvent.class.isAssignableFrom( cls ) || ContextRefreshedEvent.class.isAssignableFrom( cls ) )
      afterStartup( applicationContext );
    else if( ContextStoppedEvent.class.isAssignableFrom( cls ) )
      beforeShutdown( applicationContext );
    else if( ContextClosedEvent.class.isAssignableFrom( cls ) )
      afterShutdown();
    else if( ShutdownEvent.class.isAssignableFrom( cls ) )
      shutdown( applicationContext );
  }
}