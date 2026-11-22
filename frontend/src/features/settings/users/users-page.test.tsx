import type {
  PageResponseUserSummary,
  UserDetail,
  UserSummary,
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

import { UsersPage } from "./users-page";
import { userStatuses } from "./users-query";

const listUsers = vi.hoisted(() => vi.fn());
const getUser = vi.hoisted(() => vi.fn());

vi.mock("@alliedpayroll/api-client", () => ({ listUsers, getUser }));

const asha: UserSummary = {
  id: "0193b2c4-2f4e-7d1a-9f3e-6c1a2b3c4d01",
  username: "admin.sentinel",
  displayName: "Asha Varma",
  emailMasked: "a***@sentinel.example",
  userType: "ADMIN",
  status: "ACTIVE",
  scopeMode: "ALL",
  roles: [{ id: "r1", code: "TENANT_ADMIN", name: "Administrator" }],
  lastLoginAt: "2026-11-22T04:00:00Z",
};

const page: PageResponseUserSummary = {
  totalCount: 2,
  items: [
    asha,
    {
      id: "0193b2c4-2f4e-7d1a-9f3e-6c1a2b3c4d02",
      username: "guard.sentinel",
      displayName: "Suresh Kumar",
      userType: "EMPLOYEE",
      status: "INVITED",
      scopeMode: "RESTRICTED",
      roles: [],
    },
  ],
};

const detail: UserDetail = {
  ...asha,
  linked: true,
  permissions: ["identity:users_read"],
};

function renderPage(initialSearch = "") {
  const rootRoute = createRootRoute({
    component: () => (
      <BootstrapProvider
        value={sentinelBootstrap({ permissions: ["identity:users_read"] })}
      >
        <UsersPage />
      </BootstrapProvider>
    ),
  });
  const usersRoute = createRoute({
    getParentRoute: () => rootRoute,
    path: "/settings/users",
    validateSearch: z.object({
      q: z.string().optional(),
      status: z.enum(userStatuses).optional(),
      page: z.number().optional(),
      user: z.string().optional(),
    }),
  });
  const router = createRouter({
    routeTree: rootRoute.addChildren([usersRoute]),
    history: createMemoryHistory({
      initialEntries: [`/settings/users${initialSearch}`],
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

describe("UsersPage", () => {
  beforeEach(() => {
    listUsers.mockReset();
    getUser.mockReset();
  });

  it("lists the tenant's users with roles, status and masked email", async () => {
    listUsers.mockResolvedValue({ data: page, response: { status: 200 } });
    renderPage();

    const table = await screen.findByRole("table");
    expect(
      within(table).getByRole("link", { name: "Open Asha Varma" }),
    ).toBeVisible();
    expect(within(table).getByText("Administrator")).toBeVisible();
    expect(within(table).getByText("a***@sentinel.example")).toBeVisible();
    expect(within(table).getByText("Invited")).toBeVisible();
    expect(within(table).getByText("No roles")).toBeVisible();
    expect(screen.getByText("2 users")).toBeVisible();
    expect(listUsers).toHaveBeenCalledWith(
      expect.objectContaining({ query: { page: 0, size: 25 } }),
    );
  });

  it("puts the status filter in the URL and asks the API for it", async () => {
    listUsers.mockResolvedValue({ data: page, response: { status: 200 } });
    const router = renderPage();
    const user = userEvent.setup();

    await screen.findByRole("table");
    await user.selectOptions(screen.getByLabelText("Status"), "INVITED");

    expect(router.state.location.search).toMatchObject({ status: "INVITED" });
    expect(listUsers).toHaveBeenLastCalledWith(
      expect.objectContaining({
        query: { "filter[status]": "INVITED", page: 0, size: 25 },
      }),
    );
  });

  it("shows the filtered empty state", async () => {
    listUsers.mockResolvedValue({
      data: { items: [], totalCount: 0 },
      response: { status: 200 },
    });
    renderPage("?q=zzz");

    expect(await screen.findByText("No users match")).toBeVisible();
    expect(screen.getByText("No users")).toBeVisible();
  });

  it("explains a failed load and offers a retry", async () => {
    listUsers.mockResolvedValue({ data: undefined, response: { status: 500 } });
    renderPage();

    expect(await screen.findByRole("alert")).toHaveTextContent(
      "Could not load users",
    );
    expect(screen.getByRole("button", { name: "Try again" })).toBeVisible();
  });

  it("opens a user's quick look from the URL with roles and permissions", async () => {
    listUsers.mockResolvedValue({ data: page, response: { status: 200 } });
    getUser.mockResolvedValue({ data: detail, response: { status: 200 } });
    renderPage(`?user=${detail.id}`);

    const dialog = await screen.findByRole("dialog", { name: "Asha Varma" });
    expect(within(dialog).getByText("See users and their roles")).toBeVisible();
    expect(within(dialog).getByText("Linked to their account")).toBeVisible();
    expect(within(dialog).getByText("Whole organisation")).toBeVisible();
    expect(getUser).toHaveBeenCalledWith(
      expect.objectContaining({ path: { id: detail.id } }),
    );
  });
});
