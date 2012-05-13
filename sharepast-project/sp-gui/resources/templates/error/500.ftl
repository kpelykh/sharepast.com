<#ftl strip_text="true">
<#assign title in layout>Internal Server Error - ${layout.title}</#assign>

<#macro stackTrace stack>
    <#list stack as stackFrame>
    &nbsp;&nbsp;&nbsp at&nbsp;${stackFrame}<br/>
    </#list>
</#macro>

<#macro printException ex>
<br/>Exception: ${ex}<br/>
    <#if ex.stackTrace?? >
    <@stackTrace stack=ex.stackTrace />
    </#if>
    <#if ex.cause?? >
    <#-- recurse -->
    <@printException ex=ex.cause />
    </#if>
</#macro>

<@layout.defaultlayout >

<section id="content">
    <section id="errorExplanation" class="content-area">
        <h1>${methods.nlsFor("error.500.title")}</h1>

    <#-- hidden server error text  -->
        <div class="smallText">
            <#if (config.debug && error??) >
                <b>${error.code!} - Internal Server Error</b><br/>
                <#list error.messages as message>
                    <div class="message">${message!}</div>
                </#list>
                <#if error.traces??>
                    <#list error.traces as trace>
                        &nbsp;&nbsp;${trace}<br/>
                    </#list>
                </#if>
                <#else>
                    Internal Server Error.
            </#if>
        </div>

    </section>
</@layout.defaultlayout >


