package com.sharepast.security;

import com.sharepast.util.lang.SystemRuntimeException;
import org.restlet.Request;
import org.restlet.ext.spring.SpringFinder;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 8/31/11
 * Time: 12:02 AM
 * To change this template use File | Settings | File Templates.
 */
public class AppResourceFinder extends SpringFinder {

    /**
     * security context to validate
     */
    private UriLock[] locks;


    public void setLock(UriLock... uriLocks) {
        if (uriLocks == null)
            throw new SystemRuntimeException("resourceFinder for %s cannot accept empty set of locks", getTargetClass().getClass().getName());

        this.locks = new UriLock[uriLocks.length];

        int count = 0;

        for (UriLock lock : uriLocks)
            this.locks[count++] = lock;
    }

    public void checkAuthz(Request request) {
        if (locks != null) {
            for (UriLock lock : locks)
                lock.applyLock(request);
        }

    }

}
