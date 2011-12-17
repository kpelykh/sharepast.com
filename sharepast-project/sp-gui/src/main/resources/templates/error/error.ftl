<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>Error - ${layout.title}</title>
    <link rel="stylesheet" type="text/css" media="all" href="/css/404.css" />
</head>
<body lang="en">
<div id="layout">
    <div id="logo"><a href="/"><img alt="Ouch!" title="Ouch!" src="/img/404_logo.gif" /></a></div>
    <div id="content">

        <#if error??>
            <div id="errorExplanation" class="errorExplanation">
                <ul>
                    <#list error as errorItem>
                        <li>${errorItem}</li>
                    </#list>
                </ul>
            </div>
        </#if>

        <p><a href="/">${methods.nlsFor("error.404.main.page")}</a></p>
    </div>
</div>
</body>
</html>

