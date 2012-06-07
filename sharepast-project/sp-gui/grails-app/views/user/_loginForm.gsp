<div class="accounts-form" id="login">
    <h2>Log in to your account</h2>

    <g:form name="auth-form" url="/j_spring_security_check">
        <g:render template="/common/utils/messages" model="${pageScope.getVariables()}" />

        <fieldset>

           <div class="input">
                <g:textField type="text" placeholder="Username or email" maxlength="75" name="j_username" id="j_username" value="${username}"/>
            </div>
            <div class="input">
                <g:field type="password" placeholder="Password" id="j_password" name="j_password" />
            </div>

            <div class="form-actions">
                <g:submitButton name="Submit" class="btn btn-green" value="Log In" />
            </div>
        </fieldset>
    </g:form>

</div>
<p class="note"><g:link controller="user" action="passwordReset">Forgot your password?</g:link> · Need an account? <g:link controller="user" action="register">Sign up</g:link> </p>
