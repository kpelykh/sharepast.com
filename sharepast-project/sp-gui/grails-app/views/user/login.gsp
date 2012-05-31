
<!DOCTYPE html>
<html lang="en">
<head>
    <meta content="main" name="layout"/>
    <title><g:message code="login.title"/></title>
    <r:require modules="login"/>
</head>

<body>

<article class="container">

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

        <form id="auth-form" method="POST" action="/login"><div style="display:none"><input type="hidden" value="0728c600cd03ebdf0cd35ea8c59e592f" name="csrfmiddlewaretoken"></div>



            <div class="all-errors">

            </div>
            <div class="input">
                <input type="text" placeholder="Username or email" maxlength="75" name="username" id="id_username">

            </div>
            <div class="input">
                <input type="password" placeholder="Password" id="id_password" name="password">

            </div>
            <div class="actions clearfix">
                <input type="submit" class="btn btn-green" value="Log In">

            </div>
        </form>

    </div>
    <p class="note"><g:link controller="user" action="passwordReset">Forgot your password?</g:link> · Need an account? <g:link controller="user" action="register">Sign up</g:link> </p>


    <footer>
        <p>
            <a href="/about">About us</a> ·
            <a href="mailto:hello@kippt.com">Contact</a> ·
            <a target="_blank" href="http://blog.kippt.com">Blog</a>
        </p>
        <p>&copy; 2011-2012 Kippt. Designed and developed in Helsinki, Finland.</p>

        <g:render template="/content/follow-buttons2"/>

    </footer>

</article>

</body>
</html>