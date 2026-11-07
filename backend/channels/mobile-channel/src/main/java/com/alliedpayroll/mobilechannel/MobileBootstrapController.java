package com.alliedpayroll.mobilechannel;

import com.alliedpayroll.platform.web.ApiPaths;
import com.alliedpayroll.tenant.api.Bootstrap;
import com.alliedpayroll.tenant.api.BootstrapService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.Duration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** The canonical bootstrap plus what only the app needs: sync cadence and offline limits. */
@RestController
@RequestMapping(ApiPaths.MOBILE_V1 + "/bootstrap")
@Tag(name = "Mobile", description = "Endpoints shaped for the Flutter app")
class MobileBootstrapController {

  private final BootstrapService bootstrap;

  MobileBootstrapController(BootstrapService bootstrap) {
    this.bootstrap = bootstrap;
  }

  @GetMapping
  @Operation(operationId = "getMobileBootstrap", summary = "Bootstrap for the mobile app")
  MobileBootstrap get() {
    return new MobileBootstrap(
        bootstrap.forCurrentUser(), new SyncPolicy(Duration.ofMinutes(15), Duration.ofDays(7)));
  }

  record MobileBootstrap(Bootstrap bootstrap, SyncPolicy sync) {}

  record SyncPolicy(Duration deltaInterval, Duration offlinePunchRetention) {}
}
