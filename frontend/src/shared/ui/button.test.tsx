import { screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { describe, expect, it, vi } from "vitest";

import { renderWithProviders } from "@/test/render";

import { Button } from "./button";

describe("Button", () => {
  it("is a native button with its label as the accessible name", () => {
    renderWithProviders(<Button>Approve run</Button>);

    expect(screen.getByRole("button", { name: "Approve run" })).toBeInstanceOf(
      HTMLButtonElement,
    );
  });

  it("calls onClick when activated with the keyboard", async () => {
    const onClick = vi.fn();
    const user = userEvent.setup();
    renderWithProviders(<Button onClick={onClick}>Approve run</Button>);

    await user.tab();
    await user.keyboard("{Enter}");

    expect(onClick).toHaveBeenCalledOnce();
  });

  it("does not fire when disabled", async () => {
    const onClick = vi.fn();
    const user = userEvent.setup();
    renderWithProviders(
      <Button onClick={onClick} disabled>
        Approve run
      </Button>,
    );

    await user.click(screen.getByRole("button", { name: "Approve run" }));

    expect(screen.getByRole("button")).toBeDisabled();
    expect(onClick).not.toHaveBeenCalled();
  });

  it("renders the child element when asChild is set", () => {
    renderWithProviders(
      <Button asChild>
        <a href="/payroll/runs">Open runs</a>
      </Button>,
    );

    expect(screen.getByRole("link", { name: "Open runs" })).toHaveAttribute(
      "href",
      "/payroll/runs",
    );
    expect(screen.queryByRole("button")).not.toBeInTheDocument();
  });
});
