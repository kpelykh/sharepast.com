package com.sharepast

import com.sharepast.service.Subject

/**
 * Created with IntelliJ IDEA.
 * User: kpelykh
 * Date: 5/27/12
 * Time: 4:31 PM
 * To change this template use File | Settings | File Templates.
 */
class HomeController {
    def index = {
        if (Subject.fullyAuthenticated) {
            redirect controller: "app", action: "index"
        }

    }
}
