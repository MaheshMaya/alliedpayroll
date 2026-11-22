import type {
  AuditEventView,
  PageResponseAuditEventView,
} from "@alliedpayroll/api-client";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import {
  createMemoryHistory,
  createRootRoute,
  createRoute,
  createRouter,
  RouterProvider,
} from "@tanstack/react-router";
import { screen, within } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { z } from "zod";

import { BootstrapProvider } from "@/shared/data/bootstrap-context";
import { sentinelBootstrap } from "@/test/bootstrap-fixture";
import { renderWithProviders } from "@/test/render";

import { AuditLogPage } from "./audit-log-page";
import { auditActions } from "./audit-query";

const listAuditEvents = vi.hoisted(() => vi.fn());

vi.mock("@alliedpayroll/api-client", () => ({ listAuditEvents }));

const login: AuditEventView = {
  id: "0193b2c4-2f4e-7d1a-9f3e-6c1a2b3c4e01",
  occurredAt: "2026-11-22T04:07:24Z",
  action: "LOGIN",
  actorType: "USER",
  actorId: "0193b2c4-2f4e-7d1a-9f3e-6c1a2b3c4d01",
  actorDisplayName: "Asha Varma",
  channel: "WEB",
  clientIp: "172.18.0.1",
  changedFields: [],
  reasonParams: {},
};

const denied: AuditEventView = {
  id: "0193b2c4-2f4e-7d1a-9f3e-6c1a2b3c4e02",
  occurredAt: "2026-11-22T04:09:31Z",
  action: "AUTHZ_DENIED",
  actorType: "USER",
  actorId: "0193b2c4-2f4e-7d1a-9f3e-6c1a2b3c4d02",
  actorDisplayName: "Suresh Kumar",
  channel: "WEB",
  changedFields: [],
  reasonKey: "authz.denied",
  reasonParams: { path: "/api/v1/users" },
};

const activated: AuditEventView = {
  id: "0193b2c4-2f4e-7d1a-9f3e-6c1a2b3c4e03",
  occurredAt: "2026-11-22T04:09:25Z",
  action: "STATE_CHANGE",
  entityType: "USER",
  entityId: "0193b2c4-2f4e-7d1a-9f3e-6c1a2b3c4d02",
  entityLabel: "guard.sentinel",
  actorType: "USER",
  actorId: "0193b2c4-2f4e-7d1a-9f3e-6c1a2b3c4d02",
  actorDisplayName: "Suresh Kumar",
  channel: "WEB",
  changedFields: ["status"],
  oldValues: { status: "INVITED" },
  newValues: { status: "ACTIVE" },
  reasonKey: "identity.user_activated",
  reasonParams: {},
};

function page(
  items: AuditEventView[],
  nextCursor?: string,
): PageResponseAuditEventView {
  return nextCursor === undefined
    ? { items, totalCount: items.length }
    : { items, totalCount: items.length, nextCursor };
}

function renderPage(initialSearch = "") {
  const rootRoute = createRootRoute({
    component: () => (
      <BootstrapProvider
        value={sentinelBootstrap({
          permissions: ["audit:read", "audit:security_read"],
        })}
      >
        <AuditLogPage />
      </BootstrapProvider>
    ),
  });
  const auditRoute = createRoute({
    getParentRoute: () => rootRoute,
    path: "/settings/audit",
    validateSearch: z.object({
      action: z.enum(auditActions).optional(),
      from: z.string().optional(),
      to: z.string().optional(),
      event: z.string().optional(),
    }),
  });
  const router = createRouter({
    routeTree: rootRoute.addChildren([auditRoute]),
    history: createMemoryHistory({
      initialEntries: [`/settings/audit${initialSearch}`],
    }),
  });
  const client = new QueryClient({
    defaultOptions: { queries: { retry: false } },
  });
  renderWithProviders(
    <QueryClientProvider client={client}>
      <RouterProvider router={router} />
    </QueryClientProvider>,
  );
  return router;
}

describe("AuditLogPage", () => {
  beforeEach(() => {
    listAuditEvents.mockReset();
  });

  it("lists events newest first with who, what and why", async () => {
    listAuditEvents.mockResolvedValue({
      data: page([denied, login]),
      response: { status: 200 },
    });
    renderPage();

    const table = await screen.findByRole("table");
    const rows = within(table).getAllByRole("row");
    expect(rows[1]).toHaveTextContent("Suresh Kumar");
    expect(rows[1]).toHaveTextContent("Access denied");
    expect(rows[1]).toHaveTextContent("No permission for this action");
    expect(rows[2]).toHaveTextContent("Asha Varma");
    expect(rows[2]).toHaveTextContent("Signed in");
    expect(listAuditEvents).toHaveBeenCalledWith(
      expect.objectContaining({ query: { size: 50 } }),
    );
  });

  it("filters by event type through the URL", async () => {
    listAuditEvents.mockResolvedValue({
      data: page([login]),
      response: { status: 200 },
    });
    const router = renderPage();
    const user = userEvent.setup();

    await screen.findByRole("table");
    await user.selectOptions(screen.getByLabelText("Event"), "LOGIN");

    expect(router.state.location.search).toMatchObject({ action: "LOGIN" });
    expect(listAuditEvents).toHaveBeenLastCalledWith(
      expect.objectContaining({
        query: { "filter[action]": "LOGIN", size: 50 },
      }),
    );
  });

  it("loads older events with the cursor", async () => {
    listAuditEvents
      .mockResolvedValueOnce({
        data: page([denied], "c1"),
        response: { status: 200 },
      })
      .mockResolvedValueOnce({
        data: page([login]),
        response: { status: 200 },
      });
    renderPage();
    const user = userEvent.setup();

    await user.click(
      await screen.findByRole("button", { name: "Show older events" }),
    );

    expect(await screen.findByText("Asha Varma")).toBeVisible();
    expect(listAuditEvents).toHaveBeenLastCalledWith(
      expect.objectContaining({ query: { cursor: "c1", size: 50 } }),
    );
    expect(
      screen.queryByRole("button", { name: "Show older events" }),
    ).not.toBeInTheDocument();
  });

  it("opens an event from the URL with its before and after", async () => {
    listAuditEvents.mockResolvedValue({
      data: page([activated]),
      response: { status: 200 },
    });
    renderPage(`?event=${activated.id}`);

    const dialog = await screen.findByRole("dialog", {
      name: "Status changed",
    });
    expect(
      within(dialog).getByText(
        "Signed in for the first time; account activated",
      ),
    ).toBeVisible();
    expect(within(dialog).getByText("INVITED")).toBeVisible();
    expect(within(dialog).getByText("ACTIVE")).toBeVisible();
    expect(within(dialog).getByText("USER · guard.sentinel")).toBeVisible();
  });

  it("shows the filtered empty state and the error state", async () => {
    listAuditEvents.mockResolvedValueOnce({
      data: page([]),
      response: { status: 200 },
    });
    renderPage("?action=EXPORT");
    expect(await screen.findByText("No events match")).toBeVisible();
  });
});
