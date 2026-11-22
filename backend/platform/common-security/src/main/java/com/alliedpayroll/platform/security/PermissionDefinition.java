package com.alliedpayroll.platform.security;

/**
 * One entry of the permission catalogue, declared in code by the module that checks it.
 *
 * @param code {@code module:action}, the value used in {@link RequiresPermission}
 * @param moduleCode the owning module, for grouping in the catalogue
 * @param descriptionKey message key of the human description
 * @param moneyMoving whether exercising it moves money, which makes it a maker-checker candidate
 */
public record PermissionDefinition(
    String code, String moduleCode, String descriptionKey, boolean moneyMoving) {

  public static PermissionDefinition of(String code, String moduleCode) {
    return new PermissionDefinition(code, moduleCode, "permission." + code, false);
  }
}
