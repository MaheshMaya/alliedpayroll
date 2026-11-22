import {
  listAuditEvents,
  type PageResponseAuditEventView,
} from "@alliedpayroll/api-client";
import { infiniteQueryOptions } from "@tanstack/react-query";

import { ApiError } from "@/shared/data/session";

export const auditActions = [
  "CREATE",
  "UPDATE",
  "DELETE",
  "STATE_CHANGE",
  "LOGIN",
  "LOGIN_FAILED",
  "LOGOUT",
  "OTP_SENT",
  "OTP_FAILED",
  "AUTHZ_DENIED",
  "PII_REVEAL",
  "EXPORT",
  "IMPORT_COMMIT",
  "APPROVE",
  "REJECT",
] as const;
export type AuditAction = (typeof auditActions)[number];

export const PAGE_SIZE = 50;

export interface AuditFilters {
  action?: AuditAction | undefined;
  /** Calendar dates in the browser's zone; sent as the day's start and the next day's start. */
  from?: string | undefined;
  to?: string | undefined;
}

export const auditKeys = {
  all: ["audit-events"] as const,
  list: (filters: AuditFilters) => ["audit-events", "list", filters] as const,
};

function dayStart(date: string): string {
  return new Date(`${date}T00:00:00`).toISOString();
}

function nextDayStart(date: string): string {
  const day = new Date(`${date}T00:00:00`);
  day.setDate(day.getDate() + 1);
  return day.toISOString();
}

export function auditListOptions(filters: AuditFilters) {
  return infiniteQueryOptions<
    PageResponseAuditEventView,
    ApiError,
    { pages: PageResponseAuditEventView[]; pageParams: unknown[] },
    ReturnType<typeof auditKeys.list>,
    string | undefined
  >({
    queryKey: auditKeys.list(filters),
    initialPageParam: undefined,
    queryFn: async ({ pageParam, signal }) => {
      const { data, response } = await listAuditEvents({
        query: {
          ...(filters.action === undefined
            ? {}
            : { "filter[action]": filters.action }),
          ...(filters.from === undefined
            ? {}
            : { from: dayStart(filters.from) }),
          ...(filters.to === undefined ? {} : { to: nextDayStart(filters.to) }),
          ...(pageParam === undefined ? {} : { cursor: pageParam }),
          size: PAGE_SIZE,
        },
        signal,
      });
      if (data === undefined) {
        throw new ApiError(response?.status ?? 0, "/api/v1/audit-events");
      }
      return data;
    },
    getNextPageParam: (last) => last.nextCursor,
  });
}
