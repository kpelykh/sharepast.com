modules = {
    master {
        dependsOn 'jquery'
        resource url:'/js/main.js'
        resource url:'/css/main.css'

    }
    homepage {
        dependsOn 'master'
        resource 'css/home.css'
    }
    error {
        resource url: 'css/errors.css'
    }
}
