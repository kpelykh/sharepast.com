package com.sharepast.domain.user;

import com.sharepast.persistence.Flags;

public enum PrivacyPreferenceFlags implements Flags {

  HIDE_PROFILE_PICTURE('0'),
  HIDE_PUBLIC_NAME('0'),
  HIDE_BIRTHDAY('0'),
  HIDE_LOCATION('0');

  private char defaultCode;

  PrivacyPreferenceFlags (char defaultCode) {

    this.defaultCode = defaultCode;
  }

  public char getDefaultCode () {

    return defaultCode;
  }
}
