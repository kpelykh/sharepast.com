import com.sharepast.commons.spring.SpringConfiguration
import org.codehaus.groovy.grails.orm.hibernate.cfg.GrailsAnnotationConfiguration

dataSource_DEFAULT {
    configClass = GrailsAnnotationConfiguration.class
    pooled = true
    driverClassName = SpringConfiguration.environment.getProperty("jdbc.driver")
    username = SpringConfiguration.environment.getProperty("jdbc.username")
    password = SpringConfiguration.environment.getProperty("jdbc.password")
    url = SpringConfiguration.environment.getProperty("jdbc.sp.db.url")
    dialect = SpringConfiguration.environment.getProperty("hibernate.dialect")
    dbCreate = "validate"
}

hibernate {
    cache.use_second_level_cache=false
}
