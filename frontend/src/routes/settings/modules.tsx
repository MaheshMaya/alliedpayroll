import { createFileRoute } from "@tanstack/react-router";

import { RequirePermission } from "@/app/require-permission";
import { ModulesPage } from "@/features/settings/modules/modules-page";

export const Route = createFileRoute("/settings/modules")({
  component: ModulesRoute,
});

function ModulesRoute() {
  return (
    <RequirePermission permission="tenant:settings_manage">
      <ModulesPage />
    </RequirePermission>
  );
}
