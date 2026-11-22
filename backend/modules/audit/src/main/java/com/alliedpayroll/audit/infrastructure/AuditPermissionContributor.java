package com.alliedpayroll.audit.infrastructure;

import com.alliedpayroll.audit.domain.AuditPermissions;
import com.alliedpayroll.platform.security.PermissionContributor;
import com.alliedpayroll.platform.security.PermissionDefinition;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
class AuditPermissionContributor implements PermissionContributor {

  @Override
  public List<PermissionDefinition> permissions() {
    return AuditPermissions.ALL;
  }
}
