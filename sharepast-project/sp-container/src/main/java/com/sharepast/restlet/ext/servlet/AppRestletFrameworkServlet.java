package com.sharepast.restlet.ext.servlet;

import org.restlet.Application;
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.ext.servlet.ServletAdapter;
import org.springframework.beans.BeansException;
import org.springframework.web.servlet.FrameworkServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 4/13/11
 * Time: 12:49 AM

 * Overrides default ServletAdapter with AppServletAdapter

 */
public class AppRestletFrameworkServlet extends FrameworkServlet {

    /**
     * The default bean name for the target Restlet.
     */
    private static final String DEFAULT_TARGET_RESTLET_BEAN_NAME = "root";

    private static final long serialVersionUID = 1L;

    /**
     * The adapter of Servlet calls into Restlet equivalents.
     */
    private volatile AppServletAdapter adapter;

    /**
     * The bean name of the target Restlet.
     */
    private volatile String targetRestletBeanName;

    /**
     * Creates the Restlet {@link org.restlet.Context} to use if the target application does
     * not already have a context associated, or if the target restlet is not an
     * {@link Application} at all.
     * <p/>
     * Uses a simple {@link org.restlet.Context} by default.
     *
     * @return A new instance of {@link org.restlet.Context}
     */
    protected Context createContext() {
        return new Context();
    }

    @Override
    protected void doService(HttpServletRequest request,
                             HttpServletResponse response) throws ServletException, IOException {
        getAdapter().service(request, response);
    }

    /**
     * Provides access to the {@link ServletAdapter} used to handle requests.
     * Exposed so that subclasses may do additional configuration, if necessary,
     * by overriding {@link #initFrameworkServlet()}.
     *
     * @return The adapter of Servlet calls into Restlet equivalents.
     */
    protected ServletAdapter getAdapter() {
        return this.adapter;
    }

    /**
     * Returns the target Restlet from Spring's Web application context.
     *
     * @return The target Restlet.
     */
    protected Restlet getTargetRestlet() {
        return (Restlet) getWebApplicationContext().getBean(
                getTargetRestletBeanName());
    }

    /**
     * Returns the bean name of the target Restlet. Returns "root" by default.
     *
     * @return The bean name.
     */
    public String getTargetRestletBeanName() {
        return (this.targetRestletBeanName == null) ? DEFAULT_TARGET_RESTLET_BEAN_NAME
                : this.targetRestletBeanName;
    }

    @Override
    protected void initFrameworkServlet() throws ServletException,
            BeansException {
        super.initFrameworkServlet();
        this.adapter = new AppServletAdapter(getServletContext());

        org.restlet.Application application;

        if (getTargetRestlet() instanceof Application) {
            application = (Application) getTargetRestlet();
        } else {
            application = new Application();
            application.setInboundRoot(getTargetRestlet());
        }

        if (application.getContext() == null) {
            application.setContext(createContext());
        }

        this.adapter.setNext(application);
    }

    /**
     * Sets the bean name of the target Restlet.
     *
     * @param targetRestletBeanName The bean name.
     */
    public void setTargetRestletBeanName(String targetRestletBeanName) {
        this.targetRestletBeanName = targetRestletBeanName;
    }
}
