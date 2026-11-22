import { expect, test } from "@playwright/test";

import { signIn, tenantHost } from "./sign-in";

// Needs the compose stack with the seeded tenants (docker compose up).
test.describe("Settings, Audit log", () => {
  test("the administrator finds their own sign-in at the top of the trail", async ({
    page,
  }) => {
    await signIn(page, "admin.sentinel");

    await page.goto(`${tenantHost}/settings/audit`);
    await expect(
      page.getByRole("heading", { level: 2, name: "Audit log" }),
    ).toBeVisible();

    const rows = page.getByRole("table").getByRole("row");
    await expect(rows.nth(1)).toContainText("Asha Varma");
    await expect(rows.nth(1)).toContainText("Signed in");

    await page.getByLabel("Event").selectOption("LOGIN");
    await expect(page).toHaveURL(/action=LOGIN/);
    await expect(
      page.getByRole("table").getByText("Access denied"),
    ).toHaveCount(0);
  });

  test("an employee cannot read the trail", async ({ page }) => {
    await signIn(page, "guard.sentinel");
    await page.goto(`${tenantHost}/settings/audit`);
    await expect(
      page.getByText("You do not have access to this page"),
    ).toBeVisible();

    const response = await page.request.get(
      `${tenantHost}/api/v1/audit-events`,
    );
    expect(response.status()).toBe(403);
  });
});
