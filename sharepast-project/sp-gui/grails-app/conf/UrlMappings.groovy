class UrlMappings {

	static mappings = {
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

		"/"(view:"/index")

        "403"(view: "/errors/forbidden")
        "404"(view: "/errors/notFound")
        "500"(view: "/errors/serverError")


	}
}
