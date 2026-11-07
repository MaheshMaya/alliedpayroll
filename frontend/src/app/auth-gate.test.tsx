import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { screen } from "@testing-library/react";
import { afterEach, describe, expect, it, vi } from "vitest";

import { sentinelBootstrap } from "@/test/bootstrap-fixture";
import { renderWithProviders } from "@/test/render";

import { AuthGate } from "./auth-gate";

function jsonResponse(status: number, body: unknown = null): Response {
  return new Response(body === null ? null : JSON.stringify(body), {
    status,
    headers: { "content-type": "application/json" },
  });
}

function renderGate() {
  const queryClient = new QueryClient({
    defaultOptions: { queries: { retry: false } },
  });
  return renderWithProviders(
    <QueryClientProvider client={queryClient}>
      <AuthGate>
        <p>Signed-in content</p>
      </AuthGate>
    </QueryClientProvider>,
  );
}

describe("AuthGate", () => {
  afterEach(() => {
    vi.restoreAllMocks();
  });

  it("sends the browser to the backend login when there is no session", async () => {
    vi.spyOn(globalThis, "fetch").mockResolvedValue(jsonResponse(401));
    const assign = vi.fn();
    vi.spyOn(window, "location", "get").mockReturnValue({
      assign,
      pathname: "/payroll/runs",
      search: "?period=2026-11",
    } as unknown as Location);

    renderGate();

    await vi.waitFor(() => {
      expect(assign).toHaveBeenCalledWith(
        "/auth/login?return_to=%2Fpayroll%2Fruns%3Fperiod%3D2026-11",
      );
    });
    expect(screen.queryByText("Signed-in content")).not.toBeInTheDocument();
  });

  it("renders the app once the session and bootstrap are confirmed", async () => {
    vi.spyOn(globalThis, "fetch").mockImplementation((input) => {
      const url =
        typeof input === "string"
          ? input
          : input instanceof URL
            ? input.href
            : input.url;
      if (url.endsWith("/auth/session")) {
        return Promise.resolve(jsonResponse(200, { subject: "user-1" }));
      }
      return Promise.resolve(jsonResponse(200, sentinelBootstrap()));
    });

    renderGate();

    expect(await screen.findByText("Signed-in content")).toBeVisible();
  });

  it("offers a retry when the session check fails for another reason", async () => {
    vi.spyOn(globalThis, "fetch").mockResolvedValue(jsonResponse(503));

    renderGate();

    expect(
      await screen.findByRole("button", { name: "Try again" }),
    ).toBeVisible();
  });
});
