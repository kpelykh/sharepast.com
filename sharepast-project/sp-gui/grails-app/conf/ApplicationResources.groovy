modules = {
    master {
        dependsOn 'jquery'
        resource url:'/js/main.js'

    }
    homepage {
        dependsOn 'master'
        resource 'css/home.css'
    }
    error {
        resource url: 'css/errors.css'
    }
    login {
        dependsOn 'master'
    }
}
