package com.alliedpayroll.platform.security;

import java.util.List;

/**
 * A module's part of the permission catalogue. The identity module collects every contributor at
 * startup and reconciles the catalogue table, so roles can only reference codes that exist.
 */
@FunctionalInterface
public interface PermissionContributor {

  List<PermissionDefinition> permissions();
}
