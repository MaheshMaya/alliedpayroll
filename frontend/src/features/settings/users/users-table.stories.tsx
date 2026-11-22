import type { UserSummary } from "@alliedpayroll/api-client";
import type { Meta, StoryObj } from "@storybook/react-vite";
import {
  createMemoryHistory,
  createRootRoute,
  createRoute,
  createRouter,
  RouterProvider,
} from "@tanstack/react-router";

import { UsersTable } from "./users-table";

const users: UserSummary[] = [
  {
    id: "0193b2c4-2f4e-7d1a-9f3e-6c1a2b3c4d01",
    username: "admin.sentinel",
    displayName: "Asha Varma",
    emailMasked: "a***@sentinel.example",
    userType: "ADMIN",
    status: "ACTIVE",
    scopeMode: "ALL",
    roles: [{ id: "r1", code: "TENANT_ADMIN", name: "Administrator" }],
    lastLoginAt: "2026-11-22T04:00:00Z",
  },
  {
    id: "0193b2c4-2f4e-7d1a-9f3e-6c1a2b3c4d02",
    username: "hr.sentinel",
    displayName: "Meera Nair",
    emailMasked: "h***@sentinel.example",
    userType: "MANAGER",
    status: "INVITED",
    scopeMode: "RESTRICTED",
    roles: [
      { id: "r2", code: "HR", name: "HR" },
      { id: "r3", code: "SUPERVISOR", name: "Supervisor" },
    ],
  },
  {
    id: "0193b2c4-2f4e-7d1a-9f3e-6c1a2b3c4d03",
    username: "guard.sentinel",
    displayName: "Suresh Kumar",
    userType: "EMPLOYEE",
    status: "DISABLED",
    scopeMode: "RESTRICTED",
    roles: [],
  },
  {
    id: "0193b2c4-2f4e-7d1a-9f3e-6c1a2b3c4d04",
    username: "contractor.sentinel",
    displayName: "Venkatasubramanian Ramachandran Iyer",
    emailMasked: "v***@a-very-long-client-domain.example",
    userType: "CLIENT_CONTACT",
    status: "LOCKED",
    scopeMode: "RESTRICTED",
    roles: [{ id: "r4", code: "CLIENT_CONTACT", name: "Client contact" }],
  },
];

// The name cell is a router link, so the story runs inside a memory router with the users route.
function withRouter(table: React.ReactNode) {
  const rootRoute = createRootRoute({ component: () => table });
  const usersRoute = createRoute({
    getParentRoute: () => rootRoute,
    path: "/settings/users",
  });
  const router = createRouter({
    routeTree: rootRoute.addChildren([usersRoute]),
    history: createMemoryHistory({ initialEntries: ["/settings/users"] }),
  });
  return <RouterProvider router={router} />;
}

const meta = {
  title: "Settings/Users table",
  component: UsersTable,
  args: { users },
  render: (args) => withRouter(<UsersTable {...args} />),
} satisfies Meta<typeof UsersTable>;

export default meta;
type Story = StoryObj<typeof meta>;

export const EveryStatus: Story = {};

export const SingleUser: Story = { args: { users: users.slice(0, 1) } };

export const NoRolesYet: Story = {
  args: { users: users.map((user) => ({ ...user, roles: [] })) },
};
