import type { UserDetail } from "@alliedpayroll/api-client";
import { useQuery } from "@tanstack/react-query";
import { useState } from "react";
import { FormattedMessage } from "react-intl";

import { problemMessageId } from "@/shared/data/problem";
import { Button } from "@/shared/ui/button";
import { Skeleton } from "@/shared/ui/skeleton";

import { rolesOptions } from "../roles/roles-query";

import { RolePicker } from "./role-picker";
import { useReplaceUserRoles } from "./users-mutations";

interface UserRolesEditorProps {
  user: UserDetail;
  onDone: () => void;
}

/**
 * Edit a user's roles in place. Saves with the version the sheet showed; if someone changed the
 * user meanwhile the API refuses and the message says to reload.
 */
export function UserRolesEditor({ user, onDone }: UserRolesEditorProps) {
  const roles = useQuery(rolesOptions);
  const replace = useReplaceUserRoles();
  const [selected, setSelected] = useState<Set<string>>(
    () => new Set(user.roles.map((role) => role.id)),
  );

  return (
    <div className="flex flex-col gap-3">
      {roles.isPending && (
        <div className="flex flex-col gap-2" aria-busy="true">
          <Skeleton className="h-6 w-1/2" />
          <Skeleton className="h-6 w-2/3" />
        </div>
      )}
      {roles.isError && (
        <p role="alert" className="text-small text-status-danger-fg">
          <FormattedMessage id="settings.roles.error" />
        </p>
      )}
      {roles.isSuccess && (
        <RolePicker
          roles={roles.data}
          selected={selected}
          onChange={setSelected}
          legendId="settings.users.detail.roles"
          disabled={replace.isPending}
        />
      )}
      {replace.isError && (
        <p
          role="alert"
          className="rounded-md bg-status-danger-bg px-3 py-2 text-small text-status-danger-fg"
        >
          <FormattedMessage id={problemMessageId(replace.error)} />
        </p>
      )}
      <div className="flex gap-3">
        <Button
          size="sm"
          disabled={replace.isPending || !roles.isSuccess}
          onClick={() => {
            replace.mutate(
              { id: user.id, version: user.version, roleIds: [...selected] },
              { onSuccess: onDone },
            );
          }}
        >
          <FormattedMessage id="settings.users.roles.save" />
        </Button>
        <Button size="sm" variant="secondary" onClick={onDone}>
          <FormattedMessage id="settings.users.roles.cancel" />
        </Button>
      </div>
    </div>
  );
}
