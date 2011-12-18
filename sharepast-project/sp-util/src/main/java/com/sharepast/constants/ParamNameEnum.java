package com.sharepast.constants;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 8/27/11
 * Time: 12:55 AM
 * To change this template use File | Settings | File Templates.
 */
public enum ParamNameEnum {
  USER_ID("userId");

  private String key;
  private String defaultValue;

  ParamNameEnum (String key) {

    this(key, null);
  }

  ParamNameEnum (String key, String defaultValue) {
    this.key = key;
    this.defaultValue = defaultValue;
  }


  public String getKey () {

    return key;
  }

  public String getDefaultValue () {

    return defaultValue;
  }

  public Integer getDefaultAsInteger () {

    if (defaultValue == null) {

      return null;
    }

    return Integer.parseInt(defaultValue);
  }

  public Long getDefaultAsLong () {

    if (defaultValue == null) {

      return null;
    }

    return Long.parseLong(defaultValue);
  }

}
