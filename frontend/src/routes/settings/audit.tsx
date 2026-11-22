import { createFileRoute } from "@tanstack/react-router";
import { z } from "zod";

import { RequirePermission } from "@/app/require-permission";
import { AuditLogPage } from "@/features/settings/audit/audit-log-page";
import { auditActions } from "@/features/settings/audit/audit-query";

const auditSearch = z.object({
  action: z.enum(auditActions).optional(),
  from: z.iso.date().optional(),
  to: z.iso.date().optional(),
  event: z.uuid().optional(),
});

export const Route = createFileRoute("/settings/audit")({
  validateSearch: auditSearch,
  component: AuditRoute,
});

function AuditRoute() {
  return (
    <RequirePermission permission="audit:read">
      <AuditLogPage />
    </RequirePermission>
  );
}
