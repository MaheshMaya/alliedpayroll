import {
  getUser,
  listUsers,
  type PageResponseUserSummary,
  type UserDetail,
} from "@alliedpayroll/api-client";
import { keepPreviousData, queryOptions } from "@tanstack/react-query";

import { ApiError } from "@/shared/data/session";

export const userStatuses = [
  "INVITED",
  "ACTIVE",
  "DISABLED",
  "LOCKED",
] as const;
export type UserStatus = (typeof userStatuses)[number];

export const PAGE_SIZE = 25;

export interface UsersListParams {
  q?: string | undefined;
  status?: UserStatus | undefined;
  /** 1-based, as shown; the API counts from 0. */
  page: number;
}

export const usersKeys = {
  all: ["users"] as const,
  list: (params: UsersListParams) => ["users", "list", params] as const,
  detail: (id: string) => ["users", "detail", id] as const,
};

export function usersListOptions(params: UsersListParams) {
  return queryOptions<PageResponseUserSummary, ApiError>({
    queryKey: usersKeys.list(params),
    queryFn: async ({ signal }) => {
      const { data, response } = await listUsers({
        query: {
          ...(params.q === undefined ? {} : { q: params.q }),
          ...(params.status === undefined
            ? {}
            : { "filter[status]": params.status }),
          page: params.page - 1,
          size: PAGE_SIZE,
        },
        signal,
      });
      if (data === undefined) {
        throw new ApiError(response?.status ?? 0, "/api/v1/users");
      }
      return data;
    },
    placeholderData: keepPreviousData,
  });
}

export function userDetailOptions(id: string) {
  return queryOptions<UserDetail, ApiError>({
    queryKey: usersKeys.detail(id),
    queryFn: async ({ signal }) => {
      const { data, response } = await getUser({ path: { id }, signal });
      if (data === undefined) {
        throw new ApiError(response?.status ?? 0, `/api/v1/users/${id}`);
      }
      return data;
    },
  });
}
