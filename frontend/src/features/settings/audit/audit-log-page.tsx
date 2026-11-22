import type { AuditEventView } from "@alliedpayroll/api-client";
import { useInfiniteQuery } from "@tanstack/react-query";
import { Link, useNavigate, useSearch } from "@tanstack/react-router";
import { useId } from "react";
import { FormattedDate, FormattedMessage, useIntl } from "react-intl";

import { useBootstrap } from "@/shared/data/bootstrap-context";
import { Badge } from "@/shared/ui/badge";
import { Button } from "@/shared/ui/button";
import { Input } from "@/shared/ui/input";
import { NativeSelect } from "@/shared/ui/native-select";
import { Skeleton } from "@/shared/ui/skeleton";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeaderCell,
  TableRow,
} from "@/shared/ui/table";

import { AuditEventSheet } from "./audit-event-sheet";
import { useActorName, useReasonText } from "./audit-labels";
import {
  auditActions,
  auditListOptions,
  type AuditAction,
} from "./audit-query";

// Denials and failures stand out; everything else is a neutral fact.
const actionTone = (action: AuditAction) =>
  action === "AUTHZ_DENIED" ||
  action === "LOGIN_FAILED" ||
  action === "OTP_FAILED"
    ? "danger"
    : action === "DELETE" || action === "REJECT"
      ? "warning"
      : action === "LOGIN" || action === "APPROVE" || action === "CREATE"
        ? "success"
        : "neutral";

/**
 * Settings, Audit log: the trail newest first with the event type and dates as URL filters,
 * older pages appended on request, and the open event in the URL.
 */
export function AuditLogPage() {
  const intl = useIntl();
  const { tenant } = useBootstrap();
  const search = useSearch({ from: "/settings/audit" });
  const navigate = useNavigate({ from: "/settings/audit" });
  const actorName = useActorName();
  const reasonText = useReasonText();
  const actionId = useId();
  const fromId = useId();
  const toId = useId();
  const events = useInfiniteQuery(
    auditListOptions({
      action: search.action,
      from: search.from,
      to: search.to,
    }),
  );

  const setFilter = (patch: {
    action?: AuditAction | undefined;
    from?: string | undefined;
    to?: string | undefined;
  }) => {
    void navigate({
      search: (previous) => ({ ...previous, ...patch }),
      replace: true,
    });
  };
  const closeDetail = () => {
    void navigate({
      search: (previous) => ({ ...previous, event: undefined }),
      replace: true,
    });
  };

  const rows = events.data?.pages.flatMap((page) => page.items) ?? [];
  const open =
    search.event === undefined
      ? undefined
      : rows.find((row) => row.id === search.event);

  return (
    <div className="flex flex-col gap-6">
      <header className="flex flex-col gap-1">
        <h2 className="text-h2 text-text-default">
          <FormattedMessage id="settings.audit.title" />
        </h2>
        <p className="text-small text-text-secondary">
          <FormattedMessage
            id="settings.audit.subtitle"
            values={{ tenant: tenant.name }}
          />
        </p>
      </header>

      <div className="flex flex-col gap-3 md:flex-row md:items-end">
        <div className="flex flex-col gap-1">
          <label htmlFor={actionId} className="text-small text-text-secondary">
            <FormattedMessage id="settings.audit.filter.action" />
          </label>
          <NativeSelect
            id={actionId}
            className="min-w-52"
            value={search.action ?? ""}
            onChange={(event) => {
              setFilter({
                action: auditActions.find((a) => a === event.target.value),
              });
            }}
          >
            <option value="">
              {intl.formatMessage({ id: "settings.audit.filter.all" })}
            </option>
            {auditActions.map((action) => (
              <option key={action} value={action}>
                {intl.formatMessage({ id: `audit.action.${action}` })}
              </option>
            ))}
          </NativeSelect>
        </div>
        <div className="flex flex-col gap-1">
          <label htmlFor={fromId} className="text-small text-text-secondary">
            <FormattedMessage id="settings.audit.filter.from" />
          </label>
          <Input
            id={fromId}
            type="date"
            value={search.from ?? ""}
            onChange={(event) => {
              setFilter({
                from:
                  event.target.value === "" ? undefined : event.target.value,
              });
            }}
            className="w-44"
          />
        </div>
        <div className="flex flex-col gap-1">
          <label htmlFor={toId} className="text-small text-text-secondary">
            <FormattedMessage id="settings.audit.filter.to" />
          </label>
          <Input
            id={toId}
            type="date"
            value={search.to ?? ""}
            onChange={(event) => {
              setFilter({
                to: event.target.value === "" ? undefined : event.target.value,
              });
            }}
            className="w-44"
          />
        </div>
      </div>

      {events.isPending && (
        <div className="flex flex-col gap-2" role="status" aria-busy="true">
          <span className="sr-only">
            <FormattedMessage id="settings.audit.loading" />
          </span>
          <Skeleton className="h-10 w-full" />
          <Skeleton className="h-(--table-rowHeight) w-full" />
          <Skeleton className="h-(--table-rowHeight) w-full" />
          <Skeleton className="h-(--table-rowHeight) w-full" />
        </div>
      )}

      {events.isError && (
        <div
          role="alert"
          className="flex flex-col items-start gap-3 rounded-lg border border-status-danger-bg bg-status-danger-bg px-6 py-5 text-status-danger-fg"
        >
          <p className="text-bodyStrong">
            <FormattedMessage id="settings.audit.error.title" />
          </p>
          <Button
            variant="secondary"
            size="sm"
            onClick={() => {
              void events.refetch();
            }}
          >
            <FormattedMessage id="settings.audit.error.retry" />
          </Button>
        </div>
      )}

      {events.isSuccess && rows.length === 0 && (
        <div className="rounded-lg border border-dashed border-border-default px-6 py-10 text-center">
          <p className="text-h3 text-text-default">
            <FormattedMessage id="settings.audit.empty.title" />
          </p>
          <p className="mt-1 text-body text-text-secondary">
            <FormattedMessage id="settings.audit.empty.body" />
          </p>
        </div>
      )}

      {events.isSuccess && rows.length > 0 && (
        <>
          <Table>
            <TableHead>
              <TableRow className="h-10 hover:bg-bg-subtle">
                <TableHeaderCell>
                  <FormattedMessage id="settings.audit.column.when" />
                </TableHeaderCell>
                <TableHeaderCell>
                  <FormattedMessage id="settings.audit.column.who" />
                </TableHeaderCell>
                <TableHeaderCell>
                  <FormattedMessage id="settings.audit.column.what" />
                </TableHeaderCell>
                <TableHeaderCell>
                  <FormattedMessage id="settings.audit.column.on" />
                </TableHeaderCell>
                <TableHeaderCell>
                  <FormattedMessage id="settings.audit.column.detail" />
                </TableHeaderCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {rows.map((event) => (
                <EventRow
                  key={event.id}
                  event={event}
                  actorName={actorName(event)}
                  reason={reasonText(event)}
                />
              ))}
            </TableBody>
          </Table>
          {events.hasNextPage && (
            <div className="flex justify-center">
              <Button
                variant="secondary"
                disabled={events.isFetchingNextPage}
                onClick={() => {
                  void events.fetchNextPage();
                }}
              >
                <FormattedMessage id="settings.audit.loadMore" />
              </Button>
            </div>
          )}
        </>
      )}

      {open !== undefined && (
        <AuditEventSheet event={open} onClose={closeDetail} />
      )}
    </div>
  );
}

