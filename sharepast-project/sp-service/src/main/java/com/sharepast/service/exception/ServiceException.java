package com.sharepast.service.exception;

/**
 * Created by IntelliJ IDEA.
 * User: kpelykh
 * Date: 3/27/12
 * Time: 7:08 PM
 * To change this template use File | Settings | File Templates.
 */
/**
 *  Exception when service gets an exception from underlying DAO
 *
 */
public class ServiceException extends Exception {
    private static final long serialVersionUID = -1219262335729891920L;

    public ServiceException(final String message) {
        super(message);
    }

    public ServiceException(final Throwable cause) {
        super(cause);
    }

    public ServiceException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
