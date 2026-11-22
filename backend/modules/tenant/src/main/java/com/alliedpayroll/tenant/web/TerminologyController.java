package com.alliedpayroll.tenant.web;

import com.alliedpayroll.platform.security.RequiresPermission;
import com.alliedpayroll.platform.web.ApiPaths;
import com.alliedpayroll.tenant.application.TerminologyUseCase;
import com.alliedpayroll.tenant.domain.TenantPermissions;
import com.alliedpayroll.tenant.domain.Terminology;
import com.alliedpayroll.tenant.domain.TerminologyConcepts;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import org.jspecify.annotations.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPaths.API_V1 + "/terminology")
@Tag(
    name = "Terminology",
    description = "The tenant's words for the canonical concepts, per locale")
@RequiresPermission(TenantPermissions.SETTINGS_MANAGE)
class TerminologyController {

  private final TerminologyUseCase terminology;
  private final CurrentActor actor;

  TerminologyController(TerminologyUseCase terminology, CurrentActor actor) {
    this.terminology = terminology;
    this.actor = actor;
  }

  /**
   * One term in the locale.
   *
   * @param label what the key shows
   * @param defaultLabel what it shows without a label of the tenant's own
   * @param overridden whether the tenant chose the label
   */
  record TermView(String key, String label, String defaultLabel, boolean overridden) {}

  record TerminologyView(String locale, List<TermView> terms) {
    static TerminologyView of(Terminology terminology) {
      return new TerminologyView(
          terminology.locale(),
          TerminologyConcepts.KEYS.stream()
              .map(key ->
                  new TermView(
                      key,
                      terminology.label(key),
                      terminology.defaultLabel(key),
                      terminology.isOverridden(key)))
              .toList());
    }
  }

  /**
   * New labels for some terms.
   *
   * @param labels the label per key; a blank or null label goes back to the default
   */
  record RelabelRequest(@NotNull Map<String, @Nullable String> labels) {}

  @GetMapping("/{locale}")
  @Operation(operationId = "getTerminology", summary = "Every relabelable term in one locale")
  TerminologyView get(@PathVariable String locale) {
    return TerminologyView.of(terminology.forLocale(locale));
  }

  @PutMapping("/{locale}")
  @Operation(
      operationId = "relabelTerminology",
      summary = "Set the tenant's labels for some terms in one locale")
  TerminologyView relabel(@PathVariable String locale, @Valid @RequestBody RelabelRequest request) {
    return TerminologyView.of(terminology.relabel(locale, request.labels(), actor.require()));
  }
}
