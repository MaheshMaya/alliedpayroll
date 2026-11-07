import {
  getBootstrap,
  getSession,
  type Bootstrap,
  type SessionView,
} from "@alliedpayroll/api-client";
import { queryOptions, useQuery } from "@tanstack/react-query";

export class ApiError extends Error {
  readonly status: number;
  readonly url: string;

  constructor(status: number, url: string) {
    super(`${String(status)} from ${url}`);
    this.name = "ApiError";
    this.status = status;
    this.url = url;
  }
}

/** `null` means the browser has no session; the gate turns that into the login redirect. */
export const sessionOptions = queryOptions<SessionView | null, ApiError>({
  queryKey: ["session"],
  queryFn: async ({ signal }) => {
    const { data, response } = await getSession({ signal });
    const status = response?.status ?? 0;
    if (status === 401) return null;
    if (data === undefined) throw new ApiError(status, "/auth/session");
    return data;
  },
  staleTime: 5 * 60_000,
  retry: false,
});

export const bootstrapOptions = queryOptions<Bootstrap, ApiError>({
  queryKey: ["bootstrap"],
  queryFn: async ({ signal }) => {
    const { data, response } = await getBootstrap({ signal });
    if (data === undefined) {
      throw new ApiError(response?.status ?? 0, "/api/v1/bootstrap");
    }
    return data;
  },
  staleTime: 10 * 60_000,
});

export function useSession() {
  return useQuery(sessionOptions);
}

export function useBootstrapQuery() {
  return useQuery(bootstrapOptions);
}

export function redirectToLogin(returnTo: string): void {
  const target = `/auth/login?return_to=${encodeURIComponent(returnTo)}`;
  window.location.assign(target);
}
