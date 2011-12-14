package kp.app.dal.dao;


import kp.app.dal.domain.GPSCoordinates;
import kp.app.dal.domain.GeographicLocationDO;
import kp.app.persistence.orm.ORMDao;

import java.util.List;

public interface GeographicLocationDAO extends ORMDao<Long, GeographicLocationDO> {

    public GeographicLocationDO getByPostalCode(final String postalCode);

    public GeographicLocationDO getByLatitudeAndLongitude(double latitude, double longitude);

    public List<GeographicLocationDO> getPostalCodesByState(final String state);

    public List<GeographicLocationDO> getPostalCodesByDMAZone(final int zoneId);

    public List<GeographicLocationDO> getPostalCodesWithin(final GPSCoordinates[] bounds);

    public List<GeographicLocationDO> getPostalCodesWithinRadius(GeographicLocationDO location, int radius);
}
