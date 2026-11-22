import { Link } from "@tanstack/react-router";
import type { ReactNode } from "react";
import { FormattedMessage, useIntl } from "react-intl";

import { useBootstrap } from "@/shared/data/bootstrap-context";
import { cn } from "@/shared/ui/cn";

const sectionClass =
  "flex h-9 items-center rounded-md px-3 text-body text-text-secondary hover:bg-bg-subtle hover:text-text-default focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-border-focus";

// Sections appear only for members the API granted their permission; the API still decides.
const sections = [
  {
    to: "/settings/users",
    labelId: "settings.nav.users",
    permission: "identity:users_read",
  },
  {
    to: "/settings/roles",
    labelId: "settings.nav.roles",
    permission: "identity:users_read",
  },
  {
    to: "/settings/modules",
    labelId: "settings.nav.modules",
    permission: "tenant:settings_manage",
  },
  {
    to: "/settings/terminology",
    labelId: "settings.nav.terminology",
    permission: "tenant:settings_manage",
  },
  {
    to: "/settings/audit",
    labelId: "settings.nav.audit",
    permission: "audit:read",
  },
] as const;

/** Settings: a section list on the left, the section on the right. */
export function SettingsShell({ children }: { children: ReactNode }) {
  const intl = useIntl();
  const { permissions } = useBootstrap();
  const visible = sections.filter((section) =>
    permissions.includes(section.permission),
  );
  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-h1 text-text-default">
        <FormattedMessage id="settings.title" />
      </h1>
      <div className="flex flex-col gap-6 md:flex-row md:gap-10">
        <nav
          aria-label={intl.formatMessage({ id: "settings.nav.label" })}
          className="flex shrink-0 flex-row gap-1 md:w-48 md:flex-col"
        >
          {visible.map((section) => (
            <Link
              key={section.to}
              to={section.to}
              activeProps={{
                "aria-current": "page",
                className: cn(
                  sectionClass,
                  "bg-bg-selected text-bodyStrong text-text-default",
                ),
              }}
              inactiveProps={{ className: sectionClass }}
            >
              <FormattedMessage id={section.labelId} />
            </Link>
          ))}
        </nav>
        <div className="min-w-0 flex-1">{children}</div>
      </div>
    </div>
  );
}
