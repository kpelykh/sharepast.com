package com.sharepast.its.common;

import com.sharepast.dal.domain.user.User;
import com.sharepast.dal.exceptions.BadPasswordException;
import com.sharepast.dal.util.DataGenerator;
import com.sharepast.dal.util.TestDataGenerator;
import com.sharepast.util.Util;
import com.sharepast.util.spring.Configurator;
import com.sharepast.util.spring.StartupProperties;
import org.eclipse.jetty.server.Server;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.*;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.util.Series;
import org.slf4j.Logger;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 3/21/11
 * Time: 11:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class AbstractPlatformSupport {

    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(AbstractPlatformSupport.class);

    public static final String DEFAULT_SERVER_NAME = "testHttpServer";

    protected static volatile int httpPort = -1;

    protected static volatile int httpsPort = -1;

    protected static volatile Server appHttpServer;

    protected Series<CookieSetting> cs;

    protected static volatile User testUser;

    protected static volatile DataGenerator testDataGenerator;

    protected String[] getConfigurations() {
        return new String[]{
                "com/sharepast/its/app00105/test-server-00105.xml",
                "com/sharepast/its/app00200/test-server-00200.xml",
                "com/sharepast/its/app00205/test-server-00205.xml",
                "com/sharepast/its/app00270/test-server-00270.xml"
        };
    }

    protected void generateSuiteData()
            throws BadPasswordException {
        // need a user to logon under
        testUser =
                testDataGenerator.findOrCreateAccount(TestDataGenerator.TEST_BUSINESS_ADMIN_ACCOUNT_USERNAME,
                        TestDataGenerator.TEST_BUSINESS_ADMIN_ACCOUNT_EMAIL,
                        TestDataGenerator.TEST_BUSINESS_ADMIN_ACCOUNT_PASS,
                        TestDataGenerator.TEST_BUSINESS_ADMIN_ACCOUNT_SALT, TestDataGenerator.TEST_ACCOUNT_ROLE);
    }

    @BeforeMethod
    protected void cleanCookie() {
        cs = null;
    }

    @BeforeSuite
    protected void startServer()
            throws Exception {
        // prepare port information, set into system properties
        int generatedHttpPort = Util.findFreeRandomPort();
        Assert.assertTrue(generatedHttpPort > 1024, "Couldn't find a port above 1024");
        Assert.assertTrue(generatedHttpPort < 65537, "Couldn't find a port below 65537");
        System.setProperty(StartupProperties.SYSTEM_PROPERTY_HTTP_PORT.getKey(), "" + generatedHttpPort);

        int generatedHttpsPort = Util.findFreeRandomPort();
        Assert.assertTrue(generatedHttpsPort > 1024, "Couldn't find a port above 1024");
        Assert.assertTrue(generatedHttpsPort < 65537, "Couldn't find a port below 65537");
        System.setProperty(StartupProperties.SYSTEM_PROPERTY_HTTPS_PORT.getKey(), "" + generatedHttpsPort);

        Configurator.getInstance().addConfiguration("com/sharepast/base.xml");
        Configurator.getInstance().addConfiguration("com/sharepast/cache.xml");
        Configurator.getInstance().addConfiguration("com/sharepast/security.xml");
        Configurator.getInstance().addConfiguration("com/sharepast/persistence/sp-hibernate.xml");
        Configurator.getInstance().addConfiguration("com/sharepast/security.xml");
        Configurator.getInstance().addConfiguration("com/sharepast/its/common/test-server.xml");
        Configurator.getInstance().addConfiguration("com/sharepast/service/services.xml");
        Configurator.getInstance().addConfiguration("com/sharepast/service/geoip-location.xml");

        //Add here any test-specific configurations
        Configurator.getInstance().configure(getConfigurations());

        // grab test data generator
        testDataGenerator = Configurator.squeeze(DataGenerator.class, "testDataGenerator");
        generateSuiteData();

        // HTTP
        appHttpServer = Configurator.squeeze(Server.class, DEFAULT_SERVER_NAME);
        Assert.assertNotNull(appHttpServer);
        httpPort = appHttpServer.getServer().getConnectors()[0].getPort();
        Assert.assertEquals(httpPort, generatedHttpPort);

        httpsPort = appHttpServer.getServer().getConnectors()[1].getPort();
        Assert.assertEquals(httpsPort, generatedHttpsPort);

        appHttpServer.start();
        LOG.info("HTTPS started on port " + httpsPort);
        LOG.info("HTTP started on port " + httpPort);
    }

    @AfterSuite
    protected void stopServer()
            throws Exception {
        if (appHttpServer != null && appHttpServer.isStarted()) {
            appHttpServer.stop();
            appHttpServer = null;
        }
    }

    protected Response sendRequest(Method method, String path, String query, String fragment) {
        return sendRequest(Protocol.HTTP, method, MediaType.TEXT_HTML, path, query, fragment, null, null, null);
    }

    protected Response sendRequest(Method method, MediaType mediaType, String path, String query, String fragment, String username,
                                   String password, IRequestModifier requestModifier) {
        return sendRequest(Protocol.HTTP, method, mediaType, path, query, fragment, username, password, requestModifier);
    }

    protected Response sendRequest(Protocol protocol, Method method, MediaType mediaType, String path, String query, String fragment,
                                   String username, String password, IRequestModifier requestModifier) {
        int port = Protocol.HTTP.equals(protocol) ? httpPort : httpsPort;
        Reference resourceRef = new Reference(protocol.getName(), "localhost", port, path, query, fragment);

        Context clientContext = new Context();


        System.setProperty("javax.net.ssl.trustStore", "../sp-assembly/src/main/config/de/keystore");
        Series<Parameter> parameters = clientContext.getParameters();
        parameters.add("sslContextFactory", "org.restlet.ext.ssl.internal.DefaultSslContextFactory");
        /*parameters.add("keystorePath", "../sp-assembly/src/main/config/de/keystore");
        parameters.add("keystorePassword", "testkeystorepass");
        parameters.add("keyPassword", "testkeystorepass");
        parameters.add("keystoreType", "JKS");*/

        Context.setCurrent(clientContext);

        ClientResource resource = new ClientResource(method, resourceRef);
        resource.setFollowingRedirects(false);
        if (username != null) {
            ChallengeResponse authentication = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, username, password);
            resource.setChallengeResponse(authentication);
        }
        resource.getClientInfo().getAcceptedMediaTypes().add(new Preference<MediaType>(mediaType));
        if (requestModifier != null)
            requestModifier.modify(resource.getRequest());
        resource.handle();
        return resource.getResponse();
    }

    protected Response sendRequestWithLogon(Method method, MediaType mediaType, String path, String query, String fragment,
                                            Representation rep, String name, String password) {
        Reference resourceRef = new Reference("http", "localhost", httpPort, path, query, fragment);
        ClientResource resource = new ClientResource(method, resourceRef);
        //There's a problem if redirects are enabled. When Request is sent to Login servlet with proper username and password,
        //it returns REDIRECT_SEE_OTHER status, with cookies which include rememberMe token, however cookies are never being
        //passed to a request and server thinks that user was never authenticated.
        //That's why we need to do two separate calls, and explicitly set cookies for the second request
        resource.setFollowingRedirects(false);

        Request request = rep == null ? new Request(method, resourceRef) : new Request(method, resourceRef, rep);
        if (mediaType == null)
            mediaType = MediaType.TEXT_HTML;
        request.getClientInfo().getAcceptedMediaTypes().add(new Preference<MediaType>(mediaType));

        resource.handleOutbound(request);
        return resource.getResponse();
    }

}
