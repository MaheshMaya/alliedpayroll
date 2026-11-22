import { Link } from "@tanstack/react-router";
import type { ReactNode } from "react";
import { FormattedMessage, useIntl } from "react-intl";

import { cn } from "@/shared/ui/cn";

const sectionClass =
  "flex h-9 items-center rounded-md px-3 text-body text-text-secondary hover:bg-bg-subtle hover:text-text-default focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-border-focus";

/** Settings: a section list on the left, the section on the right. One section exists so far. */
export function SettingsShell({ children }: { children: ReactNode }) {
  const intl = useIntl();
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
          <Link
            to="/settings/users"
            activeProps={{
              "aria-current": "page",
              className: cn(
                sectionClass,
                "bg-bg-selected text-bodyStrong text-text-default",
              ),
            }}
            inactiveProps={{ className: sectionClass }}
          >
            <FormattedMessage id="settings.nav.users" />
          </Link>
        </nav>
        <div className="min-w-0 flex-1">{children}</div>
      </div>
    </div>
  );
}
