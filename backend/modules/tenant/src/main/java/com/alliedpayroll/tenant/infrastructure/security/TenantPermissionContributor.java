package com.alliedpayroll.tenant.infrastructure.security;

import com.alliedpayroll.platform.security.PermissionContributor;
import com.alliedpayroll.platform.security.PermissionDefinition;
import com.alliedpayroll.tenant.domain.TenantPermissions;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
class TenantPermissionContributor implements PermissionContributor {

  @Override
  public List<PermissionDefinition> permissions() {
    return TenantPermissions.ALL;
  }
}
