package com.sharepast.commons.util.lang;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 1/14/11
 * Time: 2:06 AM
 * To change this template use File | Settings | File Templates.
 */
public class SystemRuntimeException extends FormattedRuntimeException {

  private static final long serialVersionUID = 6613287895165074681L;

  public SystemRuntimeException () {

    super();
  }

  public SystemRuntimeException (String message, Object... args) {

    super(message, args);
  }

  public SystemRuntimeException (Throwable throwable, String message, Object... args) {

    super(throwable, message, args);
  }

  public SystemRuntimeException (Throwable throwable) {

    super(throwable);
  }
}