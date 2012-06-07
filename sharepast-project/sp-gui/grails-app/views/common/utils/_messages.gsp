<g:if test="${flash.message}">
    <div id="message" class="alert alert-info">
        <g:message code="${flash.message}"/>
    </div>
</g:if>
<g:if test="${flash.success}">
    <div id="message" class="alert alert-success">
        <g:message code="${flash.success}"/>
    </div>
</g:if>
<g:if test="${message}">
    <div id="message" class="alert alert-info">
        <g:message code="${message}"/>
    </div>
</g:if>
<g:if test="${error}">
    <div id="errors" class="alert alert-error">
        <g:message code="${error}"/>
    </div>
</g:if>
<g:if test="${bean} && ${field}">
    <g:hasErrors bean="${bean}" field="${field}">
        <g:eachError field="${field}" bean="${bean}">
            <g:message error="${it}"/>
        </g:eachError>
    </g:hasErrors>
</g:if>
<g:elseif test="${bean}">
    <g:hasErrors bean="${bean}">
        <div id="errors" class="errors">
            <g:renderErrors bean="${bean}"></g:renderErrors>
        </div>
    </g:hasErrors>
</g:elseif>