package com.sharepast.its.common;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 3/22/11
 * Time: 12:02 AM
 * To change this template use File | Settings | File Templates.
 */

import org.restlet.Request;

/**
 * if supplied, tis implementation will be called on the Request before sending it
 *
 *
 */

public interface IRequestModifier
{
  public abstract void modify( Request resource );
}
