package com.sharepast

import com.sharepast.domain.user.User
import grails.validation.Validateable
import com.sharepast.service.impl.UserService
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.RequestAttributes
import org.springframework.security.authentication.BadCredentialsException

class UserController {

    def UserService userService

    def passwordReset() {
        if (request.method == 'POST') {
            flash.message = g.message(code: 'email.not.found')
        }
    }

    def login() {

        if (params.authfailed) {
            def error = RequestContextHolder.currentRequestAttributes().getAttribute("SPRING_SECURITY_LAST_EXCEPTION", RequestAttributes.SCOPE_SESSION)
            if (error instanceof BadCredentialsException) {
                render view:"login", model:[username: error.authentication.principal, message:"auth.invalid.login"]
                return
            }
        }

        render(view:"login")

    }

    /**
     * Creates a new Shiro account and links it to the OAuth token that's in
     * the current HTTP session.
     */
    def createAccount(NewAccountCommand cmd) {
        if ( cmd.hasErrors() ) {
            render view: 'register', model: [command: cmd]
            return
        }

        def user = new User(cmd.username, cmd.password)
        forward controller: "user", action: "index", params: [userId: user.id]
    }


    def register(NewAccountCommand cmd) {

        def renderParams = [view: "register"]

        if(request.method == 'POST') {
            //checking errors
            if (cmd.hasErrors()) {
                renderParams.model.user = cmd
                render(renderParams)
                return
            }

            def user = userService.findUserByUsername(cmd.username)
            if(user) {
                renderParams.model = [message:  "auth.user.already.exists"]
                render(renderParams)
            }
            else {
                if(params.password != params.password2) {
                    renderParams.model = [message:  "auth.password.mismatch"]
                    render(renderParams)
                }
                else {
                    user = new User(cmd.username, cmd.password)
                    user.setEmail(cmd.email)
                    userService.createUser(user)

                    userService.loginUser(cmd.username, cmd.password)

                    redirect controller: "app", action: "index"
                }
            }
        }
        else {
            render(renderParams)
        }

    }
}

@Validateable
class LoginAccountCommand {

    String username
    String password
    String email

    static constraints = {
        username nullable: false, blank: false
        password nullable: false, blank: false
    }
}

@Validateable
class NewAccountCommand {

    String username
    String email

    String password
    String password2

    static constraints = {
        username nullable: false, blank: false, validator: { val, obj ->
            val != "test" ? null : "user.login.unique"
            //obj.userService.isLoginUnique(val) ? null : "user.login.unique"
        }
        email nullable: false, blank: false, validator: { val, obj ->
            val != "test" ? null : "user.login.unique"
            //obj.userService.isEmailUnique(val) ? null : "user.email.unique"
        }
    }
}