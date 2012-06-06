/* Copyright 2006-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package grails.plugins.springsecurity

import org.springframework.expression.Expression
import org.springframework.security.access.expression.ExpressionUtils
import org.springframework.security.web.FilterInvocation

import javax.servlet.FilterChain
import com.sharepast.service.Subject

/**
 * Security tags.
 *
 */
class SecurityTagLib {

	static namespace = 'sec'


	/**
	 * Renders the body if any of the specified roles are granted to the user. Roles are
	 * specified in the 'roles' attribute which is a comma-delimited string.
	 *
	 * @attr roles REQUIRED the role name(s)
	 */
	def ifAnyGranted = { attrs, body ->

		String roles = assertAttribute('roles', attrs, 'ifAnyGranted')

		if (Subject.hasAnyRole(roles)) {
			out << body()
		}
	}

	/**
	 * Renders a property (specified by the 'field' attribute) from the principal.
	 *
	 * @attr field REQUIRED the field name
	 */
	def loggedInUserInfo = { attrs, body ->


		def source
		if (Subject.isAuthenticated()) {
            source = Subject.currentUser.toString()
		}

		if (source) {
			out << source.encodeAsHTML()
		}
		else {
			out << body()
		}
	}

    /**
     * This tag only writes its body to the output if the current user
     * is remembered from a previous session (via the "remember me"
     * cookie) but not currently logged in.
     */
    def remembered = { attrs, body ->
        if (Subject.isAuthenticated() && Subject.isRememberMe()) {
            out << body()
        }
    }

    /**
     * <p>Outputs the string form of the current user's identity. By default
     * this assumes that the subject has only one principal; its string
     * representation is written to the page.</p>
     * Optional attributes:
     * <ul>
     * <li><i>type</i>: Species the type or class of the principal to
     * use</li>
     * <li><i>property</i>: Specifies the name of the property on the
     * principal to use as the string representation, e.g.
     * <code>firstName</code></li>
     * </ul>
     */
    def principal = { attrs ->
        def subject = Subject.currentUser

        if (subject != null) {
            // If a 'property' attribute has been specified, then
            // we access the property with the same name on the
            // principal and write that to the page. Otherwise, we
            // just use the string representation of the principal
            // itself.
            if (attrs["property"]) {
                out << subject."${attrs['property']}".toString().encodeAsHTML()
            }
            else {
                out << subject.username.encodeAsHTML()
            }
        }
    }

	/**
	 * Renders the user's username if logged in.
	 */
	def username = { attrs ->
		if (Subject.isAuthenticated()) {
			out << Subject.currentUser.username.encodeAsHTML()
		}
	}

	/**
	 * Renders the body if the user is authenticated.
	 */
	def isLoggedIn = { attrs, body ->
        if (Subject.isAuthenticated()) {
			out << body()
		}
	}

	/**
	 * Renders the body if the user is not authenticated.
	 */
	def isNotLoggedIn = { attrs, body ->
        if (!Subject.isAuthenticated()) {
			out << body()
		}
	}

	/**
	 * Renders the body if the specified expression (a String; the 'expression' attribute)
	 * evaluates to <code>true</code> or if the specified URL is allowed.
	 *
	 * @attr expression the expression to evaluate
	 * @attr url the URL to check
	 * @attr method the method of the URL, defaults to 'GET'
	 */
	def access = { attrs, body ->
		if (hasAccess(attrs, 'access')) {
			out << body()
		}
	}

	/**
	 * Renders the body if the specified expression (a String; the 'expression' attribute)
	 * evaluates to <code>false</code> or if the specified URL is not allowed.
	 *
	 * @attr expression the expression to evaluate
	 * @attr url the URL to check
	 * @attr method the method of the URL, defaults to 'GET'
	 */
	def noAccess = { attrs, body ->
		if (!hasAccess(attrs, 'noAccess')) {
			out << body()
		}
	}

	protected boolean hasAccess(attrs, String tagName) {

		if (!Subject.isAuthenticated()) {
			return false
		}

		String expressionText = attrs.remove('expression')
        if (expressionText) {
            return Subject.hasAccess(expressionText)
		}

		String url = attrs.remove('url')
		if (!url) {
			String controller = attrs.remove('controller')
			String mapping = attrs.remove('mapping')
			if (!controller && !mapping) {
				throwTagError "Tag [$tagName] requires an expression, a URL, or controller/action/mapping attributes to create a URL"
			}
			if (mapping) {
				url = g.createLink(mapping: mapping).toString()
			}
			else {
				String action = attrs.remove('action')
				url = g.createLink(controller: controller, action: action, base: '/').toString()
			}
		}

		String method = attrs.remove('method') ?: 'GET'
		return Subject.isAllowedUrl(request.contextPath, url, method)
	}

	protected assertAttribute(String name, attrs, String tag) {
		if (!attrs.containsKey(name)) {
			throwTagError "Tag [$tag] is missing required attribute [$name]"
		}
		attrs.remove name
	}

}
