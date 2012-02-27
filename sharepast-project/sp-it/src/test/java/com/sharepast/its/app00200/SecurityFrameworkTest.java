package com.sharepast.its.app00200;

import com.sharepast.constants.LogonConstants;
import com.sharepast.its.common.AbstractPlatformSupport;
import com.sharepast.its.common.IRequestModifier;
import org.restlet.data.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 3/23/11
 * Time: 10:01 PM
 * To change this template use File | Settings | File Templates.
 */
@Test(sequential = true)
public class SecurityFrameworkTest extends AbstractPlatformSupport {

    private final boolean IS_ENABLED = false;

    private String testContext = "/app00200";

    @BeforeClass
    public void checkData() {
        assertNotNull(testUser);
    }

    /**
     * check if resource can be protected accessed without auth
     * check if resource @RequiresAuthentication works
     */
    @Test(enabled = IS_ENABLED)
    public void testNegativeAuthenticationViaShiroFilter()
            throws Exception {
        String protectedUri = testContext + "/protectedResource";

        Response response = sendRequest(Method.GET, MediaType.APPLICATION_JSON, protectedUri, null, null, null, null
                , null);

        assertNotNull(response);

        // should fail because resource is protected by a guard and name/pass are null
        assertEquals(response.getStatus(), Status.CLIENT_ERROR_UNAUTHORIZED);
    }

    /**
     * check if resource can be protected accessed with correct name and bad password
     * check if resource @RequiresAuthentication works
     */
    @Test(enabled = IS_ENABLED)
    public void testNegativeAuthenticationViaShiroFilterBadPass()
            throws Exception {
        String protectedUri = testContext + "/protectedResource";

        Response response = sendRequest(Method.GET, MediaType.APPLICATION_JSON, protectedUri, null, null
                , TestDataGenerator.TEST_BUSINESS_ADMIN_ACCOUNT_EMAIL, TestDataGenerator.TEST_BUSINESS_ADMIN_ACCOUNT_PASS + "m"
                , null
        );

        assertNotNull(response);

        // should fail because resource is protected by a guard and name/pass are bad
        assertEquals(response.getStatus(), Status.CLIENT_ERROR_UNAUTHORIZED);
    }

    /**
     * check if resource can be protected accessed with bad name and correct password
     * check if resource @RequiresAuthentication works
     */
    @Test(enabled = IS_ENABLED)
    public void testNegativeAuthenticationViaShiroFilterBadName()
            throws Exception {
        String protectedUri = testContext + "/protectedResource";

        Response response = sendRequest(Method.GET, MediaType.APPLICATION_JSON, protectedUri, null, null
                , TestDataGenerator.TEST_BUSINESS_ADMIN_ACCOUNT_EMAIL + "n", TestDataGenerator.TEST_BUSINESS_ADMIN_ACCOUNT_PASS
                , null
        );

        assertNotNull(response);

        // should fail because resource is protected by a guard and name/pass are bad
        assertEquals(response.getStatus(), Status.CLIENT_ERROR_UNAUTHORIZED);
    }

    /**
     * check if resource can be accessed with correct auth
     * check if resource @RequiresAuthentication works
     */
    @Test(enabled = IS_ENABLED)
    public void testAuthenticationViaShiroFilter()
            throws Exception {
        String protectedUri = testContext + "/protectedResource";

        Response response = sendRequest(Method.GET, MediaType.APPLICATION_JSON, protectedUri, null, null
                , TestDataGenerator.TEST_BUSINESS_ADMIN_ACCOUNT_EMAIL, TestDataGenerator.TEST_BUSINESS_ADMIN_ACCOUNT_PASS
                , null
        );

        assertNotNull(response);

        // should pass because resource is protected by a guard and name/pass are OK
        assertEquals(response.getStatus(), Status.SUCCESS_OK);

        assertEquals(response.getEntity().getText(), ProtectedResource.myMessage);
    }

    /**
     * check if resource @RequiresRoles works - current subject does not have the role
     */
    @Test(enabled = IS_ENABLED)
    public void testNegativeAuthenticationViaAnnotation()
            throws Exception {
        String protectedUri = testContext + "/protectedAnnotatedRoleResource";

        Response response = sendRequest(Method.GET, MediaType.APPLICATION_JSON, protectedUri, null, null
                , TestDataGenerator.TEST_BUSINESS_ADMIN_ACCOUNT_EMAIL, TestDataGenerator.TEST_BUSINESS_ADMIN_ACCOUNT_PASS
                , null
        );

        assertNotNull(response);

        // should fail because annotated resource requires a non-existent role
        assertEquals(response.getStatus(), Status.CLIENT_ERROR_UNAUTHORIZED);
    }

