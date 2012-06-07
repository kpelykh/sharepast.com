
<!DOCTYPE html>
<html lang="en">
<head>
    <meta content="home" name="layout"/>
    <title><g:message code="login.title"/></title>
    <r:require modules="user"/>
</head>

<body>

<article class="container">

    <g:render template="loginForm" model="${pageScope.variables}" />

</article>

</body>
</html>