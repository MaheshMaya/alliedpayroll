package com.alliedpayroll.identity.api;

/** What kind of identity a user row is; it shapes defaults, never permissions. */
public enum UserType {
  ADMIN,
  MANAGER,
  CLIENT_CONTACT,
  EMPLOYEE,
  SERVICE_ACCOUNT
}
