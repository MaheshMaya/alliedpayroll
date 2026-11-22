package com.alliedpayroll.identity.infrastructure.security;

import com.alliedpayroll.identity.domain.IdentityPermissions;
import com.alliedpayroll.platform.security.PermissionContributor;
import com.alliedpayroll.platform.security.PermissionDefinition;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
class IdentityPermissionContributor implements PermissionContributor {

  @Override
  public List<PermissionDefinition> permissions() {
    return IdentityPermissions.ALL;
  }
}
