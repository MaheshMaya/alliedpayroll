import { expect, test } from "@playwright/test";

test("the app shell loads and shows the product name", async ({ page }) => {
  await page.goto("/");
  await expect(
    page.getByRole("heading", { level: 1, name: "Allied Payroll" }),
  ).toBeVisible();
});
