package com.alliedpayroll.platform.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Client ids registered in Keycloak per channel, so a bearer token's {@code azp} tells the channel.
 *
 * @param mobileClientId the Flutter app's public client
 * @param kioskClientId the kiosk mode's client
 */
@ConfigurationProperties("alliedpayroll.security")
public record SecurityProperties(String mobileClientId, String kioskClientId) {}
