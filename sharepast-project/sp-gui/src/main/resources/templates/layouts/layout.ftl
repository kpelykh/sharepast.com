<#import "../libs/util.ftl" as util>

<#-- variables, override these in the page -->
<#assign title>Companion.ly</#assign>
<#-- meta tags -->
<#assign meta></#assign>
<#assign link></#assign>
<#assign meta_title>Companion.ly</#assign>
<#assign meta_description>companion</#assign>
<#assign meta_keywords>meet, invite, together, go, company, companion</#assign>

<#-- JS that goes at the end of the body -->
<#-- use layout.addBodyJS to add to it -->
<!-- here goes js which needs to run on every page -->
<#assign bodyJS >
</#assign>


<#-- head settings and tags common for pages -->
<#macro htmlHead>
<!DOCTYPE HTML>

<!--[if lt IE 7 ]> <html class="ie ie6 no-js" lang="en"> <![endif]-->
<!--[if IE 7 ]>    <html class="ie ie7 no-js" lang="en"> <![endif]-->
<!--[if IE 8 ]>    <html class="ie ie8 no-js" lang="en"> <![endif]-->
<!--[if IE 9 ]>    <html class="ie ie9 no-js" lang="en"> <![endif]-->
<!--[if gt IE 9]><!-->
<html class="no-js" lang="en"><!--<![endif]-->
<!-- the "no-js" class is for Modernizr. -->

<head id="www-companion-ly" data-template-set="html5-reset">

    <meta charset="utf-8">

    <!-- Always force latest IE rendering engine (even in intranet) & Chrome Frame -->
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">

    <title>${title}</title>

    <meta name="title" content="${meta_title?js_string}">
    <meta name="description" content="${meta_description?html}">
    <!-- Google will often use this as its description of your page/site. Make it good. -->
    <meta name="author" content="Konstantin Pelykh">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="keywords" content="${meta_keywords?js_string}"/>
${meta}

    <link href="/favicon.ico" rel="shortcut icon">
    <link href="/css/style.css" rel="stylesheet">
${link}

    <!-- all our JS is at the bottom of the page, except for Modernizr. -->
    <script src="/js/libs/modernizr-2.0.6.js"></script>
</head>
</#macro>


<#-- Base Layout -->
<#macro defaultlayout header="">
<@htmlHead />

<body>

<section id="login-register">
    <div class="text-links">
        <#if util.isAuthenticated>
            Logged in as
            <a href="${util.getUserProfileUrl(user)}">${user.username}</a> |
            <a href="/logout">Logout</a>
       <#elseif util.isRemembered && !util.isAuthenticated>
            Hi,
            <a href="${util.getUserProfileUrl(user)}">${user.username}</a>
            <a href="/logout">(Not you?)</a>
        <#else>
            <a href="/users/new">Register</a> |
            <a href="/login">Login</a>
        </#if>
    </div>
    <a href="http://www.twitter.com/companionly" target="_blank">
        <img alt="Twitter-icon" border="0" class="twitter-icon" src="/img/twitter-icon.png"/>
    </a>
</section>

<nav class="top-nav">
    <a href="/" class="companionly-logo"></a>

    <ul>
        <li><a href="/app/home" class="active">Home</a></li>
        <li><a href="/about">About</a></li>

        <li><a href="/contact">Contact</a></li>
    </ul>
</nav>

    <#nested/>

<footer>
    <a href="/pages/privacy">Privacy policy</a> &#8226;
    <a href="/pages/terms">Terms and conditions</a>
</footer>

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
<#--</#compress>-->
</#macro>


<#macro addBodyJS>
  <#assign bodyJS>
    ${bodyJS}
    <#nested>
  </#assign>
</#macro>