function EventRow({
  event,
  actorName,
  reason,
}: {
  event: AuditEventView;
  actorName: string;
  reason: string | undefined;
}) {
  const intl = useIntl();
  const when = intl.formatDate(event.occurredAt, {
    day: "numeric",
    month: "short",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
  return (
    <TableRow>
      <TableCell className="whitespace-nowrap">
        <Link
          to="/settings/audit"
          search={(previous) => ({ ...previous, event: event.id })}
          aria-label={intl.formatMessage(
            { id: "settings.audit.open" },
            { when },
          )}
          className="rounded-sm text-text-default hover:underline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-border-focus"
        >
          <FormattedDate
            value={event.occurredAt}
            day="numeric"
            month="short"
            year="numeric"
            hour="2-digit"
            minute="2-digit"
          />
        </Link>
      </TableCell>
      <TableCell>{actorName}</TableCell>
      <TableCell>
        <Badge status={actionTone(event.action)} dot>
          <FormattedMessage id={`audit.action.${event.action}`} />
        </Badge>
      </TableCell>
      <TableCell>
        {event.entityType === undefined ? (
          <span className="text-small text-text-muted">
            <FormattedMessage
              id={`audit.channel.${event.channel}`}
              defaultMessage={event.channel}
            />
          </span>
        ) : (
          <span className="font-mono text-small text-text-secondary">
            {event.entityType}
            {event.entityLabel !== undefined && ` · ${event.entityLabel}`}
          </span>
        )}
      </TableCell>
      <TableCell className="max-w-md">
        <span className="line-clamp-2 text-small text-text-secondary">
          {reason ??
            (event.changedFields.length > 0
              ? event.changedFields.join(", ")
              : "")}
        </span>
      </TableCell>
    </TableRow>
  );
}
