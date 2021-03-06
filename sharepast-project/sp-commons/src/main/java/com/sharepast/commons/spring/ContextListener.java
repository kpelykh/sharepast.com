package com.sharepast.commons.spring;

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

  public void afterStartup( ApplicationContext context ) {}

  public void beforeShutdown( ApplicationContext context ) {}

  public void afterShutdown() {};

  public void shutdown( ApplicationContext context ) {}

  public void setApplicationContext( ApplicationContext applicationContext )
  {
    this.applicationContext = applicationContext;
  }

    public void onApplicationEvent( ApplicationEvent applicationEvent )
    {
        Class<? extends ApplicationEvent> cls = applicationEvent.getClass();
        Class sourceClass = applicationEvent.getSource().getClass();

        if (!ApplicationContext.class.isAssignableFrom(sourceClass))
            return;

        ApplicationContext eventAppCtx = (ApplicationContext) applicationEvent.getSource();

        if (!eventAppCtx.equals(applicationContext))
            return;

        if( ContextStartedEvent.class.isAssignableFrom( cls ) || ContextRefreshedEvent.class.isAssignableFrom( cls ) )
            afterStartup( applicationContext );
        else if( ContextStoppedEvent.class.isAssignableFrom( cls ) )
            beforeShutdown(applicationContext);
        else if( ContextClosedEvent.class.isAssignableFrom( cls ) )
            afterShutdown();
        else if( ShutdownEvent.class.isAssignableFrom( cls ) )
            shutdown(applicationContext);
    }
}