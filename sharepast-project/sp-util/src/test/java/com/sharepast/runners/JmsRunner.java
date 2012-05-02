package java.com.sharepast.runners;

import com.sharepast.spring.config.BaseConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

/**
 * Created by IntelliJ IDEA.
 * User: kpelykh
 * Date: 1/29/12
 * Time: 10:18 PM
 * To change this template use File | Settings | File Templates.
 */
@Configuration
@Import(BaseConfig.class)
@ImportResource({
        "com/sharepast/config/jmx.xml",
        "com/sharepast/config/spring-data.xml",
        "com/sharepast/config/geoip-location.xml",
        "com/sharepast/config/activemq.xml"})
public class JmsRunner {
}
