package com.sharepast.jms.consumer.email;

import com.sharepast.spring.ContextListener;
import com.sharepast.spring.RuntimeBeansException;
import org.springframework.context.ApplicationContext;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 2/14/11
 * Time: 11:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class EmainInitializationListener extends ContextListener
{
  private String webRootDir;

  public void setWebRootDir( String webRootDir )
  {
    this.webRootDir = webRootDir;
  }

  @Override
  public void afterStartup( ApplicationContext context )
  {
    //try
    //{
      if( webRootDir == null )
        throw new RuntimeBeansException( "webRootDir property is not set for EmainInitializationListener" );

      //EmailId.validateTemplates( webRootDir );
    //}
    /*catch ( IOException e )
    {
      e.printStackTrace();
      throw new RuntimeBeansException( e );
    }*/
  }

    @Override
    public void shutdown(ApplicationContext context) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

}
