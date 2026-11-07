package com.alliedpayroll;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

/** Fails the build when a module reaches into another module's internals or a cycle appears. */
class ModulithStructureTest {

  private static final ApplicationModules MODULES = ApplicationModules.of(BackendApplication.class);

  @Test
  void modulesRespectTheirBoundaries() {
    MODULES.verify();
  }
}
