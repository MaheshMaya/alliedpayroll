import type { Bootstrap } from "@alliedpayroll/api-client";

/** The Sentinel demo tenant as the backend returns it; fictional names, as in the seed. */
export function sentinelBootstrap(
  overrides: Partial<Bootstrap> = {},
): Bootstrap {
  return {
    tenant: {
      id: "0193b2c4-2f4e-7d1a-9f3e-6c1a2b3c4d5e",
      slug: "sentinel",
      name: "Sentinel Facility Services",
      industryProfileCode: "STAFFING",
      defaultLocale: "en-IN",
      enabledLocales: ["en-IN"],
      timezone: "Asia/Kolkata",
    },
    user: {
      subject: "user-1",
      displayName: "Asha Varma",
      email: "admin@sentinel.example",
    },
    permissions: [],
    features: {},
    ...overrides,
  };
}
