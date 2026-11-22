import type { RoleView, UserDetail } from "@alliedpayroll/api-client";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { renderWithProviders } from "@/test/render";

import { InviteUserDialog } from "./invite-user-dialog";

const listRoles = vi.hoisted(() => vi.fn());
const inviteUser = vi.hoisted(() => vi.fn());

vi.mock("@alliedpayroll/api-client", () => ({ listRoles, inviteUser }));

const roles: RoleView[] = [
  {
    id: "0193b2c4-0000-7000-8000-000000000001",
    code: "HR",
    name: "HR",
    system: true,
    permissions: ["identity:users_read"],
  },
  {
    id: "0193b2c4-0000-7000-8000-000000000002",
    code: "EMPLOYEE",
    name: "Employee",
    system: true,
    permissions: [],
  },
];

const invited: UserDetail = {
  id: "0193b2c4-0000-7000-8000-0000000000aa",
  username: "nitya@sentinel.example",
  displayName: "Nitya Rao",
  emailMasked: "n***@sentinel.example",
  userType: "MANAGER",
  status: "INVITED",
  scopeMode: "RESTRICTED",
  linked: false,
  roles: [{ id: roles[0]?.id ?? "", code: "HR", name: "HR" }],
  permissions: ["identity:users_read"],
  version: 1,
};

function renderDialog() {
  const onInvited = vi.fn();
  const client = new QueryClient({
    defaultOptions: { queries: { retry: false }, mutations: { retry: false } },
  });
  renderWithProviders(
    <QueryClientProvider client={client}>
      <InviteUserDialog open onOpenChange={vi.fn()} onInvited={onInvited} />
    </QueryClientProvider>,
  );
  return { onInvited };
}

async function fillPerson() {
  const user = userEvent.setup();
  await user.type(screen.getByLabelText("Full name"), "Nitya Rao");
  await user.type(
    screen.getByLabelText("Work email"),
    "nitya@sentinel.example",
  );
  return user;
}

describe("InviteUserDialog", () => {
  beforeEach(() => {
    listRoles.mockReset().mockResolvedValue({ data: roles });
    inviteUser.mockReset();
  });

  it("asks for a role before sending", async () => {
    renderDialog();
    const user = await fillPerson();
    await screen.findByRole("checkbox", { name: /HR/ });

    await user.click(screen.getByRole("button", { name: "Send invitation" }));

    expect(screen.getByRole("alert")).toHaveTextContent(
      "Choose at least one role.",
    );
    expect(inviteUser).not.toHaveBeenCalled();
  });

  it("sends the person with the chosen roles and opens them", async () => {
    inviteUser.mockResolvedValue({ data: invited });
    const { onInvited } = renderDialog();
    const user = await fillPerson();
    await user.click(await screen.findByRole("checkbox", { name: /HR/ }));

    await user.click(screen.getByRole("button", { name: "Send invitation" }));

    expect(inviteUser).toHaveBeenCalledWith({
      body: {
        displayName: "Nitya Rao",
        email: "nitya@sentinel.example",
        userType: "MANAGER",
        roleIds: [roles[0]?.id],
      },
    });
    expect(onInvited).toHaveBeenCalledWith(invited.id);
  });

  it("explains a refusal in words, not a code", async () => {
    inviteUser.mockResolvedValue({
      data: undefined,
      error: { code: "identity.username_taken" },
      response: new Response(null, { status: 409 }),
    });
    renderDialog();
    const user = await fillPerson();
    await user.click(await screen.findByRole("checkbox", { name: /HR/ }));

    await user.click(screen.getByRole("button", { name: "Send invitation" }));

    expect(
      await screen.findByText(
        "This email belongs to an account in another organisation. Use a different email.",
      ),
    ).toBeInTheDocument();
  });
});
