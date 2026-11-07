package com.alliedpayroll.platform.security;

/** Where a request came from. Channel policy decides what each may call. */
public enum Channel {
  WEB,
  MOBILE,
  KIOSK,
  DEVICE,
  PARTNER,
  SYSTEM
}
