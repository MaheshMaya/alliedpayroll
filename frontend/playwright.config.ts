import { defineConfig, devices } from "@playwright/test";

const externalBaseUrl = process.env.E2E_BASE_URL;
const baseURL = externalBaseUrl ?? "http://localhost:5173";
const isCi = process.env.CI !== undefined;

export default defineConfig({
  testDir: "./e2e",
  fullyParallel: true,
  forbidOnly: isCi,
  retries: isCi ? 2 : 0,
  reporter: "line",
  use: { baseURL, trace: "on-first-retry" },
  projects: [
    {
      name: "desktop-1440",
      use: {
        ...devices["Desktop Chrome"],
        viewport: { width: 1440, height: 900 },
      },
    },
    {
      name: "tablet-1024",
      use: {
        ...devices["Desktop Chrome"],
        viewport: { width: 1024, height: 768 },
      },
    },
  ],
  // Without E2E_BASE_URL the suite starts its own dev server; with it, it targets a running deployment.
  ...(externalBaseUrl === undefined && {
    webServer: {
      command: "pnpm dev",
      url: baseURL,
      reuseExistingServer: !isCi,
    },
  }),
});
