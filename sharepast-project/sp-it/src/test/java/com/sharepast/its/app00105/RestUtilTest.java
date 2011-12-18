package com.sharepast.its.app00105;

import com.sharepast.its.common.AbstractPlatformSupport;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 3/22/11
 * Time: 12:20 AM
 * To change this template use File | Settings | File Templates.
 */
@Test(enabled=true)
public class RestUtilTest extends AbstractPlatformSupport
{
  private String testContext = "/app00105";

  public void testContextPutGet()
  throws Exception
  {
    String protectedUri = testContext+"/restResource";

    Response response = sendRequest( Method.GET, MediaType.APPLICATION_JSON, protectedUri, null, null, null, null, null );

    assertNotNull( response );

    assertEquals( response.getStatus(), Status.SUCCESS_OK );

    String text = response.getEntity().getText();

    Assert.assertEquals(text, RestResource.value);
  }

}
