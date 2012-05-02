package java.com.sharepast.dao;


import com.sharepast.domain.GPSCoordinates;
import com.sharepast.domain.GeographicLocationDO;
import com.sharepast.persistence.ORMDao;

import java.util.List;

public interface GeographicLocationDAO extends ORMDao<GeographicLocationDO, Long> {

    public GeographicLocationDO getByPostalCode(final String postalCode);

    public GeographicLocationDO getByLatitudeAndLongitude(double latitude, double longitude);

    public List<GeographicLocationDO> getPostalCodesByState(final String state);

    public List<GeographicLocationDO> getPostalCodesByDMAZone(final int zoneId);

    public List<GeographicLocationDO> getPostalCodesWithin(final GPSCoordinates[] bounds);

    public List<GeographicLocationDO> getPostalCodesWithinRadius(GeographicLocationDO location, int radius);
}
