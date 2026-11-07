import { describe, expect, it } from "vitest";

import { cn } from "./cn";

describe("cn", () => {
  it("keeps a token font size and a token colour together", () => {
    expect(cn("text-body", "text-text-muted")).toBe(
      "text-body text-text-muted",
    );
  });

  it("lets the later token font size win", () => {
    expect(cn("text-body", "text-h1")).toBe("text-h1");
  });

  it("lets the later token colour win", () => {
    expect(cn("bg-bg-surface", "bg-bg-subtle")).toBe("bg-bg-subtle");
  });

  it("lets the later token radius and shadow win", () => {
    expect(cn("rounded-sm shadow-sm", "rounded-lg shadow-md")).toBe(
      "rounded-lg shadow-md",
    );
  });
});
