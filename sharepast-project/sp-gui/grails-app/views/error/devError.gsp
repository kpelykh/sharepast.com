<html>
	<head>
		<title><g:message code="error.500.title"/></title>
        <meta content="error" name="layout"/>
	</head>
	<body>
    <h1>Grails Runtime Exception</h1>
    <h2>Error Details</h2>
    <div class="message">
        <strong>Message:</strong> ${exception.message?.encodeAsHTML()} <br />
        <strong>Caused by:</strong> ${exception.cause?.message?.encodeAsHTML()} <br />
        <strong>Class:</strong> ${exception.className} <br />
        <strong>At Line:</strong> [${exception.lineNumber}] <br />
        <strong>Code Snippet:</strong><br />
        <div class="snippet">
            <g:each var="cs" in="${exception.codeSnippet}">
                ${cs?.encodeAsHTML()}<br />
            </g:each>
        </div>
    </div>
    <h2>Stack Trace</h2>
    <div class="stack">
        <pre><g:each in="${exception.stackTraceLines}">${it.encodeAsHTML()}<br/></g:each></pre>
    </div>
    </body>
</html>
