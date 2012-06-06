modules = {
    master {
        dependsOn 'jquery'
        resource url:'/js/main.js'
        resource url:'/js/lib/bootstrap/bootstrap-dropdown.js'

    }
    app {
        dependsOn 'master'
        resource 'css/app.css'
    }
    error {
        resource url: 'css/errors.css'
    }
    user {
        dependsOn 'master'
        resource url: 'css/user.css'
    }
}
