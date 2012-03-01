package com.sharepast.dao.hibernate;


import com.sharepast.dao.GeographicLocationDAO;
import com.sharepast.domain.GPSCoordinates;
import com.sharepast.domain.GeographicLocationDO;
import com.sharepast.domain.user.Role;
import com.sharepast.util.GeographicLocationUtil;
import com.sharepast.persistence.hibernate.CriteriaDetails;
import com.sharepast.persistence.hibernate.HibernateDao;
import com.sharepast.persistence.hibernate.QueryDetails;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@DependsOn("txAspect")
@Repository("geographicLocationDao")
@Transactional( propagation = Propagation.REQUIRED, rollbackFor = HibernateException.class)
public class GeographicLocationDAOImpl extends HibernateDao<GeographicLocationDO> implements GeographicLocationDAO {

    private static final Logger LOG = LoggerFactory.getLogger(GeographicLocationDAOImpl.class);

    public Class<GeographicLocationDO> getEntityClass() {
        return GeographicLocationDO.class;
    }

    public GeographicLocationDO getByPostalCode(final String postalCode) {
        // prepend zeroes if length less then 5
        String zipCode = postalCode;
        if (postalCode.length() < 5) {
            String zeroes = "00000";
            zipCode = zeroes.substring(0, 5 - postalCode.length()) + postalCode;
        }
        final String validPostalCode = zipCode;
        GeographicLocationDO gldo;
        gldo = findByCriteria(new CriteriaDetails() {
            @Override
            public Criteria completeCriteria(Criteria criteria) {
                return criteria.add(Restrictions.eq("postalCode", validPostalCode));
            }
        });
        return gldo;
    }

    public GeographicLocationDO getByLatitudeAndLongitude(double latitude, double longitude) {
        final int decimalPlaces = 1;
        // Round the given latitude and longitude to get range
        BigDecimal bigDecimal = new BigDecimal(latitude);
        double lowerLatitude = bigDecimal.setScale(decimalPlaces, BigDecimal.ROUND_DOWN).doubleValue();
        bigDecimal = new BigDecimal(latitude);
        double upperLatitude = bigDecimal.setScale(decimalPlaces, BigDecimal.ROUND_UP).doubleValue();
        double lowerLongitude, upperLongitude;
        if (longitude > 0) {
            bigDecimal = new BigDecimal(longitude);
            lowerLongitude = bigDecimal.setScale(decimalPlaces, BigDecimal.ROUND_DOWN).doubleValue();
            bigDecimal = new BigDecimal(longitude);
            upperLongitude = bigDecimal.setScale(decimalPlaces, BigDecimal.ROUND_UP).doubleValue();
        } else {
            // Negative numbers work in the opposite way
            bigDecimal = new BigDecimal(longitude);
            lowerLongitude = bigDecimal.setScale(decimalPlaces, BigDecimal.ROUND_UP).doubleValue();
            bigDecimal = new BigDecimal(longitude);
            upperLongitude = bigDecimal.setScale(decimalPlaces, BigDecimal.ROUND_DOWN).doubleValue();
        }
        GPSCoordinates bottomLeft = new GPSCoordinates(lowerLatitude, lowerLongitude);
        GPSCoordinates topRight = new GPSCoordinates(upperLatitude, upperLongitude);
        List<GeographicLocationDO> results = getPostalCodesWithin(new GPSCoordinates[]{bottomLeft, topRight});
        if (results == null || results.isEmpty())
            return null;
        if (results.size() == 1)
            return results.get(0);
        return GeographicLocationUtil.getClosest(new GPSCoordinates(latitude, longitude), results);
    }

    /**
     * get all zip codes/cities within a state
     */
    public List<GeographicLocationDO> getPostalCodesByState(final String state) {
        return listByQuery(new QueryDetails() {
            public String getQueryString() {
                return "from GeographicLocationDO where lower(state) = :state";
            }

            public Query completeQuery(Query query) {
                return query.setString("state", state.toLowerCase());
            }
        });
    }

    /**
     * get all zip codes within a DMA zone
     *
     * @param zoneId
     * @return
     */
    // @CacheCoherent
    public List<GeographicLocationDO> getPostalCodesByDMAZone(final int zoneId) {
        return listByCriteria(new CriteriaDetails() {
            public Criteria completeCriteria(Criteria criteria) {
                return criteria.add(Restrictions.eq("dmaZoneId", zoneId));
            }
        });
    }

    public List<GeographicLocationDO> getPostalCodesWithin(final GPSCoordinates[] bounds) {
        return listByCriteria(new CriteriaDetails() {
            public Criteria completeCriteria(Criteria criteria) {
                return criteria.add(Restrictions.ge("coordinates.latitude", bounds[0].getLatitude())).add(Restrictions.le("coordinates.latitude",
                        bounds[1].getLatitude())).add(Restrictions.ge("coordinates.longitude",
                        bounds[0].getLongitude())).add(Restrictions.le("coordinates.longitude",
                        bounds[1].getLongitude()));
            }
        });
    }

    public List<GeographicLocationDO> getPostalCodesWithinRadius(GeographicLocationDO location, int radius) {
        // First find bounding coordinates
        GPSCoordinates[] bounds = GeographicLocationUtil.getBoundingLatLong(location.getCoordinates(), radius);
        return getPostalCodesWithin(bounds);
    }

    // optimized version -- we need to know the DMA
    public List<GeographicLocationDO> getPostalCodesWithin(Integer dmaZoneId, final GPSCoordinates[] bounds) {
        List<GeographicLocationDO> nearbyPostalCodes = new ArrayList<GeographicLocationDO>();
        for (GeographicLocationDO location : getPostalCodesByDMAZone(dmaZoneId)) {
            if (location.getCoordinates().getLatitude() >= bounds[0].getLatitude()
                    && location.getCoordinates().getLatitude() <= bounds[1].getLatitude()
                    && location.getCoordinates().getLongitude() >= bounds[0].getLongitude()
                    && location.getCoordinates().getLongitude() <= bounds[1].getLongitude()) {
                nearbyPostalCodes.add(location);
            }
        }
        return nearbyPostalCodes;
    }
}