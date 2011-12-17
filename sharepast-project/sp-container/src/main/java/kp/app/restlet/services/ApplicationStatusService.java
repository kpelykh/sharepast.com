package kp.app.restlet.services;

import kp.app.constants.LogonConstants;
import kp.app.resources.LoginResource;
import kp.app.restlet.exceptions.ClientResourceException;
import kp.app.restlet.freemarker.AbstractConfigurableResource;
import kp.app.restlet.freemarker.JsonRepresentationFactory;
import kp.app.restlet.freemarker.RepresentationFactory;
import kp.app.security.LogonUtils;
import kp.app.util.Util;
import org.apache.shiro.ShiroException;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.service.StatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.util.*;

public class ApplicationStatusService extends StatusService {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationStatusService.class);
    private static final JsonRepresentationFactory JSON_FACTORY = new JsonRepresentationFactory();

    private static List<MediaType> canAccept = new ArrayList<MediaType>(3);

    @Autowired
    private ReloadableResourceBundleMessageSource messageSource;

    static {
        canAccept = new ArrayList<MediaType>(3);
        canAccept.add(MediaType.TEXT_HTML);
        canAccept.add(MediaType.APPLICATION_JSON);
        canAccept.add(MediaType.APPLICATION_JAVA_OBJECT);
    }

    /**
     * holds the map status templates
     */
    private LinkedHashMap<Status, RepresentationFactory> statusTemplateFactories;
    private RepresentationFactory defaultTemplateFactory;


    private List<String> throwable2list(Throwable throwable) {

        List<String> errors = new ArrayList<String>();
        
        if (throwable != null) {
            if (throwable instanceof UnauthorizedException) {
                errors.add(messageSource.getMessage("unauthorized.exception", null, Locale.getDefault()));
            }
            if (throwable instanceof IncorrectCredentialsException) {
                errors.add(messageSource.getMessage("incorrect.credentials.exception", null, Locale.getDefault()));
            } else if (throwable instanceof UnauthenticatedException) {
                errors.add(messageSource.getMessage("unauthenticated.exception", null, Locale.getDefault()));
            } else if (throwable instanceof UnknownAccountException) {
                errors.add(messageSource.getMessage("unknown.account.exception", null, Locale.getDefault()));
            } else if (throwable instanceof CredentialsException) {
                errors.add(messageSource.getMessage("credentials.exception", null, Locale.getDefault()));
            } else if (throwable instanceof LockedAccountException) {
                errors.add(messageSource.getMessage("locked.account.exception", null, Locale.getDefault()));
            } else if (throwable instanceof ExcessiveAttemptsException) {
                errors.add(messageSource.getMessage("excessive.attempts.exception", null, Locale.getDefault()));
            } else if (throwable instanceof AuthenticationException) {
                errors.add(messageSource.getMessage("authentication.exception", null, Locale.getDefault()));
            } else {
                errors.add(messageSource.getMessage("general.exception", null, Locale.getDefault()));
            }
        }
        return errors;
    }

    @Override
    public Representation getRepresentation(Status status, Request request, Response response) {
        MediaType preferedMediaType = request.getClientInfo().getPreferredMediaType(canAccept);
        Map<String, Object> dataModel = new HashMap<String, Object>(3);

        Throwable throwable = status.getThrowable();
        ClientResourceException cre = null;
        if (throwable != null && ClientResourceException.class.isAssignableFrom(throwable.getClass())) {
            cre = (ClientResourceException) throwable;
        }

        dataModel.put("HTTPRequest", request);

        if (status.equals(Status.CLIENT_ERROR_UNAUTHORIZED)) {
            throwable = (Throwable) request.getAttributes().get("shiro.auth.exception");
            dataModel.put(LogonConstants.LOGON_USER_NAME, response.getAttributes().get(LogonConstants.LOGON_USER_NAME) );
            dataModel.put(LogonConstants.REMEMBER_ME, response.getAttributes().get(LogonConstants.REMEMBER_ME) );

            Reference ref = LogonUtils.getLogonReference(request);
            //Used on the login page for redirecting user to the page which he tried to access before login
            if (request.getAttributes().get(LogonConstants.LOGON_TARGET_URI_NAME) == null) {
                dataModel.put(LogonConstants.LOGON_TARGET_URI_NAME, Util.encodeForUrl(request.getResourceRef().getPath()));
            } else {
                dataModel.put(LogonConstants.LOGON_TARGET_URI_NAME, request.getAttributes().get(LogonConstants.LOGON_TARGET_URI_NAME));
            }

            if (request.getAttributes().get(LogonConstants.LOGON_TARGET_QUERY_NAME) == null) {
                dataModel.put(LogonConstants.LOGON_TARGET_QUERY_NAME, Util.encodeForUrl(request.getResourceRef().getQuery()));
            } else {
                dataModel.put(LogonConstants.LOGON_TARGET_QUERY_NAME, request.getAttributes().get(LogonConstants.LOGON_TARGET_QUERY_NAME));
            }

        }
        
        if (throwable != null) {
            if (cre != null)
                cre.log(LOG);
            else
                LOG.error("status with exception", throwable);

            dataModel.put("error", throwable2list(throwable));
            dataModel.put("code", status.getCode());

            dataModel.put("traces", obtainStackTrace(throwable));

            String[] messages;
            LinkedList<String> messageList = new LinkedList<String>();

            messageList.add(throwable.getMessage());
            messages = new String[messageList.size()];
            messageList.toArray(messages);
            dataModel.put("messages", messages);

            return createRepresentation(status, preferedMediaType, dataModel);
        } else
            return createRepresentation(status, preferedMediaType, dataModel);

    }

    class StatusResource extends AbstractConfigurableResource {
    }

    private Representation createRepresentation(Status status, MediaType preferedMediaType, Map<String, Object> dataModel) {
        AbstractConfigurableResource resource = new StatusResource();
        Representation res;

        if (MediaType.APPLICATION_JSON.equals(preferedMediaType))
            res = JSON_FACTORY.createRepresentation(preferedMediaType, dataModel, resource);
        else {
            RepresentationFactory representationFactory = getStatusTemplateFactories().get(status);

            if (representationFactory == null)
                representationFactory = getDefaultTemplateFactory();

            res = representationFactory.createRepresentation(preferedMediaType, dataModel, resource);
        }

        return res;
    }

    private String[] obtainStackTrace(Throwable throwable) {
        String[] lines;
        LinkedList<String> lineList;
        StackTraceElement[] prevStackTrace = null;
        StringBuilder lineBuilder;
        int repeatedElements;

        lineList = new LinkedList<String>();
        lineBuilder = new StringBuilder(512);
        do {
            lineBuilder.append(prevStackTrace == null ? "Exception in thread " : "Caused by: ");

            lineBuilder.append(throwable.getClass().getCanonicalName());
            lineBuilder.append(": ");
            lineBuilder.append(throwable.getMessage());
            lineList.add(lineBuilder.toString());
            lineBuilder.delete(0, lineBuilder.length());

            for (StackTraceElement singleElement : throwable.getStackTrace()) {
                if (prevStackTrace != null) {
                    if ((repeatedElements = findRepeatedStackElements(singleElement, prevStackTrace)) >= 0) {
                        lineBuilder.append("   ... ");
                        lineBuilder.append(repeatedElements);
                        lineBuilder.append(" more");
                        lineList.add(lineBuilder.toString());
                        lineBuilder.delete(0, lineBuilder.length());
                        break;
                    }
                }

                lineBuilder.append("   at ");
                lineBuilder.append(singleElement.toString());
                lineList.add(lineBuilder.toString());
                lineBuilder.delete(0, lineBuilder.length());
            }

            prevStackTrace = throwable.getStackTrace();
        }
        while ((throwable = throwable.getCause()) != null);

        lines = new String[lineList.size()];
        lineList.toArray(lines);

        return lines;
    }

    private static int findRepeatedStackElements(StackTraceElement singleElement, StackTraceElement[] prevStackTrace) {

        for (int count = 0; count < prevStackTrace.length; count++)
            if (singleElement.equals(prevStackTrace[count]))
                return prevStackTrace.length - count;

        return -1;
    }


    @Override
    public Status getStatus(Throwable throwable, Request request, Response response) {
        Status result = null;

        if (throwable instanceof ResourceException) {
            ResourceException re = (ResourceException) throwable;

            if (re.getCause() != null) {
                result = new Status(re.getStatus(), re.getCause());
            } else {
                result = re.getStatus();
            }
        } else if (throwable instanceof ShiroException) {
            LOG.warn(throwable.getMessage());
            result = new Status(Status.CLIENT_ERROR_UNAUTHORIZED, throwable);
        } else {
            result = new Status(Status.SERVER_ERROR_INTERNAL, throwable);
        }
        //DisabledAccountException, ExcessiveAttemptsException, ExpiredCredentialsException, IncorrectCredentialsException, LockedAccountException,
        //UnknownAccountException


        return result;
    }


    public RepresentationFactory getDefaultTemplateFactory() {
        return defaultTemplateFactory;
    }

    public void setDefaultTemplateFactory(RepresentationFactory defaultTemplateFactory) {
        this.defaultTemplateFactory = defaultTemplateFactory;
    }

    private Map<Status, RepresentationFactory> getStatusTemplateFactories() {
        return statusTemplateFactories != null ? statusTemplateFactories : Collections.EMPTY_MAP;
    }

    public void setStatusTemplates(final Map<Status, RepresentationFactory> representationTemplates) {
        if (this.statusTemplateFactories == null)
            this.statusTemplateFactories = new LinkedHashMap<Status, RepresentationFactory>(representationTemplates.size());
        else
            this.statusTemplateFactories.clear();

        // accept all configured representations
        this.statusTemplateFactories.putAll(representationTemplates);
    }
}
