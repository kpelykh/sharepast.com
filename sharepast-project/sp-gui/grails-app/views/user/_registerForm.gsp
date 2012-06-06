<div class="accounts-form" id="signup">
    <h2>Create your free account</h2>
    <hr class="small">

    <div class="connect-buttons">

        <form id="twitter-connect-form" action="/social/twitter/redirect/" method="post" name="login" class="connect-button">
            <div style="display:none"><input type="hidden" value="0728c600cd03ebdf0cd35ea8c59e592f" name="csrfmiddlewaretoken"></div>

            <a id="twitter_button" class="btn" onclick="$('#twitter-connect-form').submit(); return false;" href="#"><span>Sign up with <strong>Twitter</strong></span></a>
        </form>

        <form id="facebook-connect-form" action="/social/facebook/redirect/" method="post" name="login" class="connect-button">
            <div style="display:none"><input type="hidden" value="0728c600cd03ebdf0cd35ea8c59e592f" name="csrfmiddlewaretoken"></div>

            <a id="facebook_button" class="btn" onclick="$('#facebook-connect-form').submit(); return false;" href="#"><span>Sign up with <strong>Facebook</strong></span></a>
        </form>
        <p><small>We won't ever post updates without your action</small></p>
    </div>

    <h3>or with email</h3>

    <div class="all-errors">
        <g:render template="/common/messages" model="${pageScope.getVariables()}" />
    </div>

    <g:set var="formBody">
        <div class="input">
            <input type="text" maxlength="30" name="username" placeholder="Username" id="id_username" value="${fieldValue(bean:user,field:'username')}">
            <g:hasErrors bean="${user}" field="username">
                <ul>
                    <g:eachError field="username" bean="${user}">
                        <li><g:message error="${it}"/></li>
                    </g:eachError>
                </ul>
            </g:hasErrors>
        </div>
        <div class="input">
            <input type="text" maxlength="75" name="email" placeholder="Email" id="id_email" value="${fieldValue(bean:user,field:'email')}">
            <g:hasErrors bean="${user}" field="email">
                <ul>
                    <g:eachError field="email" bean="${user}">
                        <li><g:message error="${it}"/></li>
                    </g:eachError>
                </ul>
            </g:hasErrors>
        </div>
        <div class="input">
            <input type="password" id="id_password" name="password" placeholder="Password">
            <g:hasErrors bean="${user}" field="password">
                <ul>
                    <g:eachError field="password" bean="${user}">
                        <li><g:message error="${it}"/></li>
                    </g:eachError>
                </ul>
            </g:hasErrors>
        </div>
        <div class="input">
            <input type="password" id="id_password2" name="password2" placeholder="Password2">
            <g:hasErrors bean="${user}" field="password2">
                <ul>
                    <g:eachError field="password2" bean="${user}">
                        <li><g:message error="${it}"/></li>
                    </g:eachError>
                </ul>
            </g:hasErrors>
        </div>

        <div class="actions">
            <input type="submit" class="btn btn-green" value="Create account">
        </div>
    </g:set>


    <g:if test="${true == async}">
        <g:formRemote name="auth-form" url="[controller:'user',action:'register']" update="contentPane" class="clearfix">
            ${formBody}
        </g:formRemote>
    </g:if>
    <g:else>
        <g:form name="auth-form" url="[controller:'user',action:'register']" update="contentPane" class="clearfix">
            ${formBody}
        </g:form>
    </g:else>

</div>