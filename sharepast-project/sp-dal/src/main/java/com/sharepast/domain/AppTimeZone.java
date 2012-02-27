package com.sharepast.domain;

import java.io.Serializable;
import java.util.TimeZone;


public enum AppTimeZone implements Serializable{

  AST ("Atlantic Standard Time", "Canada/Atlantic", -4.0),
  EST ("Eastern Standard Time", "America/New_York", -5.0),
  EDT ("Eastern Daylight Time", "America/New_York", -4.0),
  CST ("Central Standard Time", "America/Chicago", -6.0),
  MST ("Mountain Standard Time", "America/Denver", -7.0),
  PST ("Pacific Standard Time", "America/Los_Angeles", -8.0),
  AKST ("Alaska Standard Time", "America/Anchorage", -9.0),
  HAST ("Hawaii-Aleutian Standard Time", "America/Adak", -10.0),
  X ("American Samoa", "US/Samoa", -11.0),
  M ("Marshall Islands", "Pacific/Fiji", 12.0),
  K ("Guam", "Pacific/Guam", 10.0),
  I ("Palau", "Pacific/Palau", 9.0),
  GMT ("Greenwich Time", "Etc/Greenwich", 0.0),
  UTC ("UTC", "UTC", 0.0);


  private double utcOffset;
  private String zoneName;
  private String javaId;

  AppTimeZone(String name, String javaId, double utcOffset) {
    this.zoneName = name;
    this.javaId = javaId;
    this.utcOffset = utcOffset;
  }

  public synchronized double getUtcOffset() {
    return utcOffset;
  }

  public synchronized void setUtcOffset(double utcOffset) {
    this.utcOffset = utcOffset;
  }

  public synchronized String getZoneName() {
    return zoneName;
  }

  public synchronized void setZoneName(String zoneName) {
    this.zoneName = zoneName;
  }

  public synchronized String getJavaId() {
    return javaId;
  }

  public synchronized void setJavaId(String javaId) {
    this.javaId = javaId;
  }

  public synchronized TimeZone getTimeZone() {
    return TimeZone.getTimeZone(javaId);
  }
}
