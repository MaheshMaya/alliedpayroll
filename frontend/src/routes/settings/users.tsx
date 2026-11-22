import { createFileRoute } from "@tanstack/react-router";
import { z } from "zod";

import { RequirePermission } from "@/app/require-permission";
import { UsersPage } from "@/features/settings/users/users-page";
import { userStatuses } from "@/features/settings/users/users-query";

// Filters, page and the open record live in the URL so a link reproduces the view.
const usersSearch = z.object({
  q: z.string().optional(),
  status: z.enum(userStatuses).optional(),
  page: z.number().int().min(1).optional(),
  user: z.uuid().optional(),
});

export const Route = createFileRoute("/settings/users")({
  validateSearch: usersSearch,
  component: UsersRoute,
});

function UsersRoute() {
  return (
    <RequirePermission permission="identity:users_read">
      <UsersPage />
    </RequirePermission>
  );
}
