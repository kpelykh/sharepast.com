package com.sharepast.spring;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 1/14/11
 * Time: 2:18 AM
 * To change this template use File | Settings | File Templates.
 */
public enum StartupProperties {
      SYSTEM_PROPERTY_HTTP_PORT( "http.port" )
    , SYSTEM_PROPERTY_HTTPS_PORT( "https.port" )

    , APP_SERVER_NAME( "jettyServer" )

  , SYSTEM_PROPERTY_IS_PRODUCTION( "is.production.env", "false" )
  ;

  private String key;
  private String dflt;

  private StartupProperties( String key )
  {
    this.key = key;
  }

  private StartupProperties( String key, String dflt )
  {
    this( key );
    this.dflt = dflt;
  }

  public String getKey()
  {
    return key;
  }

  public String get()
  {
    return System.getProperty( key, dflt );
  }

  public Boolean getAsBoolean()
  {
    return Boolean.parseBoolean( get() );
  }

  public String get( String dflt )
  {
    return System.getProperty( key, dflt );
  }
}

