package kp.app.util.spring;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 1/14/11
 * Time: 2:18 AM
 * To change this template use File | Settings | File Templates.
 */
public enum StartupProperties {
      SYSTEM_PROPERTY_HTTP_PORT( "kp.app.http.port" )
    , SYSTEM_PROPERTY_HTTPS_PORT( "kp.app.https.port" )

    , APP_SERVER_NAME( "appHttpServer" )

  , SYSTEM_PROPERTY_IS_PRODUCTION( "kp.production", "false" )
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

