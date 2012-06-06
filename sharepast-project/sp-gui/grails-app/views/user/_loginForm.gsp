<div class="accounts-form" id="login">
    <h2>Log in to your account</h2>
    <hr class="small">

    <div class="connect-buttons">
        <form id="twitter-connect-form" action="/social/twitter/redirect/" method="post" name="login" class="connect-button">
            <div style="display:none"><input type="hidden" value="0728c600cd03ebdf0cd35ea8c59e592f" name="csrfmiddlewaretoken"></div>

            <a id="twitter_button" class="btn" onclick="$('#twitter-connect-form').submit(); return false;" href="#"><span>Log in with <strong>Twitter</strong></span></a>
        </form>

        <form id="facebook-connect-form" action="/social/facebook/redirect/" method="post" name="login" class="connect-button">
            <div style="display:none"><input type="hidden" value="0728c600cd03ebdf0cd35ea8c59e592f" name="csrfmiddlewaretoken"></div>

            <a id="facebook_button" class="btn" onclick="$('#facebook-connect-form').submit(); return false;" href="#"><span>Log in with <strong>Facebook</strong></span></a>
        </form>
    </div>

    <form id="auth-form" method="POST" action="/j_spring_security_check">
        <div class="all-errors">
            <g:render template="/common/messages" model="${pageScope.getVariables()}" />
        </div>
        <div class="input">
            <g:textField type="text" placeholder="Username or email" maxlength="75" name="j_username" id="j_username" value="${username}"/>
        </div>
        <div class="input">
            <g:field type="password" placeholder="Password" id="j_password" name="j_password" />
        </div>

        <div class="actions clearfix">
            <g:submitButton name="Submit" class="btn btn-green" value="Log In" />
        </div>
    </form>

</div>
<p class="note"><g:link controller="user" action="passwordReset">Forgot your password?</g:link> · Need an account? <g:link controller="user" action="register">Sign up</g:link> </p>
