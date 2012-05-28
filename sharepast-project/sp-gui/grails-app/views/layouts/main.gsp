<!DOCTYPE html>
<!--[if lt IE 7 ]> <html class="ie6"> <![endif]-->
<!--[if IE 7 ]> <html class="ie7"> <![endif]-->
<!--[if IE 8 ]> <html class="ie8"> <![endif]-->
<!--[if IE 9 ]> <html class="ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!-->
<html class=""> <!--<![endif]-->
	<head>
        <title><g:message code="app.name" default="SharePast"/> - <g:layoutTitle/></title>

		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

        <r:require modules="master"/>
        <r:external uri="/images/favicon.ico"/>
        <r:layoutResources/>

        <!-- Le HTML5 shim, for IE6-8 support of HTML elements -->
        <!--[if lt IE 9]>
        <r:script src="http://html5shim.googlecode.com/svn/trunk/html5.js"/>
        <![endif]-->

        <r:script>
            function addJsClass() {
                var classes = document.body.className.split(" ");
                if (classes.length == 1 && classes[0] == "") {
                    classes = ["js"];
                }
                else {
                    classes.push("js");
                }
                document.body.className = classes.join(" ");
            }
        </r:script>

        <%-- Page-specific CSS goes in here --%>
        <g:pageProperty name="page.pageCss" />
        <g:layoutHead />
	</head>
	<body>

    <div class="topbar">
        <div class="fill">
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