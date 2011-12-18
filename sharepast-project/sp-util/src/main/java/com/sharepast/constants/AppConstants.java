package com.sharepast.constants;

/**
 * Created by IntelliJ IDEA.
 * User: kpelykh
 * Date: 12/17/11
 * Time: 1:19 AM
 * To change this template use File | Settings | File Templates.
 */
public class AppConstants {


    public static final String REALM_NAME = "sharepast.com";

    /**
     * The 'app.override' property adds another environment file to the config properties
     * ~/.m2/environment-${app.override}.properties
     */
    public static final String SYSTEM_PROPERTY_ENVIRONMENT_OVERRIDE = "app.override";

    /**
     * system properties that defines server pool
     */
    public static final String SYSTEM_PROPERTY_POOL = "app.server.pool";

    public static final String DEFAULT_POOL = "app";


    /**
     * system properties that defines server inside pool
     */
    public static final String SYSTEM_PROPERTY_SERVER_ID = "app.server.id";


}
