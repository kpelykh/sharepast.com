<#import "libs/util.ftl" as util>

<#assign link in layout>
<link href="/css/users/forms.css" rel="stylesheet"/>
<link href="/css/users/login.css" rel="stylesheet"/>
</#assign>

<#if remember_me??>
<#else>
    <#assign remember_me=false>
</#if>


<@layout.addBodyJS>
$(function() {
  $("#remember_me_checkbox").click(function() {
    $("#remember_me_checkbox").val($("#remember_me_checkbox").is(":checked"));
    //log('remember_me_checkbox.value=', $("#remember_me_checkbox").val());
  });
});
</@layout.addBodyJS>

<@layout.defaultlayout >
<section id="main">

    <div class="form-container">


        <div class="form-header">
            <h4>Log in to your account</h4>
        </div>

        <form method="post" id="new_user_session" class="new_user_session" action="/login">
            <input type="hidden" name="targetUri" value="${targetUri!"/"}"/>
            <input type="hidden" name="targetQuery" value="${targetQuery!""}"/>

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
                <div class="label-container"><label for="user_session_username">Username</label></div>
                <div class="input-container"><input type="text" size="30" value="${login!}" name="login"
                                                    id="user_session_username"></div>
            </div>
            <div class="label-and-input">
                <div class="label-container"><label for="user_session_password">Password</label></div>
                <div class="input-container">
                    <input type="password" size="30" name="password" id="user_session_password"/>
                </div>
            </div>
            <div class="label-and-input">
                <div class="label-container">&nbsp;</div>
                <div class="input-container">
                    <input type="checkbox" name="remember_me" ${remember_me?string("checked", "fsdfsad")} value="${remember_me?string}" id="remember_me_checkbox">
                    <label for="remember_me_checkbox">Remember Me</label>
                </div>
            </div>
            <p>
                <input type="submit" value="Submit" name="commit" id="user_session_submit">
                &nbsp;&nbsp;&nbsp;<a class="forgot-password-link" href="/password_resets/new">Forgot password?</a>
            </p>
        </form>
    </div>

</section>

</@layout.defaultlayout>

