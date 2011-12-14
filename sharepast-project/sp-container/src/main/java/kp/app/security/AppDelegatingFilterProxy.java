package kp.app.security;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.*;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 3/13/11
 * Time: 10:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class AppDelegatingFilterProxy extends GenericFilterBean {

    private String contextAttribute;

	private String targetBeanName;

	private boolean targetFilterLifecycle = false;

	private Filter delegate;

	private final Object delegateMonitor = new Object();


	/**
	 * Set the name of the ServletContext attribute which should be used to retrieve the
	 * {@link WebApplicationContext} from which to load the delegate {@link Filter} bean.
	 */
	public void setContextAttribute(String contextAttribute) {
		this.contextAttribute = contextAttribute;
	}

	/**
	 * Return the name of the ServletContext attribute which should be used to retrieve the
	 * {@link WebApplicationContext} from which to load the delegate {@link Filter} bean.
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
	 * @throws IOException if thrown by the Filter
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
