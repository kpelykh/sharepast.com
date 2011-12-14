package kp.app.util.spring;

import org.springframework.beans.BeansException;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 2/14/11
 * Time: 11:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class RuntimeBeansException extends BeansException {

  public RuntimeBeansException (String message, Object... args) {

    super(String.format(message, args));
  }

  public RuntimeBeansException (Throwable throwable, String message, Object... args) {

    super(String.format(message, args), throwable);
  }

  public RuntimeBeansException (Throwable throwable) {

    super(throwable.getMessage(), throwable);
  }
}

