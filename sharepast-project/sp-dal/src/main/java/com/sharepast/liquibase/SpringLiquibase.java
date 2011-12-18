package com.sharepast.liquibase;

import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ResourceAccessor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.NoSuchElementException;

public class SpringLiquibase implements InitializingBean, ResourceLoaderAware
{
  private ResourceLoader resourceLoader;

  private DataSource dataSource;

  private String changeLog;

  private String contexts;

  private boolean preview = true;

  private boolean structural = true;

  private boolean execute = false;

  public void setResourceLoader( ResourceLoader resourceLoader )
  {
    this.resourceLoader = resourceLoader;
  }

  public void setDataSource( DataSource dataSource )
  {
    this.dataSource = dataSource;
  }

  public void setChangeLog( String changeLog )
  {
    this.changeLog = changeLog;
  }

  public void setContexts( String contexts )
  {
    this.contexts = contexts;
  }

  public void setExecute( boolean execute )
  {
    this.execute = execute;
  }

  @Transactional
  public void afterPropertiesSet()
    throws SQLException, LiquibaseException
  {
    if ( execute )
    {
      Liquibase liquibase;
      liquibase = new Liquibase( changeLog, new ChangeLogFileOpener(), new JdbcConnection(dataSource.getConnection()) );
      if ( preview )
      {
        liquibase.update( contexts, new PrintWriter( System.out ) );
      }
      else
      {
        liquibase.update( contexts );
      }
    }
  }

  private class ChangeLogFileOpener
  implements ResourceAccessor
  {
    public InputStream getResourceAsStream( String resource )
      throws IOException
    {
      return resourceLoader.getResource( resource ).getInputStream();
    }

    public Enumeration<URL> getResources( String resource )
      throws IOException
    {
      return new ChangleLogEnumeration( resourceLoader.getResource( resource ).getURL() );
    }

    public ClassLoader toClassLoader()
    {
      return resourceLoader.getClassLoader();
    }
  }

  private class ChangleLogEnumeration
  implements Enumeration<URL>
  {
    private URL url;

    private boolean taken = false;

    public ChangleLogEnumeration( URL url )
    {
      this.url = url;
    }

    public synchronized boolean hasMoreElements()
    {
      return taken;
    }

    public synchronized URL nextElement()
    {
      if ( taken )
      {
        throw new NoSuchElementException();
      }
      taken = true;
      return url;
    }
  }
}
