import type { UserDetail } from "@alliedpayroll/api-client";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { screen, within } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { BootstrapProvider } from "@/shared/data/bootstrap-context";
import { sentinelBootstrap } from "@/test/bootstrap-fixture";
import { renderWithProviders } from "@/test/render";

import { UserAccessActions } from "./user-access-actions";

const disableUser = vi.hoisted(() => vi.fn());
const enableUser = vi.hoisted(() => vi.fn());

vi.mock("@alliedpayroll/api-client", () => ({ disableUser, enableUser }));

const ADMIN_ID = "0193b2c4-0000-7000-8000-000000000010";

const guard: UserDetail = {
  id: "0193b2c4-0000-7000-8000-000000000011",
  username: "guard.sentinel",
  displayName: "Suresh Kumar",
  userType: "EMPLOYEE",
  status: "ACTIVE",
  scopeMode: "RESTRICTED",
  linked: true,
  roles: [],
  permissions: [],
  version: 3,
};

function renderActions(user: UserDetail) {
  const client = new QueryClient({
    defaultOptions: { mutations: { retry: false } },
  });
  renderWithProviders(
    <QueryClientProvider client={client}>
      <BootstrapProvider
        value={sentinelBootstrap({
          user: { id: ADMIN_ID, subject: "kc-admin", displayName: "Asha" },
          permissions: ["identity:users_manage"],
        })}
      >
        <UserAccessActions user={user} />
      </BootstrapProvider>
    </QueryClientProvider>,
  );
}

describe("UserAccessActions", () => {
  beforeEach(() => {
    disableUser.mockReset();
    enableUser.mockReset();
  });

  it("asks before disabling and says what happens", async () => {
    disableUser.mockResolvedValue({ data: { ...guard, status: "DISABLED" } });
    renderActions(guard);
    const user = userEvent.setup();

    await user.click(screen.getByRole("button", { name: "Disable access" }));

    const dialog = screen.getByRole("dialog", {
      name: "Disable Suresh Kumar?",
    });
    expect(dialog).toHaveTextContent("signed out on every device");
    expect(disableUser).not.toHaveBeenCalled();

    await user.click(
      within(dialog).getByRole("button", { name: "Disable access" }),
    );

    expect(disableUser).toHaveBeenCalledWith({ path: { id: guard.id } });
  });

  it("enables a disabled user without a confirmation", async () => {
    enableUser.mockResolvedValue({ data: { ...guard, status: "ACTIVE" } });
    renderActions({ ...guard, status: "DISABLED" });

    await userEvent
      .setup()
      .click(screen.getByRole("button", { name: "Enable access" }));

    expect(enableUser).toHaveBeenCalledWith({ path: { id: guard.id } });
  });

  it("offers nothing on the administrator's own record", () => {
    renderActions({ ...guard, id: ADMIN_ID });

    expect(screen.queryByRole("button")).not.toBeInTheDocument();
  });
});
