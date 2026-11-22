import { expect, test } from "@playwright/test";

import { signIn } from "./sign-in";

// Needs the compose stack with the seeded tenants (docker compose up). Runs on Nimbus so the
// Sentinel user counts the other specs assert stay as seeded; each run invites a new address.
const nimbusHost =
  process.env.E2E_NIMBUS_URL ?? "http://nimbus-it.localhost:5173";

test.describe("Settings, invite and disable a user", () => {
  test("an invited person signs in, becomes active, and is shut out once disabled", async ({
    browser,
  }) => {
    const email = `e2e-${String(Date.now())}@nimbus-it.example`;
    const admin = await browser.newPage();
    await signIn(admin, "admin.nimbus", nimbusHost);
    await admin.goto(`${nimbusHost}/settings/users`);

    await admin.getByRole("button", { name: "Invite person" }).click();
    const dialog = admin.getByRole("dialog", { name: "Invite a person" });
    await dialog.getByLabel("Full name").fill("Tara Joseph");
    await dialog.getByLabel("Work email").fill(email);
    await dialog.getByRole("checkbox", { name: /Employee/ }).check();
    await dialog.getByRole("button", { name: "Send invitation" }).click();

    const sheet = admin.getByRole("dialog", { name: "Tara Joseph" });
    await expect(sheet.getByText("Invited")).toBeVisible();

    // The invited person signs in with the development password the local profile sets.
    const invited = await browser.newPage();
    await signIn(invited, email, nimbusHost);

    await admin.reload();
    await expect(
      admin.getByRole("dialog", { name: "Tara Joseph" }).getByText("Active"),
    ).toBeVisible();

    await admin.getByRole("button", { name: "Disable access" }).click();
    const confirm = admin.getByRole("dialog", { name: "Disable Tara Joseph?" });
    await confirm.getByRole("button", { name: "Disable access" }).click();
    await expect(
      admin.getByRole("dialog", { name: "Tara Joseph" }).getByText("Disabled"),
    ).toBeVisible();

    const refused = await invited.request.get(`${nimbusHost}/api/v1/bootstrap`);
    expect(refused.status()).toBe(403);

    await admin.goto(`${nimbusHost}/settings/audit`);
    await expect(admin.getByText("Invited").first()).toBeVisible();
    await expect(
      admin.getByText("Access disabled; signed out everywhere").first(),
    ).toBeVisible();
  });

  test("HR sees the users but cannot invite", async ({ page }) => {
    await signIn(page, "hr.sentinel");
    await page.getByRole("link", { name: "Settings" }).click();

    await expect(
      page.getByRole("heading", { level: 2, name: "Users" }),
    ).toBeVisible();
    await expect(
      page.getByRole("button", { name: "Invite person" }),
    ).toHaveCount(0);
  });
});
