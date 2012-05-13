<#ftl strip_text="true">
<#assign title in layout>Page Not Found - ${layout.title}</#assign>

<@layout.buttonlessLayout>
<div class="errorPage">
   <div class="errorPage-icon"></div>
   <div class="message">
      <h1>${modules.nlsFor("error.401.title")}</h1>
      <h3>
        ${modules.nlsFor("error.401.message")}
        <br/>
      </h3>
   </div>
</div>
</@>
