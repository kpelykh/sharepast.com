<#ftl strip_text="true">
<#assign title in layout>Page Not Found - ${layout.title}</#assign>

<@layout.buttonlessLayout>
<div class="errorPage">
   <div class="pageNotFound"></div>  
   <div class="message">
      <h1>${modules.nlsFor("error.404.title")}</h1>
      <h3>
        ${modules.nlsFor("error.404.message")}
        <br/>
        <br/>
      </h3>
   </div>
</div>
</@>
