package com.sharepast.dal.exceptions;

import com.sharepast.util.lang.FormattedException;

public class BadPasswordException
extends FormattedException
{

  /**
   *
   */
  public BadPasswordException()
  {
    // TODO Auto-generated constructor stub
  }

  /**
   * @param message
   * @param args
   */
  public BadPasswordException( String message, Object... args )
  {
    super( message, args );
    // TODO Auto-generated constructor stub
  }

  /**
   * @param throwable
   * @param message
   * @param args
   */
  public BadPasswordException( Throwable throwable, String message,
                               Object... args )
  {
    super( throwable, message, args );
    // TODO Auto-generated constructor stub
  }

  /**
   * @param throwable
   */
  public BadPasswordException( Throwable throwable )
  {
    super( throwable );
    // TODO Auto-generated constructor stub
  }

}
