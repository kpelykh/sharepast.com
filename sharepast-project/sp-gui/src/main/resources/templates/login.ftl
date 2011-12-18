<#import "libs/util.ftl" as util>

<#assign link in layout>
    <link href="/css/users/login.css" rel="stylesheet"/>
</#assign>

<#if remember_me??>
    <#assign rememberMe=remember_me />
<#else>
    <#assign rememberMe=false />
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


<div class="content">
    <div class="page-header">
        <h1>Login to your account</h1>
    </div>

        <div class="login-form">
            <#if error??>
                <div class="row">
                    <div class="alert-message warning">
                        <#list error as errorItem>
                            <p>${errorItem}</p>
                        </#list>
                    </div>
                </div>
            </#if>


            <form method="post" action="/login" class="span7">
            <input type="hidden" name="targetUri" value="${targetUri!"/"}"/>
            <input type="hidden" name="targetQuery" value="${targetQuery!""}"/>

            <fieldset>
                <div class="clearfix">
                    <label for="loginBox">Username</label>
                    <div class="input">
                        <input class="span3" id="loginBox" value="${login!}" name="login" type="text" />
                    </div>
                </div><!-- /clearfix -->

                <div class="clearfix">
                    <label for="passwordBox">Password</label>
                    <div class="input">
                        <input class="span3" id="passwordBox" name="password" type="password" />
                    </div>
                </div><!-- /clearfix -->

                <div class="input span3">
                    <input id="remember_me_checkbox"  ${rememberMe?string("checked", "fsdfsad")}
                           value="${rememberMe?string}" name="remember_me" type="checkbox" />
                    &nbsp;&nbsp;Remember me
                </div>

                <div class="actions">
                    <input type="submit" class="btn primary" value="Login">
                </div>
            </fieldset>
            </form>
        </div>
</div>

</@layout.defaultlayout>

