import { screen } from "@testing-library/react";
import { describe, expect, it } from "vitest";

import { BootstrapProvider } from "@/shared/data/bootstrap-context";
import { sentinelBootstrap } from "@/test/bootstrap-fixture";
import { renderWithProviders } from "@/test/render";

import { HomePage } from "./home-page";

describe("HomePage", () => {
  it("shows the tenant and the signed-in user from the bootstrap", () => {
    renderWithProviders(
      <BootstrapProvider value={sentinelBootstrap()}>
        <HomePage />
      </BootstrapProvider>,
    );

    expect(
      screen.getByRole("heading", { level: 1, name: "Home" }),
    ).toBeVisible();
    expect(
      screen.getByText(
        "Sentinel Facility Services · Staffing and facility management",
      ),
    ).toBeVisible();
    expect(screen.getByText("Asha Varma")).toBeVisible();
    expect(screen.getByText("admin@sentinel.example")).toBeVisible();
  });

  it("falls back to the subject when the user has no display name", () => {
    const bootstrap = sentinelBootstrap();
    renderWithProviders(
      <BootstrapProvider
        value={{ ...bootstrap, user: { subject: "svc-account" } }}
      >
        <HomePage />
      </BootstrapProvider>,
    );

    expect(screen.getByText("svc-account")).toBeVisible();
  });
});
