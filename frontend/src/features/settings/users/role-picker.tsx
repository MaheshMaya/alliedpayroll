import type { RoleView } from "@alliedpayroll/api-client";
import { FormattedMessage } from "react-intl";

import { Checkbox } from "@/shared/ui/checkbox";

interface RolePickerProps {
  roles: readonly RoleView[];
  selected: ReadonlySet<string>;
  onChange: (next: Set<string>) => void;
  /** Visible group label; the fieldset carries it for assistive technology. */
  legendId: string;
  disabled?: boolean;
}

/** The tenant's roles as a checklist, each with how many permissions it grants. */
export function RolePicker({
  roles,
  selected,
  onChange,
  legendId,
  disabled = false,
}: RolePickerProps) {
  const toggle = (id: string, on: boolean) => {
    const next = new Set(selected);
    if (on) next.add(id);
    else next.delete(id);
    onChange(next);
  };
  return (
    <fieldset className="flex flex-col gap-1" disabled={disabled}>
      <legend className="mb-1 text-small text-text-secondary">
        <FormattedMessage id={legendId} />
      </legend>
      {roles.map((role) => (
        <Checkbox
          key={role.id}
          checked={selected.has(role.id)}
          onChange={(event) => {
            toggle(role.id, event.target.checked);
          }}
          label={role.name}
          hint={
            <FormattedMessage
              id="settings.roles.permissionCount"
              values={{ count: role.permissions.length }}
            />
          }
        />
      ))}
    </fieldset>
  );
}
