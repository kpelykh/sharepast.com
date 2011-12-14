package kp.app.restlet.ext.servlet;

import org.restlet.Server;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.engine.header.ContentType;
import org.restlet.engine.header.Header;
import org.restlet.engine.header.HeaderConstants;
import org.restlet.engine.io.UnclosableInputStream;
import org.restlet.ext.servlet.internal.ServletCall;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 4/21/11
 * Time: 10:53 PM
 * See AppServletAdapter for explanations
 */
public class AppServletCall extends ServletCall {

    public AppServletCall(Server server, HttpServletRequest request, HttpServletResponse response) {
        super(server, request, response);
    }

    public AppServletCall(String serverAddress, int serverPort, HttpServletRequest request, HttpServletResponse response) {
        super(serverAddress, serverPort, request, response);
    }

    @Override
    public InputStream getRequestEntityStream(long size) {
        try {
            Header type = getRequestHeaders().getFirst(HeaderConstants.HEADER_CONTENT_TYPE);
            if (type != null) {
                ContentType contentType = new ContentType(type.getValue());
                if (contentType.getMediaType().equals(MediaType.APPLICATION_WWW_FORM)) {
                    if (getRequest().getInputStream().available() == 0 && getRequest().getParameterMap().size()>0) {
                        Form form = new Form();
                        for (Object o : getRequest().getParameterMap().entrySet()) {
                            Map.Entry<String, String[]> entry = (Map.Entry<String, String[]>) o;
                            String key = entry.getKey();
                            String[] value = entry.getValue();
                            form.add(key, value[0]);
                        }

                        return new UnclosableInputStream(new ByteArrayInputStream(form.encode().getBytes()));
                    }
                }
            }
            return new UnclosableInputStream(getRequest().getInputStream());
        } catch (IOException e) {
            return null;
        }
    }
}
