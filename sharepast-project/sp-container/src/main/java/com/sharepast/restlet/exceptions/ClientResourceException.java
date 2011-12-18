package com.sharepast.restlet.exceptions;

import com.sharepast.util.LogLevel;
import org.restlet.data.Status;
import org.slf4j.Logger;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 7/18/11
 * Time: 8:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClientResourceException extends FormattedResourceException {
  protected LogLevel logLevel;
  protected String logMessage;

  /**
   * This is the most generic version of this exception. It allows one to specify
   *
   * @param level      logging level and whether put the stack trace into the log or not
   * @param status     HTTP status, user for the code only
   * @param the        the cause of thie error if any
   * @param message    the message client will see together with the code
   * @param logMessage message to log (instead of the one sent to the client)
   */
  public ClientResourceException(LogLevel level, Status status, Throwable the, String message, String logMessage) {
    super(status, the, message);
    this.logLevel = level;
    this.logMessage = logMessage;
  }

  public ClientResourceException(LogLevel level, Status status, Throwable the, String message) {
    super(status, the, message);
    this.logLevel = level;
  }

  public ClientResourceException(LogLevel level, Status status, String message) {
    super(status, message);
    this.logLevel = level;
  }

  public ClientResourceException(LogLevel level, Status status) {
    super(status);
    this.logLevel = level;
  }

  public ClientResourceException(LogLevel level, Status status, Throwable the) {
    super(status, the);
    this.logLevel = level;
  }

  public ClientResourceException(LogLevel level, Status status, String message, Object... args) {
    super(status, String.format(message, args));
    this.logLevel = level;
  }

  /**
   * @deprecated please use constructor with LogLevel as first parameter instead
   */
  public ClientResourceException(Status status) {
    this(LogLevel.ERROR_WITH_TRACE, status);
  }

  /**
   * @deprecated please use constructor with LogLevel as first parameter instead
   */
  public ClientResourceException(Status status, String message, Object... args) {
    this(LogLevel.ERROR_WITH_TRACE, status, String.format(message, args));
  }

  /**
   * @deprecated please use constructor with LogLevel as first parameter instead
   */
  public ClientResourceException(Status status, Throwable throwable,
                                 String message, Object... args) {
    this(LogLevel.ERROR_WITH_TRACE, status, throwable, String.format(message, args));
  }

  /**
   * @deprecated please use constructor with LogLevel as first parameter instead
   */
  public ClientResourceException(Status status, Throwable throwable) {
    this(LogLevel.ERROR_WITH_TRACE, status, throwable);
  }

  /**
   * log this exception
   *
   * @param logger
   */
  public void log(Logger logger) {
    if (logger == null) {
      System.out.println(String.format("%s: ClientResourceException asked to log into a null logger. Exiting!!", new Date().toString()));
      new Exception().printStackTrace();
      return;
    }

    String msg = logMessage == null ? getMessage() : logMessage;

    if (logLevel == null)
      logger.error(msg);
    else
      switch (logLevel.getLevel()) {
        case ERROR:
          logger.error(msg);
          break;
        case INFO:
          logger.info(msg);
          break;
        case DEBUG:
          logger.debug(msg);
          break;
        case WARN:
          logger.warn(msg);
          break;
      }
  }
}
