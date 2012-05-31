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

<a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
    <span class="icon-bar"></span>
    <span class="icon-bar"></span>
    <span class="icon-bar"></span>
</a>
<g:render template="/content/logos" />
<div class="nav-collapse">
  <ul class="nav actions">
      <li><g:link controller="user" action="login">Log In</g:link></li>
      <li><g:link controller="user" action="register">Sign Up</g:link></li>
  </ul>

  <ul class="nav">
        <li class="${(tab == 'index')  ? 'active' : ''}"><a href="index.html">Test</a></li>
        <li><a href="about/index.html">About us</a></li>
  </ul>
</div><!--/.nav-collapse -->