package com.alliedpayroll.tenant.web;

import com.alliedpayroll.platform.web.ApiPaths;
import com.alliedpayroll.tenant.api.Bootstrap;
import com.alliedpayroll.tenant.api.BootstrapService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPaths.API_V1 + "/bootstrap")
@Tag(
    name = "Bootstrap",
    description = "First call after sign-in: tenant, user, permissions, modules")
class BootstrapController {

  private final BootstrapService bootstrap;

  BootstrapController(BootstrapService bootstrap) {
    this.bootstrap = bootstrap;
  }

  @GetMapping
  @Operation(operationId = "getBootstrap", summary = "Bootstrap for the signed-in user")
  Bootstrap get() {
    return bootstrap.forCurrentUser();
  }
}
