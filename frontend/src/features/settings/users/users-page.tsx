import { useQuery } from "@tanstack/react-query";
import { useNavigate, useSearch } from "@tanstack/react-router";
import { useEffect, useId, useState } from "react";
import { FormattedMessage, useIntl } from "react-intl";

import { useBootstrap } from "@/shared/data/bootstrap-context";
import { Button } from "@/shared/ui/button";
import { Input } from "@/shared/ui/input";
import { NativeSelect } from "@/shared/ui/native-select";
import { Skeleton } from "@/shared/ui/skeleton";

import { UserDetailSheet } from "./user-detail-sheet";
import {
  PAGE_SIZE,
  userStatuses,
  usersListOptions,
  type UserStatus,
} from "./users-query";
import { UsersTable } from "./users-table";

const SEARCH_DEBOUNCE_MS = 300;

/**
 * Settings, Users: who can sign in and what their roles allow. Read-only in this slice; inviting
 * and editing arrive with the next one. Filters, page and the open record are URL state.
 */
export function UsersPage() {
  const intl = useIntl();
  const { tenant } = useBootstrap();
  const search = useSearch({ from: "/settings/users" });
  const navigate = useNavigate({ from: "/settings/users" });
  const page = search.page ?? 1;
  const users = useQuery(
    usersListOptions({ q: search.q, status: search.status, page }),
  );
  const searchId = useId();
  const statusId = useId();
  const [term, setTerm] = useState(search.q ?? "");

  useEffect(() => {
    const normalised = term.trim() === "" ? undefined : term.trim();
    if (normalised === search.q) return;
    const handle = setTimeout(() => {
      void navigate({
        search: (previous) => ({ ...previous, q: normalised, page: undefined }),
        replace: true,
      });
    }, SEARCH_DEBOUNCE_MS);
    return () => {
      clearTimeout(handle);
    };
  }, [term, search.q, navigate]);

  const setStatus = (value: string) => {
    const status = userStatuses.find((candidate) => candidate === value);
    void navigate({
      search: (previous) => ({ ...previous, status, page: undefined }),
      replace: true,
    });
  };
  const goTo = (next: number) => {
    void navigate({
      search: (previous) => ({
        ...previous,
        page: next === 1 ? undefined : next,
      }),
    });
  };
  const closeDetail = () => {
    void navigate({
      search: (previous) => ({ ...previous, user: undefined }),
      replace: true,
    });
  };

  const total = users.data?.totalCount ?? 0;
  const pages = Math.max(1, Math.ceil(total / PAGE_SIZE));

  return (
    <div className="flex flex-col gap-6">
      <header className="flex flex-col gap-1">
        <h2 className="text-h2 text-text-default">
          <FormattedMessage id="settings.users.title" />
        </h2>
        <p className="text-small text-text-secondary">
          <FormattedMessage
            id="settings.users.subtitle"
            values={{ tenant: tenant.name }}
          />
        </p>
      </header>

      <div className="flex flex-col gap-3 md:flex-row md:items-end">
        <div className="flex min-w-0 flex-col gap-1 md:w-sm">
          <label htmlFor={searchId} className="text-small text-text-secondary">
            <FormattedMessage id="settings.users.search" />
          </label>
          <Input
            id={searchId}
            type="search"
            value={term}
            onChange={(event) => {
              setTerm(event.target.value);
            }}
          />
        </div>
        <div className="flex flex-col gap-1">
          <label htmlFor={statusId} className="text-small text-text-secondary">
            <FormattedMessage id="settings.users.filter.status" />
          </label>
          <NativeSelect
            id={statusId}
            className="min-w-48"
            value={search.status ?? ""}
            onChange={(event) => {
              setStatus(event.target.value);
            }}
          >
            <option value="">
              {intl.formatMessage({ id: "settings.users.filter.all" })}
            </option>
            {userStatuses.map((status: UserStatus) => (
              <option key={status} value={status}>
                {intl.formatMessage({ id: `user.status.${status}` })}
              </option>
            ))}
          </NativeSelect>
        </div>
      </div>

      {users.isPending && (
        <div className="flex flex-col gap-2" role="status" aria-busy="true">
          <span className="sr-only">
            <FormattedMessage id="settings.users.loading" />
          </span>
          <Skeleton className="h-10 w-full" />
          <Skeleton className="h-(--table-rowHeight) w-full" />
          <Skeleton className="h-(--table-rowHeight) w-full" />
          <Skeleton className="h-(--table-rowHeight) w-full" />
        </div>
      )}

      {users.isError && (
        <div
          role="alert"
          className="flex flex-col items-start gap-3 rounded-lg border border-status-danger-bg bg-status-danger-bg px-6 py-5 text-status-danger-fg"
        >
          <p className="text-bodyStrong">
            <FormattedMessage id="settings.users.error.title" />
          </p>
          <Button
            variant="secondary"
            size="sm"
            onClick={() => {
              void users.refetch();
            }}
          >
            <FormattedMessage id="settings.users.error.retry" />
          </Button>
        </div>
      )}

      {users.isSuccess && (
        <>
          <p className="text-small text-text-muted" aria-live="polite">
            <FormattedMessage
              id="settings.users.count"
              values={{ count: total }}
            />
          </p>
          {users.data.items.length === 0 ? (
            <div className="rounded-lg border border-dashed border-border-default px-6 py-10 text-center">
              <p className="text-h3 text-text-default">
                <FormattedMessage id="settings.users.empty.title" />
              </p>
              <p className="mt-1 text-body text-text-secondary">
                <FormattedMessage id="settings.users.empty.body" />
              </p>
            </div>
          ) : (
            <UsersTable users={users.data.items} />
          )}
          {pages > 1 && (
            <nav
              aria-label={intl.formatMessage(
                { id: "settings.users.page.of" },
                { page, pages },
              )}
              className="flex items-center justify-end gap-3"
            >
              <span className="text-small text-text-muted">
                <FormattedMessage
                  id="settings.users.page.of"
                  values={{ page, pages }}
                />
              </span>
              <Button
                variant="secondary"
                size="sm"
                disabled={page <= 1}
                onClick={() => {
                  goTo(page - 1);
                }}
              >
                <FormattedMessage id="settings.users.page.previous" />
              </Button>
              <Button
                variant="secondary"
                size="sm"
                disabled={page >= pages}
                onClick={() => {
                  goTo(page + 1);
                }}
              >
                <FormattedMessage id="settings.users.page.next" />
              </Button>
            </nav>
          )}
        </>
      )}

      {search.user !== undefined && (
        <UserDetailSheet userId={search.user} onClose={closeDetail} />
      )}
    </div>
  );
}
