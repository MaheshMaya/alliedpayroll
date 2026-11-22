import type { TerminologyView } from "@alliedpayroll/api-client";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { renderWithProviders } from "@/test/render";

import { TerminologyForm } from "./terminology-page";

const relabelTerminology = vi.hoisted(() => vi.fn());

vi.mock("@alliedpayroll/api-client", () => ({ relabelTerminology }));

const keys = [
  "client",
  "site",
  "site_sub_unit",
  "work_location",
  "engagement",
  "duty",
  "org_unit",
];

function view(labels: Record<string, string> = {}): TerminologyView {
  const defaults: Record<string, string> = {
    site: "Site",
    "site.plural": "Sites",
    site_sub_unit: "Post",
    "site_sub_unit.plural": "Posts",
  };
  return {
    locale: "en-IN",
    terms: keys.flatMap((concept) =>
      [concept, `${concept}.plural`].map((key) => {
        const defaultLabel = defaults[key] ?? key;
        return {
          key,
          defaultLabel,
          label: labels[key] ?? defaultLabel,
          overridden: key in labels,
        };
      }),
    ),
  };
}

function renderForm(current: TerminologyView) {
  const client = new QueryClient({
    defaultOptions: { mutations: { retry: false } },
  });
  renderWithProviders(
    <QueryClientProvider client={client}>
      <TerminologyForm view={current} />
    </QueryClientProvider>,
  );
}

describe("TerminologyForm", () => {
  beforeEach(() => {
    relabelTerminology.mockReset().mockResolvedValue({ data: view() });
  });

  it("saves only the labels that changed", async () => {
    const user = userEvent.setup();
    renderForm(view());
    const save = screen.getByRole("button", { name: "Save labels" });
    expect(save).toBeDisabled();

    const singular = screen.getByRole("textbox", { name: "Client site, one" });
    await user.clear(singular);
    await user.type(singular, "Location");
    await user.click(save);

    expect(relabelTerminology).toHaveBeenCalledWith({
      path: { locale: "en-IN" },
      body: { labels: { site: "Location" } },
    });
  });

  it("puts a relabelled term back to its default", async () => {
    const user = userEvent.setup();
    renderForm(
      view({ site_sub_unit: "Gate", "site_sub_unit.plural": "Gates" }),
    );

    expect(screen.getByText("Default: Post · Posts")).toBeVisible();
    await user.click(screen.getByRole("button", { name: "Use default" }));
    await user.click(screen.getByRole("button", { name: "Save labels" }));

    expect(relabelTerminology).toHaveBeenCalledWith({
      path: { locale: "en-IN" },
      body: {
        labels: { site_sub_unit: "Post", "site_sub_unit.plural": "Posts" },
      },
    });
  });
});
