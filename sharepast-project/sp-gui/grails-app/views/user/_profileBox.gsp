<sec:isLoggedIn>
    <span class="username"><sec:principal property="username"/></span>
</sec:isLoggedIn>
<sec:isNotLoggedIn>
    <ul class="nav actions">
        <li><g:link controller="user" action="login">Log In</g:link></li>
        <li><g:link controller="user" action="register">Sign Up</g:link></li>
    </ul>
</sec:isNotLoggedIn>
