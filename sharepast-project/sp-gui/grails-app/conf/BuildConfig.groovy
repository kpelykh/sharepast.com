grails.project.work.dir = "${basedir}/target"
grails.servlet.version = "2.5" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "${basedir}/target/classes"
grails.project.plugin.class.dir = "${basedir}/target/classes"
grails.project.test.class.dir = "${basedir}/target/test-classes"
grails.project.test.reports.dir = "${basedir}/target/test-reports"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
//for correct jquery plugin resources resolution
grails.project.plugins.dir = "${basedir}/plugins"
//grails.offline.mode = true
//for correct resource (i18n) resolution
//grails.project.resource.dir=SpringConfiguration.getInstance().getEnvironment().getProperty("grails.resources")
//grails.project.war.file = "target/${appName}-${appVersion}.war"

grails.project.dependency.resolution = {
    pom true

    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {
        inherits true // Whether to inherit repository definitions from plugins
        grailsPlugins()
        grailsHome()
        grailsCentral()
        mavenCentral()

        // uncomment these to enable remote dependency resolution from public Maven repositories
        //mavenCentral()
        //mavenLocal()
        //mavenRepo "http://snapshots.repository.codehaus.org"
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        mavenRepo "http://repo.springsource.org/release"
    }

    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.
        // runtime 'mysql:mysql-connector-java:5.1.16'
    }

    plugins {
        runtime ":jquery:1.7.1",
                ":resources:1.1.6"

    }
}
