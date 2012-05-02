package java.com.sharepast.util.lang;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 1/14/11
 * Time: 2:06 AM
 * To change this template use File | Settings | File Templates.
 */
public class FormattedRuntimeException extends RuntimeException {

  public FormattedRuntimeException () {

    super();
  }

  public FormattedRuntimeException (String message, Object... args) {

    super(String.format(message, args));
  }

  public FormattedRuntimeException (Throwable throwable, String message, Object... args) {

    super(String.format(message, args), throwable);
  }

  public FormattedRuntimeException (Throwable throwable) {

    super(throwable);
  }
}