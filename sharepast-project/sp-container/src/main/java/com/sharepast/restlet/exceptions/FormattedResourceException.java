package com.sharepast.restlet.exceptions;

import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 7/18/11
 * Time: 8:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class FormattedResourceException
extends ResourceException
{
  protected boolean needStackTrace = true;

  public FormattedResourceException( Status status )
  {
    super( status );
  }

  public FormattedResourceException( Status status, String message,
                                     Object... args )
  {
    super( status, String.format( message, args ) );
  }

  public FormattedResourceException( Status status, Throwable throwable,
                                     String message, Object... args )
  {
    super( status, String.format( message, args ), throwable );
  }

  public FormattedResourceException( Status status, Throwable throwable )
  {
    super( status, throwable );
  }
}
