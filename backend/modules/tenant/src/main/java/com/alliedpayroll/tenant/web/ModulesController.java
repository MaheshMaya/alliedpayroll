package com.alliedpayroll.tenant.web;

import com.alliedpayroll.platform.security.RequiresPermission;
import com.alliedpayroll.platform.web.ApiPaths;
import com.alliedpayroll.tenant.application.ModuleSettingsUseCase;
import com.alliedpayroll.tenant.application.ModuleSettingsUseCase.ModuleState;
import com.alliedpayroll.tenant.domain.TenantPermissions;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPaths.API_V1 + "/modules")
@Tag(
    name = "Modules",
    description = "Optional modules of the tenant; switching one off keeps its data")
@RequiresPermission(TenantPermissions.SETTINGS_MANAGE)
class ModulesController {

  private final ModuleSettingsUseCase modules;
  private final CurrentActor actor;

  ModulesController(ModuleSettingsUseCase modules, CurrentActor actor) {
    this.modules = modules;
    this.actor = actor;
  }

  /**
   * One optional module.
   *
   * @param code the module, such as {@code CUSTOMERS}
   * @param flagKey the key the bootstrap's {@code features} map uses for it
   */
  record ModuleView(String code, String flagKey, boolean enabled) {
    static ModuleView of(ModuleState state) {
      return new ModuleView(state.module().name(), state.module().flagKey(), state.enabled());
    }
  }

  record SetModuleRequest(@NotNull Boolean enabled) {}

  @GetMapping
  @Operation(operationId = "listModules", summary = "Every optional module and whether it is on")
  List<ModuleView> list() {
    return modules.list().stream().map(ModuleView::of).toList();
  }

  @PutMapping("/{code}")
  @Operation(operationId = "setModule", summary = "Switch an optional module on or off")
  ModuleView set(@PathVariable String code, @Valid @RequestBody SetModuleRequest request) {
    return ModuleView.of(
        modules.setEnabled(code, Boolean.TRUE.equals(request.enabled()), actor.require()));
  }
}
