package kp.app.security;

import kp.app.constants.LogonConstants;
import kp.app.util.Util;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.Protocol;
import org.restlet.data.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 4/16/11
 * Time: 1:20 AM
 * To change this template use File | Settings | File Templates.
 */

public class LogonUtils {

    private final Logger LOG = LoggerFactory.getLogger(LogonUtils.class);

    private static String apiPrefix = "/app";
    private static String publicApiPrefix = "/pub";
    private static String loginUri = "/login";
    private static String homeUri = "/home";
    private static String rootUri = "/";
    private static String logoutUri = "/logout";
    private static String postLogoutUri = "/";

    private static int httpPort = 9090;

    public LogonUtils(String apiPrefix, String publicApiPrefix) {
        super();
        // need them to be visible to all threads right away
        synchronized (LogonUtils.class) {
            LogonUtils.apiPrefix = apiPrefix;
            LogonUtils.publicApiPrefix = publicApiPrefix;
            LogonUtils.homeUri = apiPrefix + homeUri;
            LogonUtils.postLogoutUri = apiPrefix + postLogoutUri;
        }
    }

    public void setHttpPort(int httpPort) {
        LogonUtils.httpPort = httpPort;
    }

    public int getHttpPort() {
        return httpPort;
      }

    public static void redirectToNoPrivileges(Request request, Response response) {
        LogonUtils.redirectMissingPrivileges(request, response);

    }

    public static void redirectToLogon(Request request, Response response) {
        Reference ref = getLogonReference(request);

        response.setLocationRef(ref);
    }

    public static void redirectMissingPrivileges(Request request, Response response) {
       Reference originalRef = request.getResourceRef();
       String host = originalRef.getHostDomain();

       Reference ref = new Reference(Protocol.HTTP.getSchemeName(), host, httpPort, rootUri, null, null);

        response.setLocationRef(ref);
    }


    public static Reference getLogonReference(Request request) {
        Reference originalRef = request.getResourceRef();
        String scheme = originalRef.getScheme();
        String host = originalRef.getHostDomain();

        Reference ref = new Reference(Protocol.HTTP.getSchemeName(), host, httpPort, loginUri, null, null);

        if (!Util.isEmpty(originalRef.getPath())) {
            ref.addQueryParameter(LogonConstants.LOGON_TARGET_URI_NAME, originalRef.getPath());
        }
        if (!Util.isEmpty(originalRef.getQuery())) {
            ref.addQueryParameter(LogonConstants.LOGON_TARGET_QUERY_NAME, originalRef.getQuery());
        }

        return ref;
    }

    public static Reference getHomeReference(Request request) {
        Reference originalRef = request.getResourceRef();
        String host = originalRef.getHostDomain();

        Reference ref = new Reference(Protocol.HTTP.getSchemeName(), host, httpPort, homeUri, null, null);

        return ref;
    }

    public static String getApiPrefix() {
        return apiPrefix;
    }

    public static String getPublicApiPrefix() {
        return publicApiPrefix;
    }

    public static String getLoginUri() {
        return loginUri;
    }

    public static String getHomeUri() {
        return homeUri;
    }

    public static String getLogoutUri() {
        return logoutUri;
    }

    public static String getPostLogoutUri() {
        return postLogoutUri;
    }
}
