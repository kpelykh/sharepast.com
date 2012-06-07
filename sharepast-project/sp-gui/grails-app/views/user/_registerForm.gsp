<div class="accounts-form" id="signup">
    <h2>Create your free account</h2>

    <g:form name="auth-form" url="[controller:'user',action:'register']">
        <g:render template="/common/utils/messages" model="${pageScope.getVariables()}" />

        <fieldset>

            <div class="input control-group ${hasErrors(bean:user, field:'username','error')}">
                <div class="controls">
                    <input type="text" name="username" id="id_username" placeholder="Username" value="${fieldValue(bean:user,field:'username')}">
                    <g:hasErrors bean="${user}" field="username">
                        <span class="help-inline"><g:render template="/common/utils/messages" model="${ [bean: user, field: 'username'] }"/></span>
                    </g:hasErrors>
                </div>
            </div>

            <div class="input control-group ${hasErrors(bean:user, field:'email','error')}">
                <div class="controls">
                    <input type="text" name="email" id="id_email" placeholder="Email" value="${fieldValue(bean:user,field:'email')}">
                    <g:hasErrors bean="${user}" field="email">
                        <span class="help-inline"><g:render template="/common/utils/messages" model="${ [bean: user, field: 'email'] }"/></span>
                    </g:hasErrors>
                </div>
            </div>

            <div class="input control-group ${hasErrors(bean:user, field:'password','error')}">
                <div class="controls">
                    <input type="password" name="password" placeholder="Password" id="id_password">
                    <g:hasErrors bean="${user}" field="password">
                        <span class="help-inline"><g:render template="/common/utils/messages" model="${ [bean: user, field: 'password'] }"/></span>
                    </g:hasErrors>
                </div>
            </div>

            <div class="input control-group ${hasErrors(bean:user, field:'password','error')}">
                <div class="controls">
                    <input type="password" name="password2" placeholder="Repeat password" id="id_password2" >
                </div>
            </div>

            <div class="form-actions">
                <input type="submit" class="btn btn-green" value="Create account">
            </div>
        </fieldset>
    </g:form>


</div>