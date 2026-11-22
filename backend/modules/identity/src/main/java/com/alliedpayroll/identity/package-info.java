/**
 * Who can act in a tenant and with which permissions: the tenant-side user record, roles over the
 * permission catalogue, and later data scopes, devices and kiosk credentials. Authentication itself
 * is Keycloak's. Other modules see only {@code com.alliedpayroll.identity.api}.
 */
@ApplicationModule(displayName = "Identity")
@NullMarked
package com.alliedpayroll.identity;

import org.jspecify.annotations.NullMarked;
import org.springframework.modulith.ApplicationModule;
