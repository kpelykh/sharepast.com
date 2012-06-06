<%@ page import="grails.util.Environment" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <title><g:message code="app.name" default="SharePast"/> - <g:layoutTitle/></title>

        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link type="text/css" href="${resource(file: 'humans.txt')}"/>

        <r:require modules="app"/>
        <r:external uri="/images/favicon.ico"/>

        <g:if test="${(Environment.current == Environment.PRODUCTION)}">
            <link type="text/css" rel="stylesheet" href="${resource(dir: 'css', file: 'bootstrap.css')}"/>
        </g:if>
        <g:else>
            <link rel="stylesheet/less" type="text/css" href="/less/bootstrap.less" />
            <g:javascript src="/lib/less-1.3.0.min.js"/>
        </g:else>

        <r:layoutResources/>

    <!--[if lt IE 9]>
    <g:javascript src="http://html5shim.googlecode.com/svn/trunk/html5.js"/>
    <![endif]-->

        <style>
        body {
            padding-top: 60px; /* 60px to make the container go all the way to the bottom of the topbar */
        }
        </style>

        <g:layoutHead/>
    </head>

    <body>

        <div class="navbar">
            <div class="navbar-inner">
                <div class="container">
                    <g:render template="/app/navbar"/>
                </div>
            </div>
        </div>

        <div class="container">
            <g:layoutBody/>
            <footer>
                <p>&copy; Company 2012</p>
            </footer>
        </div>

        <r:layoutResources/>
    </body>
</html>