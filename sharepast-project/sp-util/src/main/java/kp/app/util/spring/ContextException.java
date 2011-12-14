package kp.app.util.spring;

import kp.app.util.lang.FormattedRuntimeException;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 1/14/11
 * Time: 2:26 AM
 * To change this template use File | Settings | File Templates.
 */
public class ContextException extends FormattedRuntimeException {

  public ContextException () {

    super();
  }

  public ContextException (String message, Object... args) {

    super(message, args);
  }

  public ContextException (Throwable throwable, String message, Object... args) {

    super(throwable, message, args);
  }

  public ContextException (Throwable throwable) {

    super(throwable);
  }
}

