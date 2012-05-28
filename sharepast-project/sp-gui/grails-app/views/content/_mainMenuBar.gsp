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

<ul id="mainMenuBar">
	
    <li><a href="http://www.springsource.com/products/grails">Products</a></li>
    <li><a href="http://www.springsource.com/groovy-grails-consulting">Services</a></li>
    <li><a href="http://www.springsource.com/training/grv001">Training</a></li>
</ul><!-- mainMenuBar -->
