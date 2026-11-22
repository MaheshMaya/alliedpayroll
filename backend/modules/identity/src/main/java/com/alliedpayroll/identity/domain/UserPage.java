package com.alliedpayroll.identity.domain;

import java.util.List;

public record UserPage(List<AppUser> items, long total) {
  public UserPage {
    items = List.copyOf(items);
  }
}
