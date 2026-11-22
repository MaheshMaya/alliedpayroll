import type { ModuleView } from "@alliedpayroll/api-client";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { BootstrapProvider } from "@/shared/data/bootstrap-context";
import { sentinelBootstrap } from "@/test/bootstrap-fixture";
import { renderWithProviders } from "@/test/render";

import { ModulesPage } from "./modules-page";

const listModules = vi.hoisted(() => vi.fn());
const setModule = vi.hoisted(() => vi.fn());

vi.mock("@alliedpayroll/api-client", () => ({ listModules, setModule }));

const modules: ModuleView[] = [
  { code: "CUSTOMERS", flagKey: "module:CUSTOMERS", enabled: true },
  { code: "INVENTORY", flagKey: "module:INVENTORY", enabled: false },
];

function renderPage() {
  const client = new QueryClient({
    defaultOptions: { queries: { retry: false }, mutations: { retry: false } },
  });
  renderWithProviders(
    <QueryClientProvider client={client}>
      <BootstrapProvider value={sentinelBootstrap()}>
        <ModulesPage />
      </BootstrapProvider>
    </QueryClientProvider>,
  );
}

describe("ModulesPage", () => {
  beforeEach(() => {
    listModules.mockReset().mockResolvedValue({ data: modules });
    setModule.mockReset();
  });

  it("names modules in the tenant's words and says whether each is on", async () => {
    renderPage();

    const customers = await screen.findByRole("switch", {
      name: "Clients & Sites",
    });
    expect(customers).toBeChecked();
    expect(screen.getByRole("switch", { name: "Inventory" })).not.toBeChecked();
  });

  it("switches a module on and shows the new state", async () => {
    const user = userEvent.setup();
    setModule.mockResolvedValue({
      data: { code: "INVENTORY", flagKey: "module:INVENTORY", enabled: true },
    });
    renderPage();

    await user.click(await screen.findByRole("switch", { name: "Inventory" }));

    expect(setModule).toHaveBeenCalledWith({
      path: { code: "INVENTORY" },
      body: { enabled: true },
    });
    expect(
      await screen.findByRole("switch", { name: "Inventory", checked: true }),
    ).toBeVisible();
  });

  it("explains a refusal next to the module", async () => {
    const user = userEvent.setup();
    setModule.mockResolvedValue({
      error: { code: "authz.denied" },
      response: new Response(null, { status: 403 }),
    });
    renderPage();

    await user.click(await screen.findByRole("switch", { name: "Inventory" }));

    expect(await screen.findByRole("alert")).toHaveTextContent(
      "You do not have permission to do this.",
    );
  });
});
