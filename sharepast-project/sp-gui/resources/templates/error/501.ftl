<#ftl strip_text="true">
<#assign title in layout>Not Implemented - ${layout.title}</#assign>

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

<@layout.defaultlayout>
<#if config.debug>
<b>${error.code} - Not Implemented</b><br/>
<#list error.messages as message>
${message}
</#list>
<#if error.traces??>
  <#list error.traces as trace>
  &nbsp;&nbsp;${trace}<br />
  </#list>
</#if>
<#else>
Internal Server Error.
</#if>
</@layout.defaultlayout>