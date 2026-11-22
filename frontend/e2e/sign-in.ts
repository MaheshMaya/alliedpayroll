import { expect, type Page } from "@playwright/test";

export const tenantHost =
  process.env.E2E_BASE_URL ?? "http://sentinel.localhost:5173";
export const password = process.env.E2E_PASSWORD ?? "Welcome@123";

/** The real path: the backend redirects to Keycloak, the user signs in, the callback lands on Home. */
export async function signIn(page: Page, username: string): Promise<void> {
  await page.goto(`${tenantHost}/`);
  await expect(page).toHaveURL(/auth\.localhost:8081\/realms\/alliedpayroll/);
  await page.getByLabel(/username|email/i).fill(username);
  await page.getByLabel(/password/i).fill(password);
  await page.getByRole("button", { name: /sign in/i }).click();
  await expect(page).toHaveURL(`${tenantHost}/`);
}
