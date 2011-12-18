package com.sharepast.its.app00205;

import com.sharepast.constants.LogonConstants;
import com.sharepast.dal.util.TestDataGenerator;
import com.sharepast.its.common.AbstractPlatformSupport;
import com.sharepast.its.common.IRequestModifier;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Created by IntelliJ IDEA.
 * User: kpelykh
 * Date: 8/15/11
 * Time: 11:38 AM
 * To change this template use File | Settings | File Templates.
 */
public class LogonTest extends AbstractPlatformSupport {
    private String testContext = "/app00205";

    @BeforeClass
    public void checkData() {
        assertNotNull(testUser);
    }

    @Test(enabled = true)
    public void testGuardRedirect() throws Exception {
        String protectedUri = testContext+"/protectedRedirectResource";

        Response response = sendRequest( Method.GET, MediaType.APPLICATION_JSON, protectedUri, null, null, null, null, null );

        assertNotNull( response );

        // should fail because resource is protected by a guard and name/pass are null
        assertEquals(response.getStatus(), Status.CLIENT_ERROR_UNAUTHORIZED);
    }

  @Test(enabled=true)
  public void testLogon()
  throws Exception
  {
    String logonUri = testContext+"/login";
    String logoutUri = testContext+"/logout";
    String protectedUri = testContext+"/protectedRedirectResource";

    Form form = new Form();
    form.set( LogonConstants.LOGON_USER_NAME, TestDataGenerator.TEST_BUSINESS_ADMIN_ACCOUNT_EMAIL, false );

    /**
     * check if protected resource can be accessed with correct name and no password
     */
    Response response = sendRequestWithLogon( Method.POST, MediaType.APPLICATION_JSON
                                     , logonUri, null, null
                                     , form.getWebRepresentation()
                                     , null
                                     , null
                                     );
    assertNotNull( response );

    assertEquals( response.getStatus(), Status.CLIENT_ERROR_UNAUTHORIZED );

    cs = response.getCookieSettings();

    assertNotNull( response.getEntity() );

    form.set( LogonConstants.LOGON_USER_NAME, TestDataGenerator.TEST_BUSINESS_ADMIN_ACCOUNT_EMAIL, false );
    form.set( LogonConstants.LOGON_PASS_NAME , TestDataGenerator.TEST_BUSINESS_ADMIN_ACCOUNT_PASS, false );
    form.set( LogonConstants.LOGON_TARGET_URI_NAME, protectedUri, false );
    form.set( LogonConstants.LOGON_TARGET_QUERY_NAME, "param1=value", false );

    /**
     * check if login works
     * check that redirection works
     */
    final Response logonResponse = sendRequestWithLogon( Method.POST, MediaType.APPLICATION_JSON
                                     , logonUri, null, null
                                     , form.getWebRepresentation()
                                     , null
                                     , null
                                     );
    assertNotNull( logonResponse );

    assertEquals(logonResponse.getStatus(), Status.REDIRECTION_SEE_OTHER);

    /**
     * check if protected resource can be accessed with cookies received by login request
     * check that rememberMe cookies work
     */
    final Response protectedResourceResponse = sendRequest(Method.GET, MediaType.APPLICATION_JSON
            , protectedUri, null, null
            , null
            , null
            , new IRequestModifier() {
                public void modify(Request request) {
                    request.getCookies().addAll(logonResponse.getCookieSettings());
                }
            }
    );
    assertNotNull( protectedResourceResponse );

    assertNotNull( protectedResourceResponse.getEntity() );

    String text = protectedResourceResponse.getEntity().getText();

    Assert.assertEquals(text, ProtectedRestResource.value);

    assertEquals( protectedResourceResponse.getStatus(), Status.SUCCESS_OK );

    /**
     * logout user
     */
    final Response logoutResourceResponse = sendRequest(Method.GET, MediaType.APPLICATION_JSON
            , logoutUri, null, null
            , null
            , null
            , new IRequestModifier() {
                public void modify(Request request) {
                    request.getCookies().addAll(protectedResourceResponse.getCookieSettings());
                }
            }
    );

    assertEquals( logoutResourceResponse.getStatus(), Status.REDIRECTION_SEE_OTHER );

    /**
     * check if protected resource can be accessed after logout request
     */
    final Response protectedResourceResponseUnauthenticated = sendRequest(Method.GET, MediaType.APPLICATION_JSON
            , protectedUri, null, null
            , null
            , null
            , new IRequestModifier() {
                public void modify(Request request) {
                    request.getCookies().addAll(logoutResourceResponse.getCookieSettings());
                }
            }
    );
    assertNotNull( protectedResourceResponseUnauthenticated );

    assertEquals( protectedResourceResponseUnauthenticated.getStatus(), Status.CLIENT_ERROR_UNAUTHORIZED );
  }


}

