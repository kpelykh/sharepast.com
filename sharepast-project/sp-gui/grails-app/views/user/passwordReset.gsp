
<!DOCTYPE html>
<html lang="en">
<head>
    <meta content="home" name="layout"/>
    <title><g:message code="passwordReset.title"/></title>
    <r:require modules="login"/>
</head>

<body>

<article class="container">

    <div id="login" class="accounts-form">
        <h3>Reset password</h3>
        <p>Forgotten your password or username? Enter your email address below, and we'll send instructions for setting a new one.</p>
        <g:form name="auth-form" method="POST" action="passwordReset">

            <div class="input">
                <input type="text" placeholder="Email" maxlength="75" name="email" id="id_email"></div>

            <g:if test="${flash.message}">
                <ul class="errorlist">
                    ${flash.message}
                </ul>
            </g:if>



            <input type="submit" class="btn" value="Reset password">
        </g:form>
    </div>

</article>

</body>
</html>