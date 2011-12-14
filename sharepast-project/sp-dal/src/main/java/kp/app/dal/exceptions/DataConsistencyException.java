package kp.app.dal.exceptions;

import kp.app.util.lang.FormattedRuntimeException;

public class DataConsistencyException extends FormattedRuntimeException {

  public DataConsistencyException () {

    super();
  }

  public DataConsistencyException (String message, Object... args) {

    super(message, args);
  }

  public DataConsistencyException (Throwable throwable, String message, Object... args) {

    super(throwable, message, args);
  }

  public DataConsistencyException (Throwable throwable) {

    super(throwable);
  }
}
