package com.sharepast.spring.mvc;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.*;
import java.io.IOException;

/**
 * Proxy for a standard Servlet 2.3 Filter, delegating to a Spring-managed
 * bean that implements the Filter interface. Supports a "targetBeanName"
 * filter init-param in {@code web.xml}, specifying the name of the
 * target bean in the Spring application context.
 *
 * <p>{@code web.xml} will usually contain a {@code DelegatingFilterProxy} definition,
 * with the specified {@code filter-name} corresponding to a bean name in
 * Spring's root application context. All calls to the filter proxy will then
 * be delegated to that bean in the Spring context, which is required to implement
 * the standard Servlet 2.3 Filter interface.
 *
 * <p>This approach is particularly useful for Filter implementation with complex
 * setup needs, allowing to apply the full Spring bean definition machinery to
 * Filter instances. Alternatively, consider standard Filter setup in combination
 * with looking up service beans from the Spring root application context.
 *
 * <p><b>NOTE:</b> The lifecycle methods defined by the Servlet Filter interface
 * will by default <i>not</i> be delegated to the target bean, relying on the
 * Spring application context to manage the lifecycle of that bean. Specifying
 * the "targetFilterLifecycle" filter init-param as "true" will enforce invocation
 * of the {@code Filter.init} and {@code Filter.destroy} lifecycle methods
 * on the target bean, letting the servlet container manage the filter lifecycle.
 *
 * <p>As of Spring 3.1, {@code DelegatingFilterProxy} has been updated to optionally accept
 * constructor parameters when using Servlet 3.0's instance-based filter registration
 * methods, usually in conjunction with Spring 3.1's
 * {@link org.springframework.web.WebApplicationInitializer} SPI. These constructors allow
 * for providing the delegate Filter bean directly, or providing the application context
 * and bean name to fetch, avoiding the need to look up the application context from the
 * ServletContext.
 *
 * <p>This class was originally inspired by Spring Security's {@code FilterToBeanProxy}
 * class, written by Ben Alex.
 *
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Chris Beams
 * @since 1.2
 * @see #setTargetBeanName
 * @see #setTargetFilterLifecycle
 * @see javax.servlet.Filter#doFilter
 * @see javax.servlet.Filter#init
 * @see javax.servlet.Filter#destroy
 * @see #AppDelegatingFilterProxy(Filter)
 * @see #AppDelegatingFilterProxy(String)
 * @see #AppDelegatingFilterProxy(String, org.springframework.web.context.WebApplicationContext)
 * @see javax.servlet.ServletContext#addFilter(String, Filter)
 * @see org.springframework.web.WebApplicationInitializer
 */
public class AppDelegatingFilterProxy extends GenericFilterBean {

    private String contextAttribute;

	private WebApplicationContext webApplicationContext;

    private String targetBeanName;

    private boolean targetFilterLifecycle = false;

    private Filter delegate;

    private final Object delegateMonitor = new Object();


	/**
	 * Create a new {@code DelegatingFilterProxy}. For traditional (pre-Servlet 3.0) use
	 * in {@code web.xml}.
	 * @see #setTargetBeanName(String)
	 */
	public AppDelegatingFilterProxy() {
	}

	/**
	 * Create a new {@code DelegatingFilterProxy} with the given {@link Filter} delegate.
	 * Bypasses entirely the need for interacting with a Spring application context,
	 * specifying the {@linkplain #setTargetBeanName target bean name}, etc.
	 * <p>For use in Servlet 3.0+ environments where instance-based registration of
	 * filters is supported.
	 * @param delegate the {@code Filter} instance that this proxy will delegate to and
	 * manage the lifecycle for (must not be {@code null}).
	 * @see #doFilter(ServletRequest, ServletResponse, FilterChain)
	 * @see #invokeDelegate(Filter, ServletRequest, ServletResponse, FilterChain)
	 * @see #destroy()
	 * @see #setEnvironment(org.springframework.core.env.Environment)
	 */
	public AppDelegatingFilterProxy(Filter delegate) {
		Assert.notNull(delegate, "delegate Filter object must not be null");
		this.delegate = delegate;
	}

