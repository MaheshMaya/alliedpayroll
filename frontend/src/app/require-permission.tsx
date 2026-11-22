import type { ReactNode } from "react";

import { useHasPermission } from "@/shared/data/bootstrap-context";

import { NoAccess } from "./no-access";

/** Route-level guard: renders the no-access page instead of a screen the API would refuse anyway. */
export function RequirePermission({
  permission,
  children,
}: {
  permission: string;
  children: ReactNode;
}) {
  return useHasPermission(permission) ? children : <NoAccess />;
}
