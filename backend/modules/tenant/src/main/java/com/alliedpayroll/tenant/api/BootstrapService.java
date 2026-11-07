package com.alliedpayroll.tenant.api;

/** Builds the bootstrap for the authenticated user in the bound tenant. Used by every channel. */
public interface BootstrapService {

  Bootstrap forCurrentUser();
}
