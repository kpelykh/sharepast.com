package kp.app.util;

import java.io.InputStream;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 1/14/11
 * Time: 2:11 AM
 * To change this template use File | Settings | File Templates.
 */
public class BuildComponentClasspathMetadata
{
  public InputStream getStream( String component )
  {
    return Build.class.getResourceAsStream( String.format( "/META-INF/maven/kp/%s/pom.properties", component ) );
  }
}

