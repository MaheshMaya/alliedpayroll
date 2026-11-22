import { createFileRoute, Outlet } from "@tanstack/react-router";

import { SettingsShell } from "@/features/settings/settings-shell";

export const Route = createFileRoute("/settings")({
  component: SettingsLayout,
});

function SettingsLayout() {
  return (
    <SettingsShell>
      <Outlet />
    </SettingsShell>
  );
}
