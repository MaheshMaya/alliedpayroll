import { expect, test } from "@playwright/test";

import { signIn, tenantHost } from "./sign-in";

// Needs the compose stack with the seeded tenants (docker compose up).
test.describe("Settings, Users", () => {
  test("the administrator sees the people of the tenant with their roles", async ({
    page,
  }) => {
    await signIn(page, "admin.sentinel");

    await page.getByRole("link", { name: "Settings" }).click();
    await expect(page).toHaveURL(`${tenantHost}/settings/users`);
    await expect(
      page.getByRole("heading", { level: 2, name: "Users" }),
    ).toBeVisible();

    const table = page.getByRole("table");
    await expect(
      table.getByRole("link", { name: "Open Asha Varma" }),
    ).toBeVisible();
    await expect(
      table.getByRole("link", { name: "Open Suresh Kumar" }),
    ).toBeVisible();
    await expect(table.getByText("Nimbus", { exact: false })).toHaveCount(0);
    await expect(page.getByText("4 users")).toBeVisible();

    await table.getByRole("link", { name: "Open Asha Varma" }).click();
    const sheet = page.getByRole("dialog", { name: "Asha Varma" });
    await expect(sheet.getByText("See users and their roles")).toBeVisible();
    await expect(sheet.getByText("Administrator")).toBeVisible();
    await page.keyboard.press("Escape");
    await expect(sheet).toHaveCount(0);
  });

  test("an employee has no Settings entry, no page and no API access", async ({
    page,
  }) => {
    await signIn(page, "guard.sentinel");

    await expect(page.getByRole("link", { name: "Settings" })).toHaveCount(0);

    await page.goto(`${tenantHost}/settings/users`);
    await expect(
      page.getByText("You do not have access to this page"),
    ).toBeVisible();

    const response = await page.request.get(`${tenantHost}/api/v1/users`);
    expect(response.status()).toBe(403);
    expect(await response.json()).toMatchObject({ code: "authz.denied" });
  });
});
