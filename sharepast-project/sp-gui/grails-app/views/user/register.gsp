
<!DOCTYPE html>
<html lang="en">
<head>
    <meta content="main" name="layout"/>
    <title><g:message code="login.title"/></title>
    <r:require modules="login"/>
</head>

<body>

<article class="container">


    <article class="container">

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

            <form id="auth-form" class="clearfix" autocomplete="off" method="POST" action="/signup"><div style="display:none"><input type="hidden" value="0728c600cd03ebdf0cd35ea8c59e592f" name="csrfmiddlewaretoken"></div>
                <h3>or with email</h3>
                <div class="all-errors">

                </div>
                <div class="input">
                    <input type="text" maxlength="30" name="username" placeholder="Username" id="id_username">

                </div>
                <div class="input">
                    <input type="text" maxlength="75" name="email" placeholder="Email" id="id_email">

                    <div class="email_suggestion"></div>
                </div>
                <div class="input">
                    <input type="password" id="id_password" name="password" placeholder="Password">

                </div>

                <div class="actions">
                    <input type="submit" class="btn btn-green" value="Create account">
                </div>
            </form>
        </div>

        <p class="note">Already have an account? <g:link controller="user" action="login">Log in</g:link></p>


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