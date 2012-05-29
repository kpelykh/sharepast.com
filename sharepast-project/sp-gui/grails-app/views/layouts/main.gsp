<%@ page import="grails.util.Environment" %>
<!DOCTYPE html>
<html lang="en">
	<head>
        <title><g:message code="app.name" default="SharePast"/> - <g:layoutTitle/></title>

        <meta name="viewport" content="width=device-width, initial-scale=1.0">
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="description" content="">
        <meta name="author" content="">

        <r:require modules="master"/>
        <r:external uri="/images/favicon.ico"/>
        <r:layoutResources/>

        <g:if test="${(Environment.current == Environment.PRODUCTION)}">
            <link type="text/css" href="${resource(dir: 'css', file: 'bootstrap.css')}" />
        </g:if>
        <g:else>
            <link rel="stylesheet/less" type="text/css" href="${resource(dir: 'less', file: 'bootstrap.less')}" />
            <g:javascript src="lib/less-1.3.0.min.js" />
        </g:else>

        <!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->
        <!--[if lt IE 9]>
            <g:javascript src="http://html5shim.googlecode.com/svn/trunk/html5.js"/>
        <![endif]-->

        <style>
            body {
                padding-top: 60px; /* 60px to make the container go all the way to the bottom of the topbar */
            }
        </style>

        <g:layoutHead />
	</head>
	<body>

    <div class="navbar navbar-fixed-top">
        <div class="navbar-inner">
            <div class="container">
                <g:render template="/content/logos" />
                <g:render template="/content/mainMenuBar" />
            </div>
        </div>
    </div>

    <div class="container">
        <g:layoutBody />
        <g:render template="/content/footer" />
    </div> <!-- /container -->

    <%-- Google Analytics --%>
    <g:render template="/content/analytics" />

    <r:layoutResources/>
  </body>
</html>