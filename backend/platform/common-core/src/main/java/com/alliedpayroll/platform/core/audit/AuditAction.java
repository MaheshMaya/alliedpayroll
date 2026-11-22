package com.alliedpayroll.platform.core.audit;

/** What happened. Data changes and security events share one trail. */
public enum AuditAction {
  CREATE,
  UPDATE,
  DELETE,
  STATE_CHANGE,
  LOGIN,
  LOGIN_FAILED,
  LOGOUT,
  OTP_SENT,
  OTP_FAILED,
  AUTHZ_DENIED,
  PII_REVEAL,
  EXPORT,
  IMPORT_COMMIT,
  APPROVE,
  REJECT;

  /** Rows about who may do what; reading them needs the security permission. */
  public boolean isSecurityEvent() {
    return switch (this) {
      case LOGIN, LOGIN_FAILED, LOGOUT, OTP_SENT, OTP_FAILED, AUTHZ_DENIED, PII_REVEAL -> true;
      default -> false;
    };
  }
}
