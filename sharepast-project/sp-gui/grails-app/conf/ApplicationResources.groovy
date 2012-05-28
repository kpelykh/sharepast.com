modules = {
    master {
        dependsOn 'jquery'
        resource url:'/js/main.js'
        resource url:'/css/main.css'
        resource url:'/less/bootstrap.less', attrs:[rel: "stylesheet/less", type:'css']

    }
    homepage {
        dependsOn 'master'
        resource 'css/home.css'
    }
    error {
        resource url: 'css/errors.css'
    }

}
