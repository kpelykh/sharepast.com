package com.sharepast.its.app00270;

import com.sharepast.constants.LogonConstants;
import com.sharepast.dal.util.TestDataGenerator;
import com.sharepast.its.common.AbstractPlatformSupport;
import com.sharepast.its.common.IRequestModifier;
import com.sharepast.security.LogonUtils;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.*;
import org.restlet.util.Series;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 8/31/11
 * Time: 10:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class AppSecurityRestExtentionsTest extends AbstractPlatformSupport {

    private static final String testContext = "/app00270";

    String logonUri = testContext+ LogonUtils.getLoginUri();

    private static final boolean ENABLED = true;

    /**
     * check if resource will be redirected trying to access BUSINESS_OWNER protected context
     */
    @Test(enabled = ENABLED)
    public void testRedirectMe() throws Exception {
        long userId = testUser.getId();

        String protectedUri = testContext + "/protectedByContext/" + userId + "/secured";

        Response response = sendRequest(Method.GET, MediaType.APPLICATION_JSON, protectedUri, null, null, null, null, null);

        assertNotNull(response);

        // should redirect us to logon because not authenticated
        assertEquals(response.getStatus(), Status.CLIENT_ERROR_UNAUTHORIZED);

        final Form form = new Form();

        form.add(LogonConstants.LOGON_TARGET_URI_NAME, protectedUri);
        form.add(LogonConstants.LOGON_USER_NAME, TestDataGenerator.TEST_BUSINESS_ADMIN_ACCOUNT_EMAIL);
        form.add(LogonConstants.LOGON_PASS_NAME, TestDataGenerator.TEST_BUSINESS_ADMIN_ACCOUNT_PASS);

        response = sendRequestWithLogon( Method.POST, MediaType.APPLICATION_JSON
                                     , logonUri, null, null
                                     , form.getWebRepresentation()
                                     , null
                                     , null
                                     );


        assertNotNull(response);

        // should redirect us to original resource
        assertEquals(response.getStatus(), Status.REDIRECTION_SEE_OTHER);

        Reference finalRef = response.getLocationRef();

        final Series<CookieSetting> cs = response.getCookieSettings();

        response = sendRequest(Method.GET, MediaType.APPLICATION_JSON
                , finalRef.getPath()
                , finalRef.getQuery()
                , finalRef.getFragment()
                , null, null
                , new IRequestModifier() {
            public void modify(Request request) {
                for (CookieSetting s : cs)
                    request.getCookies().add(s);
            }
        });

        assertNotNull(response);

        // should be original resource. At last
        assertEquals(response.getStatus(), Status.SUCCESS_OK);

        assertNotNull(response.getEntity());

        String text = response.getEntity().getText();

        assertNotNull(text);

        assertEquals(text, ProtectedAnnotatedAuthenticatedResource.myMessage);
    }

  /** check if resource will be able to access permission protected context. test user does not have the permission */
  @Test(enabled = ENABLED)
  public void testNoPermission()
  throws Exception
  {

    long userId = testUser.getId();

    String protectedUri = testContext+"/protectedByPermission/"+userId+"/secured";

    Response response = sendRequest( Method.GET, MediaType.APPLICATION_JSON, protectedUri, null, null, null, null, null );

    assertNotNull( response );

    // should redirect us to logon
    assertEquals( response.getStatus(), Status.CLIENT_ERROR_UNAUTHORIZED );

    final Form form = new Form();

    form.add( LogonConstants.LOGON_TARGET_URI_NAME, protectedUri );
    form.add( LogonConstants.LOGON_USER_NAME, TestDataGenerator.TEST_BUSINESS_ADMIN_ACCOUNT_EMAIL);
    form.add( LogonConstants.LOGON_PASS_NAME, TestDataGenerator.TEST_BUSINESS_ADMIN_ACCOUNT_PASS);


    form.add(LogonConstants.LOGON_TARGET_URI_NAME, protectedUri);
    form.add(LogonConstants.LOGON_USER_NAME, TestDataGenerator.TEST_BUSINESS_ADMIN_ACCOUNT_EMAIL);
    form.add(LogonConstants.LOGON_PASS_NAME, TestDataGenerator.TEST_BUSINESS_ADMIN_ACCOUNT_PASS);

    response = sendRequestWithLogon( Method.POST, MediaType.APPLICATION_JSON
                               , logonUri, null, null
                               , form.getWebRepresentation()
                               , null
                               , null
                               );



    assertNotNull( response );

    // should redirect us to original resource
    assertEquals( response.getStatus(), Status.REDIRECTION_SEE_OTHER );

    Reference finalRef = response.getLocationRef();

    final Series<CookieSetting> cs = response.getCookieSettings();

    response = sendRequest( Method.GET, MediaType.APPLICATION_JSON
                             , finalRef.getPath()
                             , finalRef.getQuery()
                             , finalRef.getFragment()
                             , null, null
                             , new IRequestModifier() {
                                public void modify(Request request) {
                                    for (CookieSetting s : cs)
                                        request.getCookies().add(s);
                                }
                            });

      assertNotNull( response );

      // should fail as test user does not have the permission
      assertEquals( response.getStatus(), Status.CLIENT_ERROR_UNAUTHORIZED );
  }


  /** check if resource will be able to access role protected context. test user does not have the role */
  @Test(enabled = ENABLED)
  public void testNoRole()
  throws Exception
  {
    long userId = testUser.getId();

    String protectedUri = testContext+"/protectedByRole/"+userId+"/secured";

    Response response = sendRequest( Method.GET, MediaType.APPLICATION_JSON, protectedUri, null, null, null, null, null );

    assertNotNull( response );

    // should redirect us to logon
    assertEquals( response.getStatus(), Status.CLIENT_ERROR_UNAUTHORIZED );

    final Form form = new Form();

    form.add(LogonConstants.LOGON_TARGET_URI_NAME, protectedUri);
    form.add(LogonConstants.LOGON_USER_NAME, TestDataGenerator.TEST_BUSINESS_ADMIN_ACCOUNT_EMAIL);
    form.add(LogonConstants.LOGON_PASS_NAME, TestDataGenerator.TEST_BUSINESS_ADMIN_ACCOUNT_PASS);

    response = sendRequestWithLogon( Method.POST, MediaType.APPLICATION_JSON
                                     , logonUri, null, null
                                     , form.getWebRepresentation()
                                     , null
                                     , null
                                     );


    assertNotNull( response );

    // should redirect us to original resource
    assertEquals( response.getStatus(), Status.REDIRECTION_SEE_OTHER );

    Reference finalRef = response.getLocationRef();

    final Series<CookieSetting> cs = response.getCookieSettings();

    response = sendRequest( Method.GET, MediaType.APPLICATION_JSON
                             , finalRef.getPath()
                             , finalRef.getQuery()
                             , finalRef.getFragment()
                             , null, null
                             , new IRequestModifier() {
                                public void modify(Request request) {
                                    for (CookieSetting s : cs)
                                        request.getCookies().add(s);
                                }
                            });

    assertNotNull( response );

    // should fail as test user does not have the permission
    assertEquals( response.getStatus(), Status.CLIENT_ERROR_UNAUTHORIZED );
  }

}
