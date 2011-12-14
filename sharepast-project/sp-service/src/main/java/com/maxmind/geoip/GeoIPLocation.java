/**
 * Location.java
 *
 * Copyright (C) 2004 MaxMind LLC.  All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Lesser Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.maxmind.geoip;

public class GeoIPLocation {


  private String countryCode;
  private String countryName;
  private String region;
  private String city;
  private String postalCode;
  private float latitude;
  private float longitude;
  private int dma_code;
  private int area_code;
  private int metro_code;

  private final static double EARTH_DIAMETER = 2 * 6378.2;
  private final static double PI = 3.14159265;
  private final static double RAD_CONVERT = PI / 180;

  public double distance(GeoIPLocation loc) {
    double delta_lat, delta_lon;
    double temp;

    float lat1 = latitude;
    float lon1 = longitude;
    float lat2 = loc.latitude;
    float lon2 = loc.longitude;

    // convert degrees to radians
    lat1 *= RAD_CONVERT;
    lat2 *= RAD_CONVERT;

    // find the deltas
    delta_lat = lat2 - lat1;
    delta_lon = (lon2 - lon1) * RAD_CONVERT;

    // Find the great circle distance
    temp = Math.pow(Math.sin(delta_lat / 2), 2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(delta_lon / 2), 2);
    return EARTH_DIAMETER * Math.atan2(Math.sqrt(temp), Math.sqrt(1 - temp));
  }


  public String getCountryCode() {
    return countryCode;
  }

  public void setCountryCode(String countryCode) {
    this.countryCode = countryCode;
  }

  public String getCountryName() {
    return countryName;
  }

  public void setCountryName(String countryName) {
    this.countryName = countryName;
  }

  public String getRegion() {
    return region;
  }

  public void setRegion(String region) {
    this.region = region;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }

  public float getLatitude() {
    return latitude;
  }

  public void setLatitude(float latitude) {
    this.latitude = latitude;
  }

  public float getLongitude() {
    return longitude;
  }

  public void setLongitude(float longitude) {
    this.longitude = longitude;
  }

  public int getDmaCode() {
    return dma_code;
  }

  public void setDmaCode(int dma_code) {
    this.dma_code = dma_code;
  }

  public int getAreaCode() {
    return area_code;
  }

  public void setAreaCode(int area_code) {
    this.area_code = area_code;
  }

  public int getMetroCode() {
    return metro_code;
  }

  public void setMetroCode(int metro_code) {
    this.metro_code = metro_code;
  }

}
