package com.sharepast.util.config;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 1/14/11
 * Time: 1:58 AM
 * To change this template use File | Settings | File Templates.
 */
public class Environment {

  private static final String ENVIRONMENT_SYSTEM_PROPERTY = "com.sharepast.env";

  public static boolean isTestActive = false;

  private static String current;

  static
  {
    String propertyAsSet = System.getProperty( ENVIRONMENT_SYSTEM_PROPERTY, Environment.getDefault() );

    System.setProperty( ENVIRONMENT_SYSTEM_PROPERTY, propertyAsSet );

    current = propertyAsSet;
  }

  public static String getDefault()
  {
    return "de";
  }

  public static String getCurrent()
  {
    return current;
  }
}
