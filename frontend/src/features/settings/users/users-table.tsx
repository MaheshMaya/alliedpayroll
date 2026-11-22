import type { UserSummary } from "@alliedpayroll/api-client";
import { Link } from "@tanstack/react-router";
import { FormattedDate, FormattedMessage, useIntl } from "react-intl";

import { Avatar } from "@/shared/ui/avatar";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeaderCell,
  TableRow,
} from "@/shared/ui/table";

import { UserStatusBadge } from "./user-status-badge";

/**
 * The users list. Five fixed columns and no client-side sorting or selection yet, so plain table
 * rows do; the name opens the record's quick look through the URL, which makes it a link.
 */
export function UsersTable({ users }: { users: readonly UserSummary[] }) {
  const intl = useIntl();
  return (
    <Table>
      <TableHead>
        <TableRow className="h-10 hover:bg-bg-subtle">
          <TableHeaderCell>
            <FormattedMessage id="settings.users.column.name" />
          </TableHeaderCell>
          <TableHeaderCell>
            <FormattedMessage id="settings.users.column.roles" />
          </TableHeaderCell>
          <TableHeaderCell>
            <FormattedMessage id="settings.users.column.status" />
          </TableHeaderCell>
          <TableHeaderCell>
            <FormattedMessage id="settings.users.column.email" />
          </TableHeaderCell>
          <TableHeaderCell>
            <FormattedMessage id="settings.users.column.lastSignIn" />
          </TableHeaderCell>
        </TableRow>
      </TableHead>
      <TableBody>
        {users.map((user) => (
          <TableRow key={user.id}>
            <TableCell>
              <Link
                to="/settings/users"
                search={(previous) => ({ ...previous, user: user.id })}
                aria-label={intl.formatMessage(
                  { id: "settings.users.open" },
                  { name: user.displayName },
                )}
                className="group flex min-w-0 items-center gap-3 rounded-sm focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-border-focus"
              >
                <Avatar name={user.displayName} fallback={user.username} />
                <span className="flex min-w-0 flex-col">
                  <span className="truncate text-bodyStrong text-text-default group-hover:underline">
                    {user.displayName}
                  </span>
                  <span className="truncate text-small text-text-muted">
                    {user.username}
                  </span>
                </span>
              </Link>
            </TableCell>
            <TableCell>
              <RoleChips roles={user.roles} />
            </TableCell>
            <TableCell>
              <UserStatusBadge status={user.status} />
            </TableCell>
            <TableCell>
              <span className="font-mono text-small text-text-secondary">
                {user.emailMasked ?? ""}
              </span>
            </TableCell>
            <TableCell>
              {user.lastLoginAt === undefined ? (
                <span className="text-small text-text-muted">
                  <FormattedMessage id="settings.users.never" />
                </span>
              ) : (
                <FormattedDate
                  value={user.lastLoginAt}
                  day="numeric"
                  month="short"
                  year="numeric"
                />
              )}
            </TableCell>
          </TableRow>
        ))}
      </TableBody>
    </Table>
  );
}

export function RoleChips({ roles }: { roles: UserSummary["roles"] }) {
  if (roles.length === 0) {
    return (
      <span className="text-small text-text-muted">
        <FormattedMessage id="settings.users.noRoles" />
      </span>
    );
  }
  return (
    <ul className="flex flex-wrap gap-1">
      {roles.map((role) => (
        <li
          key={role.id}
          className="rounded-sm border border-border-default px-2 py-0.5 text-caption text-text-secondary"
        >
          {role.name}
        </li>
      ))}
    </ul>
  );
}
