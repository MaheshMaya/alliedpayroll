import { ApiError, problemCode } from "./session";

/** Builds the error for a failed SDK call from its `error` body and `response`. */
export function problemFrom(
  error: unknown,
  response: Response | undefined,
  url: string,
): ApiError {
  return new ApiError(response?.status ?? 0, url, problemCode(error));
}

/**
 * The message key for a failed mutation: `problem.<code>` when the API explained the refusal, a
 * generic sentence otherwise.
 */
export function problemMessageId(error: unknown): string {
  if (error instanceof ApiError) {
    if (error.code !== undefined) return `problem.${error.code}`;
    if (error.status === 403) return "problem.authz.denied";
  }
  return "problem.unknown";
}
