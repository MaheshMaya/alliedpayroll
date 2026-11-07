/**
 * Browser token handler: the SPA never sees tokens. Login is OIDC Authorization Code with PKCE
 * against Keycloak, the result is a session cookie, and every API call rides on that cookie with a
 * CSRF token. Shapes data only; no business rules, no tables.
 */
@ApplicationModule(displayName = "Web channel")
@NullMarked
package com.alliedpayroll.webchannel;

import org.jspecify.annotations.NullMarked;
import org.springframework.modulith.ApplicationModule;
