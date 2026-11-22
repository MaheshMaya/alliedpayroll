package com.alliedpayroll.identity.infrastructure.security;

import com.alliedpayroll.identity.domain.PermissionStore;
import com.alliedpayroll.platform.security.PermissionContributor;
import com.alliedpayroll.platform.security.PermissionDefinition;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Mirrors the code-declared permissions into the catalogue table before anything creates roles,
 * which reference the codes by foreign key.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class PermissionCatalogueReconciler implements ApplicationRunner {

  private static final Logger log = LoggerFactory.getLogger(PermissionCatalogueReconciler.class);

  private final List<PermissionContributor> contributors;
  private final PermissionStore store;

  PermissionCatalogueReconciler(List<PermissionContributor> contributors, PermissionStore store) {
    this.contributors = contributors;
    this.store = store;
  }

  @Override
  public void run(ApplicationArguments args) {
    List<PermissionDefinition> all = new ArrayList<>();
    for (PermissionContributor contributor : contributors) {
      all.addAll(contributor.permissions());
    }
    store.upsert(all);
    log.info("permission catalogue reconciled: {} permissions", all.size());
  }
}
