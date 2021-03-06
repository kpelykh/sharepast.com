package com.sharepast.domain.user;

public enum UserSettingFlags implements Flags {

  SHOW_WELCOME_PAGE ('1', "swp");

  private char defaultCode;
  private String settingKey;

    UserSettingFlags(char defaultCode, String settingKey) {

    this.defaultCode = defaultCode;
    this.settingKey = settingKey;
  }

  public char getDefaultCode () {

    return defaultCode;
  }

  public String getSettingKey () {
    return settingKey;
  }
}
