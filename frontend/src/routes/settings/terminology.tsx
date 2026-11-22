import { createFileRoute } from "@tanstack/react-router";
import { z } from "zod";

import { RequirePermission } from "@/app/require-permission";
import { terminologyLocales } from "@/features/settings/terminology/locales";
import { TerminologyPage } from "@/features/settings/terminology/terminology-page";

// The language being edited lives in the URL so a link opens the same view.
const terminologySearch = z.object({
  locale: z.enum(terminologyLocales).optional(),
});

export const Route = createFileRoute("/settings/terminology")({
  validateSearch: terminologySearch,
  component: TerminologyRoute,
});

function TerminologyRoute() {
  return (
    <RequirePermission permission="tenant:settings_manage">
      <TerminologyPage />
    </RequirePermission>
  );
}
