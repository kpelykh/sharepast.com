%{--
<ul class="nav">
    <#if util.isAuthenticated>
        <li <#if tab == "home">class="active"</#if>><a href="/app/home">Home</a></li>
        <li <#if tab == "profile">class="active"</#if>><a href="${util.getUserProfileUrl(user)}">Profile</a></li>
    </#if>
</ul>
<#if util.isAuthenticated>
    <p class="pull-right">Logged in as <a href="${util.getUserProfileUrl(user)}">${user.username}</a> | <a href="/logout">Logout</a></p>
    <#elseif util.isRemembered && !util.isAuthenticated>
        <p class="pull-right"><a href="${util.getUserProfileUrl(user)}">${user.username}</a> <a href="/logout">(Not you?)</a></p>
        <#else>
            <p class="pull-right">
                <a href="/users/new">Register</a> |
                <a href="/login">Login</a>
            </p>
</#if>
--}%

<div class="nav-collapse">
    <ul class="nav">
        <li class="active"><a href="#">Home</a></li>
        <li><a href="#about">About</a></li>
        <li><a href="#contact">Contact</a></li>
    </ul>
</div><!--/.nav-collapse -->