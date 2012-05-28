includeTargets << grailsScript("_GrailsWar")

target(main: "Creates the WEB-INF/grails.xml") {

    stagingDir = "${basedir}/web-app"
    createDescriptor()
}

setDefaultTarget(main)
