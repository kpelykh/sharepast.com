package com.sharepast.security;

import com.sharepast.constants.LogonConstants;
import com.sharepast.util.restlet.RequestContextUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.Form;
import org.restlet.security.User;
import org.restlet.security.Verifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 4/17/11
 * Time: 1:39 AM
 * To change this template use File | Settings | File Templates.
 */
@Component("shiroVerifier")
public class ShiroVerifier implements Verifier {

    private static final Logger LOG = LoggerFactory.getLogger(ShiroVerifier.class);

    @Override
    public int verify(Request request, Response response) {
        int result = RESULT_UNKNOWN;

        Subject subject = SecurityUtils.getSubject();

        if( subject == null )
        {
          LOG.error( "cannot obtail the subject for request "+ RequestContextUtil.dumpRequest(request) );
          return RESULT_MISSING;
        }

        if (subject != null && subject.isAuthenticated()) {
            return RESULT_VALID;
        }

        if( !subject.isAuthenticated())
        {
            String identifier = getIdentifier(request, response);
            char[] secret = getSecret(request, response);
            boolean rememberMe = getRememberMe(request, response);

            if (identifier == null || secret == null) {
                return RESULT_MISSING;
            }

            try {
                if (verify(subject, request, identifier, secret, rememberMe)) {
                    request.getClientInfo().setUser(new User(identifier));
                    return RESULT_VALID;
                } else {
                    result = RESULT_INVALID;
                    response.getAttributes().put(LogonConstants.LOGON_USER_NAME, identifier);
                    response.getAttributes().put(LogonConstants.REMEMBER_ME, rememberMe);
                }
            } catch (IllegalArgumentException iae) {
                // The identifier is unknown.
                result = RESULT_UNKNOWN;
            }
        }

        return result;
    }

    private boolean verify(Subject subj, Request request, String identifier, char[] secret, boolean rememberMe) {
        try
        {
          AuthenticationToken authenticationToken =
                  new UsernamePasswordToken( identifier,
                                             secret,
                                             rememberMe,
                                             RequestContextUtil.getInetAddress(request).getHostName()
                  );
          subj.login(authenticationToken);
        }
        catch ( Exception e )
        {
            request.getAttributes().put("shiro.auth.exception", e);
            return false;
        }
        return true;
    }

    /**
     * Returns the user identifier.
     *
     * @param request
     *            The request to inspect.
     * @param response
     *            The response to inspect.
     * @return The user identifier.
     */
    protected String getIdentifier(Request request, Response response) {
        ChallengeResponse rsp = request.getChallengeResponse();
        return rsp == null ? null : request.getChallengeResponse().getIdentifier();
    }

    /**
     * Returns the secret provided by the user.
     *
     * @param request
     *            The request to inspect.
     * @param response
     *            The response to inspect.
     * @return The secret provided by the user.
     */
    protected char[] getSecret(Request request, Response response) {
        ChallengeResponse rsp = request.getChallengeResponse();
        return rsp == null ? null : request.getChallengeResponse().getSecret();
    }

    protected boolean getRememberMe(Request request, Response response) {
        Form form = (Form) request.getAttributes().get("form");
        if (form != null) {
            String rememberMe = form.getFirstValue(LogonConstants.REMEMBER_ME);
            return Boolean.parseBoolean(rememberMe);
        } else {
            return false;
        }
    }

}
