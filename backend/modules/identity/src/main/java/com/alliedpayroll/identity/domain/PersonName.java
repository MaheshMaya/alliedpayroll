package com.alliedpayroll.identity.domain;

/**
 * A display name split the way the identity provider stores it. The last word is the family name; a
 * person with a single name has none.
 */
public record PersonName(String first, String last) {

  public static PersonName of(String displayName) {
    String trimmed = displayName.trim().replaceAll("\\s+", " ");
    int space = trimmed.lastIndexOf(' ');
    if (space < 0) {
      return new PersonName(trimmed, "");
    }
    return new PersonName(trimmed.substring(0, space), trimmed.substring(space + 1));
  }
}
