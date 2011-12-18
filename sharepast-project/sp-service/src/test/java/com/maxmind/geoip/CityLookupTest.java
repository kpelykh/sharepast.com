package com.maxmind.geoip;/* CityLookupTest.java */

import com.sharepast.util.spring.Configurator;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


@Test
public class CityLookupTest {

    @BeforeClass
    public void configure() {
        Configurator.getInstance().configure("com/sharepast/base.xml", "com/sharepast/service/geoip-location.xml");
    }

    @AfterClass
    public void afterClass() {
        Configurator.getInstance().shutdown();
    }

    /**
     * resolve IP to location
     */
    @Test
    public void testGoogleIPLocation() {

        try {

            LookupService geoIPservice = Configurator.squeeze(LookupService.class, "geoIPService");

            GeoIPLocation l1 = geoIPservice.getLocation("74.125.224.16");
            Assert.assertEquals(l1.getCountryCode(), "US", "Google server country code must resolve to US!");
            Assert.assertEquals(l1.getCountryName(), "United States", "Google server country namemust resolve to United States!");
            Assert.assertEquals(l1.getRegion(), "CA", "Google server region must resolve to CA!");
            Assert.assertEquals(RegionName.regionNameByCode(l1.getCountryCode(), l1.getRegion()), "California", "Google server region name must resolve to California!");
            Assert.assertEquals(l1.getCity(), "Mountain View", "Google server city must resolve to Mountain View!");
            Assert.assertEquals(l1.getPostalCode(), "94043", "Google server postal code must resolve to 94043!");
            Assert.assertEquals(l1.getMetroCode(), 807, "Google server must resolve to metro/dma code 807!");
            Assert.assertEquals(l1.getAreaCode(), 650, "Google server area code must resolve to 650!");
            Assert.assertEquals(TimeZone.timeZoneByCountryAndRegion(l1.getCountryCode(), l1.getRegion()), "America/Los_Angeles", "Google server time zone must resolve to America/Los_Angeles!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
