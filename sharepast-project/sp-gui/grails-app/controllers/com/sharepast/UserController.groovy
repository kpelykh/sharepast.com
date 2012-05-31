package com.sharepast

class UserController {

    def index() { }

    def register(){

    }

    def passwordReset() {
        if (request.method == 'POST') {
            flash.message = g.message(code: 'email.not.found')
        }
    }

    def login() {
        if (request.method == 'POST') {
        } else {
            if (params.targetUri) session["targetUri"] = params.targetUri
            render(view:"login", model: [targetUri:params.targetUri])
        }
    }
}
