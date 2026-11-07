import { client } from "@alliedpayroll/api-client/client";

const CSRF_COOKIE = "XSRF-TOKEN";
const CSRF_HEADER = "X-XSRF-TOKEN";
const SAFE_METHODS = new Set(["GET", "HEAD", "OPTIONS"]);

/** The backend's CSRF cookie is readable on purpose so the SPA can echo it as a header. */
export function readCsrfToken(): string | null {
  const match = document.cookie
    .split("; ")
    .find((part) => part.startsWith(`${CSRF_COOKIE}=`));
  return match === undefined
    ? null
    : decodeURIComponent(match.slice(CSRF_COOKIE.length + 1));
}

// Same origin in every environment: the dev server and nginx proxy /api and /auth to the backend,
// which keeps the session cookie first-party and lets the backend read the tenant from the host.
client.setConfig({
  baseUrl: window.location.origin,
  credentials: "same-origin",
});

client.interceptors.request.use((request) => {
  if (!SAFE_METHODS.has(request.method)) {
    const token = readCsrfToken();
    if (token !== null) request.headers.set(CSRF_HEADER, token);
  }
  return request;
});

export { client };
