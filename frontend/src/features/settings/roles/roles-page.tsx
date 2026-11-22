import { useQuery } from "@tanstack/react-query";
import { useNavigate, useSearch } from "@tanstack/react-router";
import { useState } from "react";
import { FormattedMessage } from "react-intl";

import { useHasPermission } from "@/shared/data/bootstrap-context";
import { Badge } from "@/shared/ui/badge";
import { Button } from "@/shared/ui/button";
import { Skeleton } from "@/shared/ui/skeleton";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeaderCell,
  TableRow,
} from "@/shared/ui/table";

import { NewRoleDialog } from "./new-role-dialog";
import { RoleSheet } from "./role-sheet";
import { rolesOptions } from "./roles-query";

/**
 * Settings, Roles: what each role allows. Built-in roles are fixed; the tenant adds roles of its
 * own for the way it works. The open role is URL state.
 */
export function RolesPage() {
  const roles = useQuery(rolesOptions);
  const search = useSearch({ from: "/settings/roles" });
  const navigate = useNavigate({ from: "/settings/roles" });
  const canManage = useHasPermission("identity:roles_manage");
  const [creating, setCreating] = useState(false);

  const open = (id: string | undefined) => {
    void navigate({ search: { role: id }, replace: id === undefined });
  };
  const current = roles.data?.find((role) => role.id === search.role);

  return (
    <div className="flex flex-col gap-6">
      <header className="flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between">
        <div className="flex flex-col gap-1">
          <h2 className="text-h2 text-text-default">
            <FormattedMessage id="settings.roles.title" />
          </h2>
          <p className="text-small text-text-secondary">
            <FormattedMessage id="settings.roles.subtitle" />
          </p>
        </div>
        {canManage && (
          <Button
            className="self-start"
            onClick={() => {
              setCreating(true);
            }}
          >
            <FormattedMessage id="settings.roles.new.open" />
          </Button>
        )}
      </header>

      {roles.isPending && (
        <div className="flex flex-col gap-2" role="status" aria-busy="true">
          <span className="sr-only">
            <FormattedMessage id="settings.roles.loading" />
          </span>
          <Skeleton className="h-10 w-full" />
          <Skeleton className="h-(--table-rowHeight) w-full" />
          <Skeleton className="h-(--table-rowHeight) w-full" />
        </div>
      )}
      {roles.isError && (
        <div
          role="alert"
          className="flex flex-col items-start gap-3 rounded-lg border border-status-danger-bg bg-status-danger-bg px-6 py-5 text-status-danger-fg"
        >
          <p className="text-bodyStrong">
            <FormattedMessage id="settings.roles.error" />
          </p>
          <Button
            variant="secondary"
            size="sm"
            onClick={() => {
              void roles.refetch();
            }}
          >
            <FormattedMessage id="settings.users.error.retry" />
          </Button>
        </div>
      )}
      {roles.isSuccess && (
        <Table>
          <TableHead>
            <TableRow>
              <TableHeaderCell>
                <FormattedMessage id="settings.roles.column.name" />
              </TableHeaderCell>
              <TableHeaderCell>
                <FormattedMessage id="settings.roles.column.kind" />
              </TableHeaderCell>
              <TableHeaderCell className="text-right">
                <FormattedMessage id="settings.roles.column.permissions" />
              </TableHeaderCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {roles.data.map((role) => (
              <TableRow key={role.id}>
                <TableCell>
                  <button
                    type="button"
                    className="flex cursor-pointer flex-col items-start text-left focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-border-focus"
                    onClick={() => {
                      open(role.id);
                    }}
                  >
                    <span className="text-bodyStrong text-text-default underline-offset-2 hover:underline">
                      {role.name}
                    </span>
                    <span className="font-mono text-caption text-text-muted">
                      {role.code}
                    </span>
                  </button>
                </TableCell>
                <TableCell>
                  <Badge status={role.system ? "neutral" : "info"}>
                    <FormattedMessage
                      id={
                        role.system
                          ? "settings.roles.kind.system"
                          : "settings.roles.kind.custom"
                      }
                    />
                  </Badge>
                </TableCell>
                <TableCell className="text-right tabular-nums">
                  {role.permissions.length}
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      )}

      {current !== undefined && (
        <RoleSheet
          key={current.id}
          role={current}
          onClose={() => {
            open(undefined);
          }}
        />
      )}
      {canManage && (
        <NewRoleDialog
          open={creating}
          onOpenChange={setCreating}
          onCreated={(id) => {
            setCreating(false);
            open(id);
          }}
        />
      )}
    </div>
  );
}
