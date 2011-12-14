package kp.app.util.restlet;

import kp.app.util.Util;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.shiro.util.ThreadContext;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.ClientInfo;
import org.restlet.data.Form;
import org.restlet.data.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 3/22/11
 * Time: 12:51 AM
 * To change this template use File | Settings | File Templates.
 */
public class RequestContextUtil {

    private static final Logger LOG = LoggerFactory.getLogger(RequestContextUtil.class);

    public static InetAddress getInetAddress(Request request) {
        InetAddress clientAddress = null;
        // get the Host/IP the client is coming from:
        String addrString = request.getHostRef().getHostDomain();
        try {
            clientAddress = InetAddress.getByName(addrString);
        } catch (UnknownHostException e) {
            if (LOG.isInfoEnabled())
                LOG.info("Unable to acquire InetAddress from Request", e);
        }
        return clientAddress;
    }

    public static String dumpRequest(Request request) {
        if (request == null)
            return "request is null";
        StringBuilder sb = new StringBuilder(128);
        sb.append("request for: " + request.getOriginalRef());
        ClientInfo ci = request.getClientInfo();
        if (ci != null) {
            sb.append(", client addresses: " + ci.getUpstreamAddress());
            sb.append(", client agent: " + ci.getAgent());
            sb.append(", client accepts: " + ci.getAcceptedMediaTypes());
        }
        return sb.toString();
    }

    public static void put(Object key, Object value) {
        ThreadContext.put(key, value);
    }

    public static Object get(Object key) {
        return ThreadContext.get(key);
    }

    public static boolean containsKey(Object key) {
        return ThreadContext.get(key) != null;
    }

    public static String getAsString(Object key) {
        return getAsString(key, null, true);
    }

    public static String getAsString(Object key, String defaultValue) {
        return getAsString(key, defaultValue, true);
    }

    public static String getAsString(Object key, boolean enforced) {
        return getAsString(key, null, enforced);
    }

    public static String getAsString(Object key, String defaultValue, boolean enforced) {
        Object value;
        if ((value = get(key)) == null) {
            if (defaultValue != null) {
                return defaultValue;
            }
            if (enforced) {
                throw new IllegalArgumentException(key.toString());
            } else {
                return null;
            }
        }
        return value.toString();
    }

    public static int getAsInt(Object key) {
        return getAsInt(key, null, null);
    }

    public static int getAsInt(Object key, Integer defaultValue, Integer max) {
        String value;
        if ((value = getAsString(key, false)) == null) {
            if (defaultValue != null) {
                return (max == null) ? defaultValue : Math.min(defaultValue, max);
            }
            throw new IllegalArgumentException(key.toString());
        }
        try {
            return (max == null) ? Integer.parseInt(value) : Math.min(Integer.parseInt(value), max);
        } catch (NumberFormatException numberFormatException) {
            throw new IllegalArgumentException(String.format("The parameter(%s) for key(%s) is not a 'int' value", value, key));
        }
    }

    public static long getAsLong(Object key) {
        return getAsLong(key, null, null);
    }

    public static long getAsLong(Object key, Long defaultValue) {
        return getAsLong(key, defaultValue, null);
    }

    public static long getAsLong(Object key, Long defaultValue, Long max) {
        String value;
        if ((value = getAsString(key, false)) == null) {
            if (defaultValue != null) {
                return (max == null) ? defaultValue : Math.min(defaultValue, max);
            }
            throw new IllegalArgumentException(key.toString());
        }
        try {
            return (max == null) ? Long.parseLong(value) : Math.min(Long.parseLong(value), max);
        } catch (NumberFormatException numberFormatException) {
            throw new IllegalArgumentException(String.format("The parameter(%s) for key(%s) is not a 'long' value", value, key));
        }
    }

    /**
     * decides if supplied method could be redirected
     */
    public static boolean isRedirectable(Method method) {
        if (Method.POST.equals(method) || Method.PUT.equals(method) || Method.DELETE.equals(method))
            return false;
        return true;
    }

    /**
     * converts supplied bean to a Map
     */
    public static Map<String, Object> toMap(Object bean)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if (bean == null)
            throw new IllegalArgumentException("cannot conver null object to a form");
        Map<String, Object> map = BeanUtils.describe(bean);
        return map;
    }

    /**
     * converts supplied bean to a restlet From
     */
    public static Form toForm(Object bean, String... attributes)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Map<String, Object> map = toMap(bean);
        Form form = new Form();
        if (Util.isEmpty(map))
            return form;
        boolean restrictAttributes = !Util.isEmpty(attributes);
        List<String> al = null;
        if (restrictAttributes)
            al = Arrays.asList(attributes);
        for (Map.Entry<String, Object> e : map.entrySet()) {
            String key = e.getKey();
            String val = e.getValue() == null ? "" : e.getValue().toString();
            if (restrictAttributes) {
                if (al.contains(key))
                    form.add(key, val);
            } else
                form.add(key, val);
        }
        return form;
    }

    /**
     * Set response header
     */
    public static void setResponseHeader(Response response, String header, String value) {
        Form headers = (Form) response.getAttributes().get("org.restlet.http.headers");
        if (headers == null) {
            headers = new Form();
            response.getAttributes().put("org.restlet.http.headers", headers);
        }
        headers.add(header, value);
    }

}
