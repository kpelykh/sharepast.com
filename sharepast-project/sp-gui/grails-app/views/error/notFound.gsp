<!DOCTYPE html>
<html lang="en">
<head>
    <title><g:message code="error.404.title" default="404 - Page not found"/></title>
    <meta content="error" name="layout" />
    <style type="text/css">
        html, body {
            height: 100%;
            margin: 0;
            padding: 0;
            width: 100%;
        }
        body {
            background-color: #FFFFFF;
            color: #000000;
            font: 1.125em Arial,Helvetica,sans-serif;
        }
        img {
            border: 0 none;
        }
        #layout {
            height: 100%;
            margin: auto;
            max-width: 1600px;
            min-height: 540px;
            min-width: 950px;
            position: relative;
        }
        #logo {
            font-size: 4em;
            left: 2%;
            position: absolute;
            top: 30px;
        }
        #logo img {
            margin-left: 6px;
        }
        #content {
            background: url("/images/404_bucket.jpg") no-repeat scroll 0 0 transparent;
            left: 46%;
            margin: -85px 0 0 -320px;
            min-height: 374px;
            padding: 85px 0 0 320px;
            position: absolute;
            top: 37%;
            white-space: nowrap;
        }
        h1 {
            font-size: 2.67em;
            font-weight: normal;
            margin: 0 0 0.5em;
        }
        a {
            color: #1C4ACF;
        }
    </style>
</head>
<body>
<div id="layout">
    <div id="logo"><a href="${createLink(controller:'home',action:'index')}">
        <img src="${resource(dir: 'images', file: '404_logo.gif')}"/></a>
    </div>
    <div id="content">
        <h1><g:message code="error.404.description" default="Page not found"/></h1>

        <p><g:message code="error.404.message" default="Page not found"/></p>
        <p><g:message code="error.404.main.page" default="Main"/></p>
    </div>
</div>
</body>
</html>