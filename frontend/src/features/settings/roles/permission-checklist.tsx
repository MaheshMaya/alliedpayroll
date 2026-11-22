import type { PermissionDefinition } from "@alliedpayroll/api-client";
import { FormattedMessage, useIntl } from "react-intl";

import { Checkbox } from "@/shared/ui/checkbox";

import { byModule } from "./roles-query";

interface PermissionChecklistProps {
  catalogue: readonly PermissionDefinition[];
  selected: ReadonlySet<string>;
  onChange?: (next: Set<string>) => void;
  /** Read-only: built-in roles, or a viewer without the right to change roles. */
  readOnly?: boolean;
}

/** The permission catalogue grouped by module, each entry with its plain-language meaning. */
export function PermissionChecklist({
  catalogue,
  selected,
  onChange,
  readOnly = false,
}: PermissionChecklistProps) {
  const intl = useIntl();
  const toggle = (code: string, on: boolean) => {
    const next = new Set(selected);
    if (on) next.add(code);
    else next.delete(code);
    onChange?.(next);
  };
  return (
    <div className="flex flex-col gap-4">
      {byModule(catalogue).map(([module, permissions]) => (
        <fieldset
          key={module}
          className="flex flex-col gap-1"
          disabled={readOnly}
        >
          <legend className="mb-1 text-overline tracking-[0.04em] text-text-muted uppercase">
            {intl.formatMessage({
              id: `module.${module}`,
              defaultMessage: module,
            })}
          </legend>
          {permissions.map((permission) => (
            <Checkbox
              key={permission.code}
              checked={selected.has(permission.code)}
              onChange={(event) => {
                toggle(permission.code, event.target.checked);
              }}
              label={intl.formatMessage({
                id: `permission.${permission.code}`,
                defaultMessage: permission.code,
              })}
              hint={<span className="font-mono">{permission.code}</span>}
            />
          ))}
        </fieldset>
      ))}
      {catalogue.length === 0 && (
        <p className="text-small text-text-muted">
          <FormattedMessage id="settings.roles.noPermissions" />
        </p>
      )}
    </div>
  );
}
