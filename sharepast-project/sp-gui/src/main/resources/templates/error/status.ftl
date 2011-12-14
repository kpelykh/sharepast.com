<#ftl strip_text="true">
<#assign title in layout>Error - ${layout.title}</#assign>


<@layout.defaultlayout >

<section id="content">
    <section id="errorExplanation" class="content-area">
        <h1>${methods.nlsFor("error.500.title")}</h1>

        <#if error?? >
          <#list error.messages as message>
            <div class="message">${message}</div>
          </#list>
        </#if>

    </section>
</@layout.defaultlayout >

