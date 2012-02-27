package com.sharepast.domain.user;

public enum Gender {

  MALE("he", "his"), FEMALE("she", "her"), UNCERTAIN("he/she", "his/her");

  private String pronoun;
  private String possessive;

  private Gender (String pronoun, String possessive) {

    this.pronoun = pronoun;
    this.possessive = possessive;
  }

  public String asPronoun () {

    return pronoun;
  }

  public String getPossessive () {

    return possessive;
  }
}
