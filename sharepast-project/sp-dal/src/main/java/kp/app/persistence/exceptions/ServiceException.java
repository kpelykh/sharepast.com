package kp.app.persistence.exceptions;

import org.springframework.core.NestedCheckedException;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 12/29/10
 * Time: 1:00 AM
 * To change this template use File | Settings | File Templates.
 */
public class ServiceException extends NestedCheckedException {

    public String exceptionClass;

    public ServiceException(Class exceptionClass, String msg, Throwable cause) {
        super(msg, cause);
        this.exceptionClass = exceptionClass.getName();
    }

    public ServiceException(String msg) {
        super(msg);
        this.exceptionClass = ServiceException.class.toString();
    }

    public ServiceException(String msg, Throwable cause) {
        super(msg, cause);
        this.exceptionClass = ServiceException.class.toString();
    }

    @Override
    public String toString() {
        String s = this.exceptionClass != null ? this.exceptionClass : getClass().getName();
        String message = getLocalizedMessage();
        return (message != null) ? (s + ": " + message) : s;
    }

    public String getExceptionClass() {
        return exceptionClass;
    }
}

