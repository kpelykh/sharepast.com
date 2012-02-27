package com.sharepast.domain.user;

import com.sharepast.persistence.Flags;

public enum UserNotificationFlags implements Flags {

  RECEIVE_PERSONAL_MESSAGES_NOTIFICATIONS('1', "cpm"),
  RECEIVE_SUMMARY_NOTIFICATIONS('1', "cs"),
  RECEIVE_WHATS_HAPPENING_NEWSLETTER('1', "whn");

  private char defaultCode;
  private String notificationKey;

    UserNotificationFlags(char defaultCode, String notificationKey) {

    this.defaultCode = defaultCode;
    this.notificationKey = notificationKey;
  }

  public char getDefaultCode () {

    return defaultCode;
  }

  public String getNotificationKey () {
    return notificationKey;
  }
}
