package com.sharepast.helloworld

class HelloController {

    def index() {
        def msg = message(code:"error.404.main.page")
        render msg
    }
}
