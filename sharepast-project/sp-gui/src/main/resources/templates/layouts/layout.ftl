<#import "../libs/util.ftl" as util>

<#-- variables, override these in the page -->
<#assign title>sharepast.com - share the moments of your past!</#assign>
<#-- meta tags -->
<#assign meta></#assign>
<#assign link></#assign>
<#assign meta_title>sharepast.com</#assign>
<#assign meta_description>Share your past events, upload videos and photos, discuss them with your friends. Share your moments!</#assign>
<#assign meta_keywords>memory, share, photo, friends, past, events</#assign>

<#-- JS that goes at the end of the body -->
<#-- use layout.addBodyJS to add to it -->
<!-- here goes js which needs to run on every page -->
<#assign bodyJS >
</#assign>

<#-- head settings and tags common for pages -->
<#macro htmlHead>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>${title}</title>
    <meta name="description" content="${meta_description?html}">
    <meta name="author" content="Konstantin Pelykh">
    <meta name="title" content="${meta_title?js_string}">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="keywords" content="${meta_keywords?js_string}"/>
${meta}

    <!-- Le HTML5 shim, for IE6-8 support of HTML elements -->
    <!--[if lt IE 9]>
    <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->

<#if config.isProduction>
    <link href="/css/bootstrap.css" rel="/css/bootstrap.css">
<#else>
    <link rel="stylesheet/less" href="/less/bootstrap.less">
    <script src="/js/libs/less-1.1.5.min.js"></script>
</#if>

    <link rel="shortcut icon" href="/favicon.ico">
    <link rel="stylesheet" href="/css/style.css">
${link}
</head>
</#macro>


<#-- Base Layout -->
<#macro defaultlayout header="" tab="">
<@htmlHead />

<body>

<div class="topbar">
    <div class="fill">
        <div class="container">
            <a class="brand" href="/">sharepast</a>

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
        </div>
    </div>
</div>

<div class="container">
    <#nested/>

    <footer>
        <p>&copy; Sharepast 2011</p>

        <p>

            <a href="/pages/privacy">Privacy policy</a> |
            <a href="/pages/terms">Terms and conditions</a> |
            <a href="/about">About</a> |
            <a href="/contact">Contact</a>
        </p>

        <#if !config.isProduction>
            <p> ${build.version} ${build.timestamp} </p>
        </#if>

    </footer>

</div> <!-- /container -->


<!-- here comes the javascript -->

<!-- Grab Google CDN's jQuery. fall back to local if necessary -->
<script src="//ajax.googleapis.com/ajax/libs/jquery/1.6.2/jquery.min.js"></script>
<script>window.jQuery || document.write("<script src='/js/libs/jquery-1.6.2.min.js'>\x3C/script>")</script>

<!-- scripts concatenated and minified via ant build script-->
  <script defer src="/js/plugins.js"></script>
  <script defer src="/js/script.js"></script>
<!-- end scripts-->

  <script type="text/javascript">
      ${bodyJS}
  </script>

<#--<script>

    var _gaq = _gaq || [];
    _gaq.push(['_setAccount', 'UA-4813257-2']);
    _gaq.push(['_trackPageview']);

    (function() {
        var ga = document.createElement('script');
        ga.type = 'text/javascript';
        ga.async = true;
        ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
        var s = document.getElementsByTagName('script')[0];
        s.parentNode.insertBefore(ga, s);
    })();

</script>-->

</body>
</html>
</#macro>


<#macro addBodyJS>
  <#assign bodyJS>
    ${bodyJS}
    <#nested>
  </#assign>
</#macro>