    /**
     * check if resource @RequiresAuthentication works
     */
    @Test(enabled = IS_ENABLED)
    public void testAuthenticationViaAnnotation()
            throws Exception {
        String protectedUri = testContext + "/protectedAnnotatedAuthenticatedResource";

        Response response = sendRequest(Method.GET, MediaType.APPLICATION_JSON, protectedUri, null, null
                , TestDataGenerator.TEST_BUSINESS_ADMIN_ACCOUNT_EMAIL, TestDataGenerator.TEST_BUSINESS_ADMIN_ACCOUNT_PASS
                , null
        );

        assertNotNull(response);

        // should pass because annotated resource requires an authenticated user
        assertEquals(response.getStatus(), Status.SUCCESS_OK);

        assertEquals(response.getEntity().getText(), ProtectedAnnotatedAuthenticatedResource.myMessage);
    }

    /**
     * check if security token is set correctly is and is reused on subsequent invocation
     */
    @Test(enabled = IS_ENABLED)
    public void testRememberMe()
            throws Exception {
        String protectedUri = testContext + "/protectedResource";

        final Response response = sendRequest(Method.GET, MediaType.APPLICATION_JSON, protectedUri, null, null
                , TestDataGenerator.TEST_BUSINESS_ADMIN_ACCOUNT_EMAIL, TestDataGenerator.TEST_BUSINESS_ADMIN_ACCOUNT_PASS
                , null
        );

        assertNotNull(response);

        // should pass because annotated resource requires an authenticated user
        assertEquals(response.getStatus(), Status.SUCCESS_OK);

        assertEquals(response.getEntity().getText(), ProtectedResource.myMessage);

        Response resp = sendRequest(Method.GET, MediaType.APPLICATION_JSON, protectedUri, null, null, null, null
                , new IRequestModifier() {
                    public void modify(Request request) {
                        request.getCookies().addAll(response.getCookieSettings());
                    }
                }
        );

        assertNotNull(resp);

        // should pass because we sent the coockie from response
        assertEquals(resp.getStatus(), Status.SUCCESS_OK);

        assertEquals(resp.getEntity().getText(), ProtectedResource.myMessage);

    }


  /** check if resource can be accessed logon redirection */
  @Test(enabled = true)
  public void testLogon() throws Exception
  {
    String logonUri = testContext+ LogonUtils.getLoginUri();

    String targetUri = testContext+"/protectedAnnotatedAuthenticatedResource";

    final Form form = new Form();

    form.add( LogonConstants.LOGON_USER_NAME, TestDataGenerator.TEST_BUSINESS_ADMIN_ACCOUNT_EMAIL);
    form.add( LogonConstants.LOGON_PASS_NAME, TestDataGenerator.TEST_BUSINESS_ADMIN_ACCOUNT_PASS);
    form.set( LogonConstants.LOGON_TARGET_URI_NAME, targetUri, false );

   final Response logonResponse = sendRequestWithLogon( Method.POST, MediaType.APPLICATION_JSON
                                     , logonUri, null, null
                                     , form.getWebRepresentation()
                                     , null
                                     , null
                                     );

    assertNotNull( logonResponse );

    // should pass - all correct fields are in
    assertEquals( logonResponse.getStatus(), Status.REDIRECTION_SEE_OTHER );

    Reference rel = logonResponse.getLocationRef();

    assertNotNull( rel );

    // redirected to supplied target
    assertEquals( rel.getPath(), targetUri );

    // should pass because authenticated
    final Response resp = sendRequest(   Method.GET, MediaType.APPLICATION_JSON
                                   , rel.getPath(), rel.getQuery(), rel.getFragment(), null, null
                                     , new IRequestModifier()
                                    {
                                      public void modify( Request request )
                                      {
                                        request.getCookies().addAll( logonResponse.getCookieSettings() );
                                      }
                                    }
             );

    assertNotNull( resp );

    // should pass because we sent the cookie from response
    assertEquals( resp.getStatus(), Status.SUCCESS_OK );

    assertEquals( resp.getEntity().getText(), ProtectedAnnotatedAuthenticatedResource.myMessage );

    // should pass because authenticated
    // we need to reuse cookies that we got from login response, server will not set them again in responce to previous request,
    // since cookies age being set only once or when server wants to modify them
    //http://en.wikipedia.org/wiki/HTTP_cookie
    Response lastResp = sendRequest(   Method.GET, MediaType.APPLICATION_JSON
                                   , targetUri, null, null, null, null
                                     , new IRequestModifier()
                                    {
                                      public void modify( Request request )
                                      {
                                        request.getCookies().addAll( logonResponse.getCookieSettings() );
                                      }
                                    }
             );

    assertNotNull( lastResp );

    // should pass because we sent the cookie from response
    assertEquals( lastResp.getStatus(), Status.SUCCESS_OK );

    assertEquals( lastResp.getEntity().getText(), ProtectedAnnotatedAuthenticatedResource.myMessage );
  }

}
