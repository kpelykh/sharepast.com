package com.sharepast.runners;

import com.sharepast.config.BaseConfig;
import com.sharepast.config.DatabaseConfig;
import com.sharepast.http.HttpServer;
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
@Import({BaseConfig.class,
        DatabaseConfig.class,
        HttpServer.class})
@ImportResource({
        "classpath:com/sharepast/config/cache.xml",
        "classpath:com/sharepast/config/security_spring.xml",
        "classpath:com/sharepast/config/jmx.xml",
        "classpath:com/sharepast/config/geoip-location.xml",
        "classpath:com/sharepast/config/jms_producer.xml"})
public class PlatformRunner {
}
