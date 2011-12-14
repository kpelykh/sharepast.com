<#import "../libs/util.ftl" as util>

<#assign link in layout>
<link href="/css/users/forms.css" rel="stylesheet"/>
</#assign>


<@layout.addBodyJS>
$(function() {
  $("#user_is_email_subscribed").click(function() {
    $("#user_is_email_subscribed").val($("#user_is_email_subscribed").is(":checked"));
  });
});
</@layout.addBodyJS>


<@layout.defaultlayout >

<section id="main">
        <div class="form-container">

	<div class="form-header">
	  <h4>Register</h4>
	</div>

  <form method="post" id="new_user" class="new_user" action="/users/new">

    <#if error??>
        <div id="errorExplanation" class="errorExplanation">
            <ul>
                <#list error as errorItem>
                    <li>${errorItem}</li>
                </#list>
            </ul>
        </div>
    </#if>

    <div class="label-and-input">
      <div class="label-container"><label for="user_username">Username</label></div>
      <div class="input-container"><input type="text" size="30" name="username" id="user_username"/></div>
    </div>
    <div class="label-and-input">
      <div class="label-container"><label for="user_email">Email</label></div>
      <div class="input-container"><input type="text" size="30" name="email" id="user_email"/></div>
    </div>
    <div class="label-and-input">
      <div class="label-container"><label for="user_password">Password</label></div>
      <div class="input-container"><input type="password" size="30" name="password" id="user_password"/></div>
    </div>
    <div class="label-and-input">
      <div class="label-container"><label for="user_password_confirmation">Password confirmation</label></div>
      <div class="input-container"><input type="password" size="30" name="password_confirmation" id="user_password_confirmation"/></div>
    </div>
    <div class="label-and-input">
      <input type="checkbox" value="1" name="is_email_subscribed" id="user_is_email_subscribed"/>
      <label for="user_is_email_subscribed" class="email-subscription-label">Yes, I want to subscribe to email newsletters.</label>
    </div>

	<p><input type="submit" value="Submit" name="commit" id="user_submit"></p>

  </form>

</div>
</section>

</@layout.defaultlayout>

