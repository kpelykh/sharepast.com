package com.sharepast.util.spring;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 2/12/11
 * Time: 1:53 AM
 * To change this template use File | Settings | File Templates.
 */

import org.springframework.context.ApplicationEvent;

/**
 * event sent to all registered listeners when the system goes down
 *
 *
 */
public class ShutdownEvent
extends ApplicationEvent
{
  private static final long serialVersionUID = 2968063444052206198L;

  public ShutdownEvent( Object source )
  {
    super( source );
  }
}
