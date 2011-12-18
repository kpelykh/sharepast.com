package com.sharepast.security;

import com.sharepast.constants.LogonConstants;
import com.sharepast.util.Util;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.*;
import org.restlet.security.ChallengeAuthenticator;
import org.restlet.util.Series;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: kpelykh
 * Date: 8/16/11
 * Time: 12:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class ShiroAuthenticator extends ChallengeAuthenticator {

    /**
     * The name of the HTML login form field containing the identifier.
     */
    private volatile String identifierFormName;

    /**
     * Indicates if the login requests should be intercepted.
     */
    private volatile boolean interceptingLogin;

    /**
     * Indicates if the logout requests should be intercepted.
     */
    private volatile boolean interceptingLogout;

    /**
     * The name of the query parameter containing the URI to redirect the
     * browser to after login or logout.
     */
    private volatile String redirectQueryName;

    /**
     * The name of the HTML login form field containing the secret.
     */
    private volatile String secretFormName;

    /**
     * The login URI path to intercept.
     */
    private volatile String loginPath;

    /**
     * The logout URI path to intercept.
     */
    private volatile String logoutPath;

    private int httpPort = 9090;
    private int httpsPort = 9091;

    public void setHttpPort(int httpPort) {
        this.httpPort = httpPort;
    }

    public void setHttpsPort(int httpsPort) {
        this.httpsPort = httpsPort;
    }


    /**
     * Constructor. Use the {@link org.restlet.data.ChallengeScheme#HTTP_COOKIE} pseudo-scheme.
     *
     * @param context  The parent context.
     * @param optional Indicates if this authenticator is optional so alternative
     *                 authenticators down the chain can be attempted.
     * @param realm    The name of the security realm.
     */
    public ShiroAuthenticator(Context context, boolean optional, String realm) {
        super(context, optional, ChallengeScheme.HTTP_COOKIE, realm);
        this.interceptingLogin = true;
        this.interceptingLogout = true;
        this.identifierFormName = "login";
        this.loginPath = "/login";
        this.logoutPath = "/logout";
        this.secretFormName = "password";
        this.redirectQueryName = "targetUri";
    }

    /**
     * Constructor for mandatory cookie authenticators.
     *
     * @param context The parent context.
     * @param realm   The name of the security realm.
     */
    public ShiroAuthenticator(Context context, String realm) {
        this(context, false, realm);
    }

    /**
     * Attempts to redirect the user's browser can be redirected to the URI
     * provided in a query parameter named by {@link #getRedirectQueryName()}.
     *
     * @param request  The current request.
     * @param response The current response.
     */
    protected boolean attemptRedirect(Request request, Response response) {

        Form loginForm = (Form) request.getAttributes().get("form");

        Subject subject = SecurityUtils.getSubject();

        if (loginForm != null && !Util.isEmpty(loginForm.getFirstValue(LogonConstants.LOGON_TARGET_URI_NAME))) {
            Reference ref = Util.form2reference(request, loginForm);
            response.redirectSeeOther(ref);
            return true;
        }


        if (subject.isAuthenticated() && request.getResourceRef().getPath().contains(getLoginPath())) {
            Reference targetUri = LogonUtils.getHomeReference(request);
            response.redirectSeeOther(targetUri);
            return true;
        }

        return false;
    }

    /**
     * Sets or update the credentials cookie.
     */
    @Override
    protected int authenticated(Request request, Response response) {
        super.authenticated(request, response);
        // Attempt to redirect
        if (!attemptRedirect(request, response)) {
                //proceed for all secured urls
                return CONTINUE;
        }
        return STOP;
    }

    /**
     * Optionally handles the login and logout actions by intercepting the HTTP
     * calls to the {@link #getLoginPath()} and {@link #getLogoutPath()} URIs.
     */
    @Override
    protected int beforeHandle(Request request, Response response) {

        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated() && request.getResourceRef().getPath().contains(getLoginPath())) {
            Reference targetUri = LogonUtils.getHomeReference(request);
            response.redirectSeeOther(targetUri);
        } else if (isLoggingIn(request, response)) {
            login(request, response);
        } else if (isLoggingOut(request, response)) {
            return logout(request, response);
        }

        return super.beforeHandle(request, response);
    }

    /**
     * Returns the name of the HTML login form field containing the identifier.
     * Returns "login" by default.
     *
     * @return The name of the HTML login form field containing the identifier.
     */
    public String getIdentifierFormName() {
        return identifierFormName;
    }

    /**
     * Returns the login URI path to intercept.
     *
     * @return The login URI path to intercept.
     */
    public String getLoginPath() {
        return loginPath;
    }

    /**
     * Returns the logout URI path to intercept.
     *
     * @return The logout URI path to intercept.
     */
    public String getLogoutPath() {
        return logoutPath;
    }

    /**
     * Returns the name of the query parameter containing the URI to redirect
     * the browser to after login or logout. By default, it uses "targetUri".
     *
     * @return The name of the query parameter containing the URI to redirect
     *         the browser to after login or logout.
     */
    public String getRedirectQueryName() {
        return redirectQueryName;
    }

    /**
     * Returns the name of the HTML login form field containing the secret.
     * Returns "password" by default.
     *
     * @return The name of the HTML login form field containing the secret.
     */
    public String getSecretFormName() {
        return secretFormName;
    }

    /**
     * Indicates if the login requests should be intercepted.
     *
     * @return True if the login requests should be intercepted.
     */
    public boolean isInterceptingLogin() {
        return interceptingLogin;
    }

    /**
     * Indicates if the logout requests should be intercepted.
     *
     * @return True if the logout requests should be intercepted.
     */
    public boolean isInterceptingLogout() {
        return interceptingLogout;
    }

    /**
     * Indicates if the request is an attempt to log in and should be
     * intercepted.
     *
     * @param request  The current request.
     * @param response The current response.
     * @return True if the request is an attempt to log in and should be
     *         intercepted.
     */
    protected boolean isLoggingIn(Request request, Response response) {
        return isInterceptingLogin()
                && request.getResourceRef().getPath().contains(getLoginPath())
                && Method.POST.equals(request.getMethod());
    }

    /**
     * Indicates if the request is an attempt to log out and should be
     * intercepted.
     *
     * @param request  The current request.
     * @param response The current response.
     * @return True if the request is an attempt to log out and should be
     *         intercepted.
     */
    protected boolean isLoggingOut(Request request, Response response) {
        return isInterceptingLogout()
                && request.getResourceRef().getPath().contains(getLogoutPath())
                && (Method.GET.equals(request.getMethod()) || Method.POST
                .equals(request.getMethod()));
    }

    /**
     * Processes the login request.
     *
     * @param request  The current request.
     * @param response The current response.
     */
    protected void login(Request request, Response response) {
        // Login detected
        Form form = new Form(request.getEntity());
        Parameter identifier = form.getFirst(getIdentifierFormName());
        Parameter secret = form.getFirst(getSecretFormName());

        // Set credentials
        ChallengeResponse cr = new ChallengeResponse(getScheme(),
                identifier != null ? identifier.getValue() : null,
                secret != null ? secret.getValue() : null);
        request.setChallengeResponse(cr);
        request.getAttributes().put("form", form);

        if (form.getFirstValue(LogonConstants.LOGON_TARGET_URI_NAME) != null)
            request.getAttributes().put(LogonConstants.LOGON_TARGET_URI_NAME, form.getFirstValue(LogonConstants.LOGON_TARGET_URI_NAME));

        if (form.getFirstValue(LogonConstants.LOGON_TARGET_QUERY_NAME) != null)
            request.getAttributes().put(LogonConstants.LOGON_TARGET_QUERY_NAME, form.getFirstValue(LogonConstants.LOGON_TARGET_QUERY_NAME));


    }

    /**
     * Processes the logout request.
     *
     * @param request  The current request.
     * @param response The current response.
     */
    protected int logout(Request request, Response response) {
        // Clears the credentials
        request.setChallengeResponse(null);

        Subject subject = SecurityUtils.getSubject();

        if (subject != null) {
            subject.logout();
        }

        cleanCookies(request, response);

        String host = request.getResourceRef().getHostDomain();
        Reference targetUri = new Reference(Protocol.HTTP.getSchemeName(), host, httpPort, "/", null, null);

        // Attempt to redirect
        response.redirectSeeOther(targetUri);


        return STOP;
    }

    /**
     * cleans all but rememberMe cookies from the response
     */
    private void cleanCookies(Request request, Response response) {
        Series<Cookie> inCss = request.getCookies();
        Series<CookieSetting> css = response.getCookieSettings();
        List<CookieSetting> badCookies = null;

        if (inCss != null && inCss.size() > 0)
            for (Cookie c : inCss)
                if (c.getName() != null && c.getName().startsWith("_")) {
                    if (badCookies == null)
                        badCookies = new ArrayList<CookieSetting>(inCss.size());

                    badCookies.add(new CookieSetting(0, c.getName(), null, "/",
                            null, null, -1, false));
                }

        if (!Util.isEmpty(badCookies) && inCss != null) {
            inCss.removeAll(badCookies);
        }
    }

    /**
     * Sets the name of the HTML login form field containing the identifier.
     *
     * @param loginInputName The name of the HTML login form field containing the
     *                       identifier.
     */
    public void setIdentifierFormName(String loginInputName) {
        this.identifierFormName = loginInputName;
    }

    /**
     * Indicates if the login requests should be intercepted.
     *
     * @param intercepting True if the login requests should be intercepted.
     */
    public void setInterceptingLogin(boolean intercepting) {
        this.interceptingLogin = intercepting;
    }

    /**
     * Indicates if the logout requests should be intercepted.
     *
     * @param intercepting True if the logout requests should be intercepted.
     */
    public void setInterceptingLogout(boolean intercepting) {
        this.interceptingLogout = intercepting;
    }

    /**
     * Sets the login URI path to intercept.
     *
     * @param loginPath The login URI path to intercept.
     */
    public void setLoginPath(String loginPath) {
        this.loginPath = loginPath;
    }

    /**
     * Sets the logout URI path to intercept.
     *
     * @param logoutPath The logout URI path to intercept.
     */
    public void setLogoutPath(String logoutPath) {
        this.logoutPath = logoutPath;
    }

    /**
     * Sets the name of the query parame containing the URI to redirect the
     * browser to after login or logout.
     *
     * @param redirectQueryName The name of the query parameter containing the URI to redirect
     *                          the browser to after login or logout.
     */
    public void setRedirectQueryName(String redirectQueryName) {
        this.redirectQueryName = redirectQueryName;
    }

    /**
     * Sets the name of the HTML login form field containing the secret.
     *
     * @param passwordInputName The name of the HTML login form field containing the secret.
     */
    public void setSecretFormName(String passwordInputName) {
        this.secretFormName = passwordInputName;
    }

}
