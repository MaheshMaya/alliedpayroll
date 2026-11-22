package com.alliedpayroll.tenant.application;

import com.alliedpayroll.platform.core.ids.UserId;
import java.util.Map;

/** Port: the labels the bound tenant chose for itself. */
public interface TerminologyOverrides {

  /** The tenant's labels in one locale, by key. */
  Map<String, String> forLocale(String locale);

  void put(String locale, String key, String label, UserId actor);

  void remove(String locale, String key);
}
