import type { Bootstrap } from "@alliedpayroll/api-client";
import {
  createMemoryHistory,
  createRootRoute,
  createRoute,
  createRouter,
  RouterProvider,
} from "@tanstack/react-router";
import { screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { describe, expect, it, vi } from "vitest";

import { BootstrapProvider } from "@/shared/data/bootstrap-context";
import { sentinelBootstrap } from "@/test/bootstrap-fixture";
import { renderWithProviders } from "@/test/render";

import { SideNav } from "./side-nav";

function renderNav(bootstrap: Bootstrap, collapsed = false) {
  const onToggle = vi.fn();
  const rootRoute = createRootRoute({
    component: () => (
      <BootstrapProvider value={bootstrap}>
        <SideNav collapsed={collapsed} onToggle={onToggle} />
      </BootstrapProvider>
    ),
  });
  const indexRoute = createRoute({
    getParentRoute: () => rootRoute,
    path: "/",
  });
  const router = createRouter({
    routeTree: rootRoute.addChildren([indexRoute]),
    history: createMemoryHistory({ initialEntries: ["/"] }),
  });
  renderWithProviders(<RouterProvider router={router} />);
  return { onToggle };
}

describe("SideNav", () => {
  it("marks the current page and hides groups of modules that are off", async () => {
    renderNav(sentinelBootstrap());

    expect(await screen.findByRole("link", { name: "Home" })).toHaveAttribute(
      "aria-current",
      "page",
    );
    expect(
      screen.queryByRole("button", { name: "Clients & sites" }),
    ).not.toBeInTheDocument();
  });

  it("shows a module's group when its flag is on and opens one group at a time", async () => {
    const user = userEvent.setup();
    renderNav(sentinelBootstrap({ features: { "module:CUSTOMERS": true } }));

    const clients = await screen.findByRole("button", {
      name: "Clients & sites",
    });
    const payroll = screen.getByRole("button", { name: "Payroll" });

    await user.click(payroll);
    expect(payroll).toHaveAttribute("aria-expanded", "true");
    expect(screen.getByText("Payroll runs")).toBeVisible();

    await user.click(clients);
    expect(clients).toHaveAttribute("aria-expanded", "true");
    expect(payroll).toHaveAttribute("aria-expanded", "false");
  });

  it("keeps every destination reachable by name when collapsed", async () => {
    const { onToggle } = renderNav(sentinelBootstrap(), true);
    const user = userEvent.setup();

    expect(await screen.findByRole("link", { name: "Home" })).toBeVisible();
    expect(screen.getByRole("button", { name: "Payroll" })).toBeVisible();

    await user.click(screen.getByRole("button", { name: "Expand navigation" }));
    expect(onToggle).toHaveBeenCalledOnce();
  });
});
