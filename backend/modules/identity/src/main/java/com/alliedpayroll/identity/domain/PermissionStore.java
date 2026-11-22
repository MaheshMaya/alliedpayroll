package com.alliedpayroll.identity.domain;

import com.alliedpayroll.platform.security.PermissionDefinition;
import java.util.List;

/** The platform-owned catalogue table that mirrors the code-declared permissions. */
public interface PermissionStore {

  void upsert(List<PermissionDefinition> permissions);

  List<PermissionDefinition> findAll();
}
