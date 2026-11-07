import { expect, test } from "@playwright/test";

// The real login path: the backend redirects to Keycloak, the user signs in as a member of the
// tenant's organisation, and Home shows the tenant. Needs the compose stack (docker compose up).
// E2E_BASE_URL=http://sentinel.localhost:5173 selects the tenant host; defaults below match it.
const tenantHost = process.env.E2E_BASE_URL ?? "http://sentinel.localhost:5173";
const username = process.env.E2E_USERNAME ?? "admin.sentinel";
const password = process.env.E2E_PASSWORD ?? "Welcome@123";

test.describe("sign in", () => {
  test("a tenant user signs in through Keycloak and sees their Home", async ({
    page,
  }) => {
    await page.goto(`${tenantHost}/`);

    await expect(page).toHaveURL(/auth\.localhost:8081\/realms\/alliedpayroll/);
    await page.getByLabel(/username|email/i).fill(username);
    await page.getByLabel(/password/i).fill(password);
    await page.getByRole("button", { name: /sign in/i }).click();

    await expect(page).toHaveURL(`${tenantHost}/`);
    await expect(
      page.getByRole("heading", { level: 1, name: "Home" }),
    ).toBeVisible();
    await expect(
      page.getByText("Sentinel Facility Services", { exact: false }).first(),
    ).toBeVisible();
    await expect(
      page.getByRole("navigation", { name: "Main navigation" }),
    ).toBeVisible();
  });

  test("a user of another tenant is refused on this host", async ({ page }) => {
    await page.goto(`${tenantHost}/`);

    await page.getByLabel(/username|email/i).fill("admin.nimbus");
    await page.getByLabel(/password/i).fill(password);
    await page.getByRole("button", { name: /sign in/i }).click();

    // Keycloak issues a token without this organisation; the API answers 403 and the app says so.
    await expect(
      page.getByText("not a member of this organisation", { exact: false }),
    ).toBeVisible();
    await expect(
      page.getByRole("heading", { level: 1, name: "Home" }),
    ).toHaveCount(0);
  });
});
