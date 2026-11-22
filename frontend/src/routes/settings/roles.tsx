import { createFileRoute } from "@tanstack/react-router";
import { z } from "zod";

import { RequirePermission } from "@/app/require-permission";
import { RolesPage } from "@/features/settings/roles/roles-page";

// The open role lives in the URL so a link reproduces the view.
const rolesSearch = z.object({
  role: z.uuid().optional(),
});

export const Route = createFileRoute("/settings/roles")({
  validateSearch: rolesSearch,
  component: RolesRoute,
});

function RolesRoute() {
  return (
    <RequirePermission permission="identity:users_read">
      <RolesPage />
    </RequirePermission>
  );
}