	/**
	 * Create a new {@code DelegatingFilterProxy} that will retrieve the named target
	 * bean from the Spring {@code WebApplicationContext} found in the {@code ServletContext}
	 * (either the 'root' application context or the context named by
	 * {@link #setContextAttribute}).
	 * <p>For use in Servlet 3.0+ environments where instance-based registration of
	 * filters is supported.
	 * <p>The target bean must implement the standard Servlet Filter.
	 * @param targetBeanName name of the target filter bean to look up in the Spring
	 * application context (must not be {@code null}).
	 * @see #findWebApplicationContext()
	 * @see #setEnvironment(org.springframework.core.env.Environment)
	 */
	public AppDelegatingFilterProxy(String targetBeanName) {
		this(targetBeanName, null);
	}

	/**
	 * Create a new {@code DelegatingFilterProxy} that will retrieve the named target
	 * bean from the given Spring {@code WebApplicationContext}.
	 * <p>For use in Servlet 3.0+ environments where instance-based registration of
	 * filters is supported.
	 * <p>The target bean must implement the standard Servlet Filter interface.
	 * <p>The given {@code WebApplicationContext} may or may not be refreshed when passed
	 * in. If it has not, and if the context implements {@link org.springframework.context.ConfigurableApplicationContext},
	 * a {@link org.springframework.context.ConfigurableApplicationContext#refresh() refresh()} will be attempted before
	 * retrieving the named target bean.
	 * <p>This proxy's {@code Environment} will be inherited from the given
	 * {@code WebApplicationContext}.
	 * @param targetBeanName name of the target filter bean in the Spring application
	 * context (must not be {@code null}).
	 * @param wac the application context from which the target filter will be retrieved;
	 * if {@code null}, an application context will be looked up from {@code ServletContext}
	 * as a fallback.
	 * @see #findWebApplicationContext()
	 * @see #setEnvironment(org.springframework.core.env.Environment)
	 */
	public AppDelegatingFilterProxy(String targetBeanName, WebApplicationContext wac) {
		Assert.hasText(targetBeanName, "target Filter bean name must not be null or empty");
		this.setTargetBeanName(targetBeanName);
		this.webApplicationContext = wac;
		if (wac != null) {
			this.setEnvironment(wac.getEnvironment());
		}
	}

	/**
     * Set the name of the ServletContext attribute which should be used to retrieve the
     * {@link org.springframework.web.context.WebApplicationContext} from which to load the delegate {@link Filter} bean.
     */
    public void setContextAttribute(String contextAttribute) {
        this.contextAttribute = contextAttribute;
    }

    /**
     * Return the name of the ServletContext attribute which should be used to retrieve the
     * {@link org.springframework.web.context.WebApplicationContext} from which to load the delegate {@link Filter} bean.
     */
    public String getContextAttribute() {
        return this.contextAttribute;
    }

    /**
     * Set the name of the target bean in the Spring application context.
     * The target bean must implement the standard Servlet 2.3 Filter interface.
     * <p>By default, the <code>filter-name</code> as specified for the
     * DelegatingFilterProxy in <code>web.xml</code> will be used.
     */
    public void setTargetBeanName(String targetBeanName) {
        this.targetBeanName = targetBeanName;
    }

    /**
     * Return the name of the target bean in the Spring application context.
     */
    protected String getTargetBeanName() {
        return this.targetBeanName;
    }

    /**
     * Set whether to invoke the <code>Filter.init</code> and
     * <code>Filter.destroy</code> lifecycle methods on the target bean.
     * <p>Default is "false"; target beans usually rely on the Spring application
     * context for managing their lifecycle. Setting this flag to "true" means
     * that the servlet container will control the lifecycle of the target
     * Filter, with this proxy delegating the corresponding calls.
     */
    public void setTargetFilterLifecycle(boolean targetFilterLifecycle) {
        this.targetFilterLifecycle = targetFilterLifecycle;
    }

    /**
     * Return whether to invoke the <code>Filter.init</code> and
     * <code>Filter.destroy</code> lifecycle methods on the target bean.
     */
    protected boolean isTargetFilterLifecycle() {
        return this.targetFilterLifecycle;
    }


