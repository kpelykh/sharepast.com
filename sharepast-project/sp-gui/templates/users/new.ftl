<#import "../libs/util.ftl" as util>

<@layout.defaultlayout>

<div class="content">
    <div class="page-header">
        <h1>Register</h1>
    </div>

    <div class="registration-form">
        <#if error??>
            <div class="row">
                <div class="alert-message warning">
                    <#list error as errorItem>
                        <p>${errorItem}</p>
                    </#list>
                </div>
            </div>
        </#if>


        <form method="post" action="/users/new" class="span7">

            <fieldset>
                <div class="clearfix">
                    <label for="user_username">Username</label>
                    <div class="input">
                        <input class="span3" id="user_username" value="${login!}" name="username" type="text" />
                    </div>
                </div><!-- /clearfix -->

                <div class="clearfix">
                    <label for="user_email">Email</label>
                    <div class="input">
                        <input class="span3" id="user_email" name="email" type="text" />
                    </div>
                </div><!-- /clearfix -->

                <div class="clearfix">
                    <label for="user_password">Password</label>
                    <div class="input">
                        <input class="span3" id="user_password" name="password" type="password" />
                    </div>
                </div><!-- /clearfix -->

                <div class="clearfix">
                    <label for="user_password_confirmation">Password confirmation</label>
                    <div class="input">
                        <input class="span3" id="user_password_confirmation" name="password_confirmation" type="password" />
                    </div>
                </div><!-- /clearfix -->

                <div class="actions">
                    <input type="submit" class="btn primary" value="Register">
                </div>
            </fieldset>
        </form>
    </div>
</div>

</@layout.defaultlayout>

