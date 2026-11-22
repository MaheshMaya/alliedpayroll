package com.alliedpayroll.platform.security;

/** A browser session was established for this person in the bound tenant. */
public record UserSignedIn(AuthenticatedUser user) {}