    @Override
    protected void initFilterBean() throws ServletException {

        // Fetch Spring root application context and initialize the delegate early,
        // if possible. If the root application context will be started after this
        // filter proxy, we'll have to resort to lazy initialization.
        synchronized (this.delegateMonitor) {
            if (getServletContext()!=null) {
                // If no target bean name specified, use filter name.
                if (this.targetBeanName == null) {
                    this.targetBeanName = getFilterName();
                }

                WebApplicationContext wac = findWebApplicationContext();
                if (wac != null) {
                    this.delegate = initDelegate(wac);
                }
            }
        }
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Lazily initialize the delegate if necessary.
        Filter delegateToUse = null;
        synchronized (this.delegateMonitor) {
            if (this.delegate == null) {
                WebApplicationContext wac = findWebApplicationContext();
                if (wac == null) {
                    throw new IllegalStateException("No WebApplicationContext found: no ContextLoaderListener registered?");
                }
                this.delegate = initDelegate(wac);
            }
            delegateToUse = this.delegate;
        }

        // Let the delegate perform the actual doFilter operation.
        invokeDelegate(delegateToUse, request, response, filterChain);
    }

    @Override
    public void destroy() {
        Filter delegateToUse = null;
        synchronized (this.delegateMonitor) {
            delegateToUse = this.delegate;
        }
        if (delegateToUse != null) {
            destroyDelegate(delegateToUse);
        }
    }


    /**
     * Retrieve a <code>WebApplicationContext</code> from the <code>ServletContext</code>
     * attribute with the {@link #setContextAttribute configured name}. The
     * <code>WebApplicationContext</code> must have already been loaded and stored in the
     * <code>ServletContext</code> before this filter gets initialized (or invoked).
     * <p>Subclasses may override this method to provide a different
     * <code>WebApplicationContext</code> retrieval strategy.
     * @return the WebApplicationContext for this proxy, or <code>null</code> if not found
     * @see #getContextAttribute()
     */
    protected WebApplicationContext findWebApplicationContext() {
		if (this.webApplicationContext != null) {
			// the user has injected a context at construction time -> use it
			if (this.webApplicationContext instanceof ConfigurableApplicationContext) {
				if (!((ConfigurableApplicationContext)this.webApplicationContext).isActive()) {
					// the context has not yet been refreshed -> do so before returning it
					((ConfigurableApplicationContext)this.webApplicationContext).refresh();
				}
			}
			return this.webApplicationContext;
		}
        String attrName = getContextAttribute();
        if (attrName != null) {
            return WebApplicationContextUtils.getWebApplicationContext(getServletContext(), attrName);
        }
        else {
            return WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        }
    }

    /**
     * Initialize the Filter delegate, defined as bean the given Spring
     * application context.
     * <p>The default implementation fetches the bean from the application context
     * and calls the standard <code>Filter.init</code> method on it, passing
     * in the FilterConfig of this Filter proxy.
     * @param wac the root application context
     * @return the initialized delegate Filter
     * @throws ServletException if thrown by the Filter
     * @see #getTargetBeanName()
     * @see #isTargetFilterLifecycle()
     * @see #getFilterConfig()
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    protected Filter initDelegate(WebApplicationContext wac) throws ServletException {
        Filter delegate = wac.getBean(getTargetBeanName(), Filter.class);
        if (isTargetFilterLifecycle()) {
            delegate.init(getFilterConfig());
        }
        return delegate;
    }

    /**
     * Actually invoke the delegate Filter with the given request and response.
     * @param delegate the delegate Filter
     * @param request the current HTTP request
     * @param response the current HTTP response
     * @param filterChain the current FilterChain
     * @throws ServletException if thrown by the Filter
     * @throws java.io.IOException if thrown by the Filter
     */
    protected void invokeDelegate(
            Filter delegate, ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        delegate.doFilter(request, response, filterChain);
    }

    /**
     * Destroy the Filter delegate.
     * Default implementation simply calls <code>Filter.destroy</code> on it.
     * @param delegate the Filter delegate (never <code>null</code>)
     * @see #isTargetFilterLifecycle()
     * @see javax.servlet.Filter#destroy()
     */
    protected void destroyDelegate(Filter delegate) {
        if (isTargetFilterLifecycle()) {
            delegate.destroy();
        }
    }
}
