import grails.util.Environment

class UrlMappings {

	static mappings = {
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

        "/"(controller:'home',action:'index')
        "/app"(controller: 'app', action: 'index')

        "401"(controller: "error", action: "forbidden")
        "403"(controller: "error", action: "devError")
        "404"(controller: "error", action: "notFound")
        if (Environment.current == Environment.PRODUCTION) {
            "500"(controller: "error", action: "serverError")
        }
        else {
            "500"(controller: "error", action: "devError")
        }


	}
}
