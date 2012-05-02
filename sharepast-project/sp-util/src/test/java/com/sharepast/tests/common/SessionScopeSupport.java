package com.sharepast.tests.common;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/**
 * Created by IntelliJ IDEA.
 * User: kpelykh
 * Date: 2/16/12
 * Time: 12:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class SessionScopeSupport extends Assert {
    protected MockHttpSession session;
    protected MockHttpServletRequest request;

    protected void startSession() {
        session = new MockHttpSession();
    }

    protected void endSession() {
        if (session != null) {
            session.clearAttributes();
            session = null;
        }
    }

    @BeforeMethod
    protected void startRequest() {
        request = new MockHttpServletRequest();
        request.setSession(session);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @AfterMethod
    protected void endRequest() {
        ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).requestCompleted();
        RequestContextHolder.resetRequestAttributes();
        request = null;
    }


}
