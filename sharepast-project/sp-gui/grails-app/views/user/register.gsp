<!DOCTYPE html>
<html lang="en">
<head>
    <meta content="home" name="layout"/>
    <title><g:message code="login.title"/></title>
    <r:require modules="user"/>
</head>

<body>

    <article class="container">
        <g:render template="registerForm" model="${pageScope.getVariables()}" />
        <p class="note">Already have an account? <g:link controller="user" action="login">Log in</g:link></p>
    </article>

</body>
</html>