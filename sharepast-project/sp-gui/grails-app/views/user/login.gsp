
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