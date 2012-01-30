package com.sharepast.config.runners;

import com.sharepast.config.BaseConfig;
import com.sharepast.config.WebConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

/**
 * Created by IntelliJ IDEA.
 * User: kpelykh
 * Date: 1/29/12
 * Time: 10:16 PM
 * To change this template use File | Settings | File Templates.
 */
@Configuration
@Import({BaseConfig.class, WebConfig.class})
@ImportResource({
        "com/sharepast/config/cache.xml",
        "com/sharepast/config/security.xml",
        "com/sharepast/config/jmx.xml",
        "com/sharepast/config/persistence.xml",
        "com/sharepast/config/services.xml",
        "com/sharepast/config/geoip-location.xml",
        "com/sharepast/config/jms_producer.xml",
        "com/sharepast/config/container.xml",
        "com/sharepast/config/restlets.xml"})
public class PlatformRunner {
}
