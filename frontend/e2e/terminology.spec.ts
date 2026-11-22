import { expect, test } from "@playwright/test";

import { signIn } from "./sign-in";

// Needs the compose stack with the seeded tenants (docker compose up). Runs on Nimbus, a
// corporate tenant that starts without clients and sites, and puts everything back at the end.
const nimbusHost =
  process.env.E2E_NIMBUS_URL ?? "http://nimbus-it.localhost:5173";

test.describe("Settings, modules and terminology", () => {
  test("a module switched on appears in the navigation under the tenant's own words", async ({
    page,
  }) => {
    await signIn(page, "admin.nimbus", nimbusHost);
    const nav = page.getByRole("navigation", { name: "Main navigation" });
    await expect(nav.getByRole("button", { name: /Clients/ })).toHaveCount(0);

    await page.goto(`${nimbusHost}/settings/modules`);
    await page.getByRole("switch", { name: "Clients & Sites" }).click();
    await expect(
      nav.getByRole("button", { name: "Clients & Sites" }),
    ).toBeVisible();

    await page.goto(`${nimbusHost}/settings/terminology`);
    await page
      .getByRole("textbox", { name: "Client site, one" })
      .fill("Campus");
    await page
      .getByRole("textbox", { name: "Client site, many" })
      .fill("Campuses");
    await page.getByRole("button", { name: "Save labels" }).click();
    await expect(page.getByRole("status")).toHaveText("Labels saved");
    await expect(
      nav.getByRole("button", { name: "Clients & Campuses" }),
    ).toBeVisible();

    await page.reload();
    await expect(
      nav.getByRole("button", { name: "Clients & Campuses" }),
    ).toBeVisible();

    await page.getByRole("button", { name: "Use default" }).click();
    await page.getByRole("button", { name: "Save labels" }).click();
    await page.goto(`${nimbusHost}/settings/modules`);
    await page.getByRole("switch", { name: "Clients & Sites" }).click();
    await expect(nav.getByRole("button", { name: /Clients/ })).toHaveCount(0);
  });
});
