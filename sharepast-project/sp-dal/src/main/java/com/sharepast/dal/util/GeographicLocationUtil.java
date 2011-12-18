package com.sharepast.dal.util;

import com.sharepast.dal.domain.GPSCoordinates;
import com.sharepast.dal.domain.GeographicLocationDO;
import com.sharepast.dal.domain.user.Location;

import java.util.List;

public class GeographicLocationUtil {

  public static GPSCoordinates midpoint(GPSCoordinates p1, GPSCoordinates p2) {
    if (p1 == null && p2 == null)
      return null;

    if (p1 == null)
      return p2;

    if (p2 == null)
      return p1;

    // Source: http://www.movable-type.co.uk/scripts/latlong.html
    // JavaScript code:
    //    lat1 = this._lat.toRad(),
    //    lon1 = this._lon.toRad();
    //    lat2 = point._lat.toRad();
    //    var dLon = (point._lon-this._lon).toRad();
    //
    //    var Bx = Math.cos(lat2) * Math.cos(dLon);
    //    var By = Math.cos(lat2) * Math.sin(dLon);
    //
    //    lat3 = Math.atan2(Math.sin(lat1)+Math.sin(lat2),
    //                      Math.sqrt( (Math.cos(lat1)+Bx)*(Math.cos(lat1)+Bx) + By*By) );
    //    lon3 = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);
    //
    //    return new LatLon(lat3.toDeg(), lon3.toDeg());

    double radLatitude1 = Math.toRadians(p1.getLatitude());
    double radLatitude2 = Math.toRadians(p2.getLatitude());
    double radLongitude1 = Math.toRadians(p1.getLongitude());
    double diffLongitute = Math.toRadians(p2.getLongitude() - p1.getLongitude());

    double x = Math.cos(radLatitude2) * Math.cos(diffLongitute);
    double y = Math.cos(radLatitude2) * Math.sin(diffLongitute);

    double midLatitude = Math.atan2(Math.sin(radLatitude1) + Math.sin(radLatitude2),
                  Math.sqrt((Math.cos(radLatitude1) + x) * (Math.cos(radLatitude1) + x) + y * y));
    double midLongitude = radLongitude1 + Math.atan2(y, Math.cos(radLatitude1) + x);

    return new GPSCoordinates(Math.toDegrees(midLatitude), Math.toDegrees(midLongitude));
  }

  public static double distance(GPSCoordinates p1, GPSCoordinates p2) {
    if (p1 == null || p2 == null)
      return 0;

    // Source: http://www.zipcodeworld.com/samples/distance.java.html

    double theta = p1.getLongitude() - p2.getLongitude();
    double distance = Math.sin(Math.toRadians(p1.getLatitude())) * Math.sin(Math.toRadians(p2.getLatitude()))
                  + Math.cos(Math.toRadians(p1.getLatitude())) * Math.cos(Math.toRadians(p2.getLatitude())) * Math.cos(Math.toRadians(theta));
    distance = Math.acos(distance);

    return Math.toDegrees(distance) * 60 * 1.1515;  // miles
  }

  public static double distance(GPSCoordinates p1, Location loc) {
    if (p1 == null || loc == null)
      return 0;

    // Source: http://www.zipcodeworld.com/samples/distance.java.html

    double theta = p1.getLongitude() - loc.getLongitude();
    double distance = Math.sin(Math.toRadians(p1.getLatitude())) * Math.sin(Math.toRadians(loc.getLatitude()))
                  + Math.cos(Math.toRadians(p1.getLatitude())) * Math.cos(Math.toRadians(loc.getLatitude())) * Math.cos(Math.toRadians(theta));
    distance = Math.acos(distance);

    return Math.toDegrees(distance) * 60 * 1.1515;  // miles
  }

  /**
   * Determine coordinates of the bounding box with the given radius in miles starting from the center.
   *
   * @param center            Center point
   * @param radiusInMiles     Radius in miles
   *
   * @return coordinates of the bottom-left (index 0) and top-right (index 1) corners of the bounding box.
   */
  public static GPSCoordinates[] getBoundingLatLong(GPSCoordinates center, int radiusInMiles) {
    if (center == null)
      return null;

    if (radiusInMiles <= 0)
      return new GPSCoordinates[]{center, center};

    // Source: http://uclue.com/?xq=2861

    GPSCoordinates[] bounds = new GPSCoordinates[2];

    double degrees = radiusInMiles / 69.1;

    double minLatitude = center.getLatitude() - degrees;
    double maxLatitude = center.getLatitude() + degrees;

    degrees /= Math.cos(Math.toRadians(center.getLatitude()));

    double minLongitude = center.getLongitude() - degrees;
    double maxLongitude = center.getLongitude() + degrees;

    bounds[0] = new GPSCoordinates(minLatitude, minLongitude);
    bounds[1] = new GPSCoordinates(maxLatitude, maxLongitude);

    return bounds;
  }

  /**
   * Given a list of locations, determine the top-right and bottom-left corners of the bounding box.
   *
   * @param locationList    A list of GeographicLocationDO
   *
   * @return coordinates of the bottom-left (index 0) and top-right (index 1) corners of the bounding box.
   */
  public static GPSCoordinates[] getBoundingLatLong(List<GeographicLocationDO> locationList) {
    if (locationList == null || locationList.isEmpty())
      return null;

    double minLatitude = locationList.get(0).getCoordinates().getLatitude();
    double maxLatitude = minLatitude;
    double minLongitude = locationList.get(0).getCoordinates().getLongitude();
    double maxLongitude = minLongitude;

    for (GeographicLocationDO location : locationList) {
      GPSCoordinates coordinates = location.getCoordinates();

      if (coordinates.getLatitude() > maxLatitude)
        maxLatitude = coordinates.getLatitude();
      else if (coordinates.getLatitude() < minLatitude)
        minLatitude = coordinates.getLatitude();

      if (coordinates.getLongitude() > maxLongitude)
        maxLongitude = coordinates.getLongitude();
      else if (coordinates.getLongitude() < minLongitude)
        minLongitude = coordinates.getLongitude();
    }

    GPSCoordinates bottomLeft = new GPSCoordinates(minLatitude, minLongitude);
    GPSCoordinates topRight = new GPSCoordinates(maxLatitude, maxLongitude);

    return new GPSCoordinates[]{bottomLeft, topRight};
  }

  /**
   * From the given list of locations, find one that is the closest to the anchor point.
   *
   * @param anchor          Anchor point for distance calculation and comparison
   * @param locationList    A list of GeographicLocationDO
   *
   * @return GeographicLocationDO that is closest to the anchor point.
   */
  public static GeographicLocationDO getClosest(GPSCoordinates anchor, List<GeographicLocationDO> locationList) {
    if (locationList == null || locationList.isEmpty())
      return null;

    double shortestDistance = distance(anchor, locationList.get(0).getCoordinates());
    GeographicLocationDO closest = locationList.get(0);
    for (GeographicLocationDO location : locationList) {
      double distance = distance(anchor, location.getCoordinates());
      if (distance < shortestDistance) {
        shortestDistance = distance;
        closest = location;
      }
    }

    return closest;
  }

}
