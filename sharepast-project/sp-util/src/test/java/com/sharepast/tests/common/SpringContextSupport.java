package com.sharepast.tests.common;

import com.sharepast.spring.SpringConfiguration;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;


/**
 * Created by IntelliJ IDEA.
 * User: kpelykh
 * Date: 2/15/12
 * Time: 2:23 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class SpringContextSupport extends SessionScopeSupport {

    public abstract Class getConfiguration();

    @BeforeSuite
    public void beforeSuite() throws Exception {
        System.setProperty(SpringConfiguration.ENVIRONMENT_SYSTEM_PROPERTY, "test");
        SpringConfiguration.getInstance().configure(getConfiguration());
        Assert.assertTrue(SpringConfiguration.getInstance().isContextInitialized());
        startSession();
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        endSession();
        SpringConfiguration.getInstance().shutdown();
    }


}
