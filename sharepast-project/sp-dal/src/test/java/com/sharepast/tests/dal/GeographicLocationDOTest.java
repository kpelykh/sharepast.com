package com.sharepast.tests.dal;

import com.sharepast.dao.GeographicLocation;
import com.sharepast.spring.config.BaseConfig;
import com.sharepast.domain.GeographicLocationDO;
import com.sharepast.spring.SpringConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.util.StopWatch;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

@Test(enabled = true)
public class GeographicLocationDOTest {

    private static final Logger LOG = LoggerFactory.getLogger(GeographicLocationDOTest.class);
    private StopWatch stopWatch;

    @Configuration
    @Import({BaseConfig.class})
    @ImportResource({"com/sharepast/config/spring-data.xml"})
    static class TestGeoLocationConf {}

    @BeforeClass
    public void prepareBeforeTest()
            throws Exception {
        SpringConfiguration.getInstance().configure(TestGeoLocationConf.class);

    }

    @AfterClass
    public void afterClass() {
        SpringConfiguration.getInstance().shutdown();
    }

    @Test
    public void checkZipCode()
            throws Exception {

        GeographicLocation geoLocation = SpringConfiguration.getInstance().getBean(GeographicLocation.class, "geographicLocationDao");

        stopWatch = new StopWatch("GeographicLocationDOTest");
        stopWatch.start("GeographicLocationDOTest");
        for (int i=0; i<=500; i++) {
            GeographicLocationDO geoDo = geoLocation.find(i);
        }
        if (stopWatch.isRunning()) {
            stopWatch.stop();
        }
        LOG.info("Time elapsed: " + stopWatch.getTotalTimeMillis());

        GeographicLocationDO location = geoLocation.getByPostalCode("94041");
        assertEquals(location.getCity(), "Mountain View", "Cities don't match");

        GeographicLocationDO greatNeck = geoLocation.getByLatitudeAndLongitude(40.7913017272949, -73.7412033081055);
        assertEquals(greatNeck.getCity(), "Great Neck", "Invalid city name. Expected Great Neck.");
    }
}
