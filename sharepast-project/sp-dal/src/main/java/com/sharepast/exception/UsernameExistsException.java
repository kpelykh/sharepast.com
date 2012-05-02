package com.sharepast.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * Created by IntelliJ IDEA.
 * User: kpelykh
 * Date: 1/25/12
 * Time: 1:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class UsernameExistsException extends AuthenticationException {

    public UsernameExistsException(String msg) {
        super(msg);
    }

    public UsernameExistsException(String msg, Object extraInformation) {
        super(msg);
    }
}

