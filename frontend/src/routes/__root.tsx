import { createRootRoute, Outlet } from "@tanstack/react-router";

import { AuthGate } from "@/app/auth-gate";
import { AppShell } from "@/shared/shell/app-shell";

export const Route = createRootRoute({ component: Root });

function Root() {
  return (
    <AuthGate>
      <AppShell>
        <Outlet />
      </AppShell>
    </AuthGate>
  );
}
