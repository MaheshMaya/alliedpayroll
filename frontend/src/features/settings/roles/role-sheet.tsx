import type { RoleView } from "@alliedpayroll/api-client";
import { useQuery } from "@tanstack/react-query";
import { useState } from "react";
import { FormattedMessage, useIntl } from "react-intl";

import { useHasPermission } from "@/shared/data/bootstrap-context";
import { problemMessageId } from "@/shared/data/problem";
import { Button } from "@/shared/ui/button";
import { Sheet } from "@/shared/ui/sheet";
import { Skeleton } from "@/shared/ui/skeleton";

import { PermissionChecklist } from "./permission-checklist";
import { permissionsOptions, useReplaceRolePermissions } from "./roles-query";

interface RoleSheetProps {
  role: RoleView;
  onClose: () => void;
}

/**
 * One role and what it allows. A role the tenant created can be changed here; a built-in role
 * explains why it cannot, so nobody looks for a hidden save button.
 */
export function RoleSheet({ role, onClose }: RoleSheetProps) {
  const intl = useIntl();
  const catalogue = useQuery(permissionsOptions);
  const canManage = useHasPermission("identity:roles_manage");
  const save = useReplaceRolePermissions();
  const editable = canManage && !role.system;
  const [selected, setSelected] = useState<Set<string>>(
    () => new Set(role.permissions),
  );
  const changed =
    selected.size !== role.permissions.length ||
    role.permissions.some((code) => !selected.has(code));

  return (
    <Sheet
      open
      onOpenChange={(open) => {
        if (!open) onClose();
      }}
      title={role.name}
      description={intl.formatMessage({ id: "settings.roles.detail.title" })}
      closeLabel={intl.formatMessage({ id: "settings.roles.detail.close" })}
    >
      <div className="flex flex-col gap-1">
        <span className="font-mono text-small text-text-secondary">
          {role.code}
        </span>
        {role.description !== undefined && (
          <p className="text-body text-text-secondary">{role.description}</p>
        )}
      </div>
      {role.system && (
        <p className="rounded-md bg-bg-subtle px-3 py-2 text-small text-text-secondary">
          <FormattedMessage id="settings.roles.systemNote" />
        </p>
      )}
      {catalogue.isPending && (
        <div className="flex flex-col gap-2" aria-busy="true">
          <Skeleton className="h-6 w-2/3" />
          <Skeleton className="h-6 w-1/2" />
          <Skeleton className="h-6 w-2/3" />
        </div>
      )}
      {catalogue.isError && (
        <p role="alert" className="text-small text-status-danger-fg">
          <FormattedMessage id="settings.roles.error" />
        </p>
      )}
      {catalogue.isSuccess && (
        <PermissionChecklist
          catalogue={catalogue.data}
          selected={selected}
          onChange={setSelected}
          readOnly={!editable || save.isPending}
        />
      )}
      {save.isError && (
        <p
          role="alert"
          className="rounded-md bg-status-danger-bg px-3 py-2 text-small text-status-danger-fg"
        >
          <FormattedMessage id={problemMessageId(save.error)} />
        </p>
      )}
      {editable && (
        <div className="flex gap-3">
          <Button
            disabled={!changed || save.isPending}
            onClick={() => {
              save.mutate({ id: role.id, permissions: [...selected] });
            }}
          >
            <FormattedMessage id="settings.roles.save" />
          </Button>
          {save.isSuccess && !changed && (
            <p
              role="status"
              className="self-center text-small text-text-secondary"
            >
              <FormattedMessage id="settings.roles.saved" />
            </p>
          )}
        </div>
      )}
    </Sheet>
  );
}
