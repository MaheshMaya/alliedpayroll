import { useQuery } from "@tanstack/react-query";
import { useState, type ReactNode } from "react";
import { FormattedMessage, useIntl } from "react-intl";

import { useHasPermission } from "@/shared/data/bootstrap-context";
import { Avatar } from "@/shared/ui/avatar";
import { Button } from "@/shared/ui/button";
import { Sheet } from "@/shared/ui/sheet";
import { Skeleton } from "@/shared/ui/skeleton";

import { UserAccessActions } from "./user-access-actions";
import { UserRolesEditor } from "./user-roles-editor";
import { UserStatusBadge } from "./user-status-badge";
import { userDetailOptions } from "./users-query";

interface UserDetailSheetProps {
  userId: string;
  onClose: () => void;
}

/**
 * One user: identity, sign-in state, roles and what those roles allow. An administrator with the
 * manage permission edits the roles in place and disables or enables access.
 */
export function UserDetailSheet({ userId, onClose }: UserDetailSheetProps) {
  const intl = useIntl();
  const user = useQuery(userDetailOptions(userId));
  const canManage = useHasPermission("identity:users_manage");
  const [editingRoles, setEditingRoles] = useState(false);

  return (
    <Sheet
      open
      onOpenChange={(open) => {
        if (!open) onClose();
      }}
      title={
        user.data?.displayName ??
        intl.formatMessage({ id: "settings.users.detail.title" })
      }
      description={intl.formatMessage({ id: "settings.users.detail.title" })}
      closeLabel={intl.formatMessage({ id: "settings.users.detail.close" })}
    >
      {user.isPending && (
        <div className="flex flex-col gap-3" aria-busy="true">
          <span className="sr-only">
            <FormattedMessage id="settings.users.detail.loading" />
          </span>
          <Skeleton className="h-10 w-10 rounded-full" />
          <Skeleton className="h-4 w-2/3" />
          <Skeleton className="h-4 w-1/2" />
          <Skeleton className="h-24 w-full" />
        </div>
      )}
      {user.isError && (
        <p role="alert" className="text-body text-status-danger-fg">
          <FormattedMessage id="settings.users.detail.error" />
        </p>
      )}
      {user.isSuccess && (
        <div className="flex flex-col gap-6">
          <div className="flex items-center gap-3">
            <Avatar
              name={user.data.displayName}
              fallback={user.data.username}
              className="size-10"
            />
            <div className="flex min-w-0 flex-col items-start gap-1">
              <span className="font-mono text-small text-text-secondary">
                {user.data.username}
              </span>
              <UserStatusBadge status={user.data.status} />
            </div>
          </div>

          <dl className="grid grid-cols-[auto_1fr] gap-x-6 gap-y-3 text-body">
            <Fact labelId="settings.users.detail.type">
              <FormattedMessage id={`user.type.${user.data.userType}`} />
            </Fact>
            <Fact labelId="settings.users.column.email">
              <span className="font-mono text-small">
                {user.data.emailMasked ?? ""}
              </span>
            </Fact>
            <Fact labelId="settings.users.detail.scope">
              <FormattedMessage id={`user.scope.${user.data.scopeMode}`} />
            </Fact>
            <Fact labelId="settings.users.detail.signIn">
              <FormattedMessage
                id={
                  user.data.linked
                    ? "settings.users.detail.linked"
                    : "settings.users.detail.notLinked"
                }
              />
            </Fact>
          </dl>

          <section aria-labelledby="user-roles" className="flex flex-col gap-2">
            <div className="flex items-center justify-between gap-3">
              <h3
                id="user-roles"
                className="text-overline tracking-[0.04em] text-text-muted uppercase"
              >
                <FormattedMessage id="settings.users.detail.roles" />
              </h3>
              {canManage && !editingRoles && (
                <Button
                  size="sm"
                  variant="ghost"
                  onClick={() => {
                    setEditingRoles(true);
                  }}
                >
                  <FormattedMessage id="settings.users.roles.edit" />
                </Button>
              )}
            </div>
            {editingRoles ? (
              <UserRolesEditor
                user={user.data}
                onDone={() => {
                  setEditingRoles(false);
                }}
              />
            ) : user.data.roles.length === 0 ? (
              <p className="text-small text-text-muted">
                <FormattedMessage id="settings.users.noRoles" />
              </p>
            ) : (
              <ul className="flex flex-wrap gap-1">
                {user.data.roles.map((role) => (
                  <li
                    key={role.id}
                    className="rounded-sm border border-border-default px-2 py-0.5 text-caption text-text-secondary"
                  >
                    {role.name}
                  </li>
                ))}
              </ul>
            )}
          </section>

          <section
            aria-labelledby="user-permissions"
            className="flex flex-col gap-2"
          >
            <h3
              id="user-permissions"
              className="text-overline tracking-[0.04em] text-text-muted uppercase"
            >
              <FormattedMessage id="settings.users.detail.permissions" />
            </h3>
            {user.data.permissions.length === 0 ? (
              <p className="text-small text-text-muted">
                <FormattedMessage id="settings.users.detail.noPermissions" />
              </p>
            ) : (
              <ul className="flex flex-col gap-1 text-body text-text-default">
                {user.data.permissions.map((code) => (
                  <li key={code} className="flex flex-col">
                    <span>
                      {intl.formatMessage({
                        id: `permission.${code}`,
                        defaultMessage: code,
                      })}
                    </span>
                    <span className="font-mono text-caption text-text-muted">
                      {code}
                    </span>
                  </li>
                ))}
              </ul>
            )}
          </section>

          {canManage && (
            <section
              aria-labelledby="user-access"
              className="flex flex-col gap-2 border-t border-border-default pt-5"
            >
              <h3
                id="user-access"
                className="text-overline tracking-[0.04em] text-text-muted uppercase"
              >
                <FormattedMessage id="settings.users.access.title" />
              </h3>
              <UserAccessActions user={user.data} />
            </section>
          )}
        </div>
      )}
    </Sheet>
  );
}

function Fact({ labelId, children }: { labelId: string; children: ReactNode }) {
  return (
    <>
      <dt className="text-small text-text-muted">
        <FormattedMessage id={labelId} />
      </dt>
      <dd className="min-w-0 text-text-default">{children}</dd>
    </>
  );
}
