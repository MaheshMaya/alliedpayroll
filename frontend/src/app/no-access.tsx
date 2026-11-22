import { Link } from "@tanstack/react-router";
import { FormattedMessage } from "react-intl";

import { useBootstrap } from "@/shared/data/bootstrap-context";
import { Button } from "@/shared/ui/button";

/** The page exists but the API grants this member no part of it; say who can change that. */
export function NoAccess() {
  const { tenant } = useBootstrap();
  return (
    <section
      role="status"
      className="mx-auto flex max-w-prose flex-col items-start gap-4 rounded-lg border border-border-default bg-bg-surface px-6 py-8"
    >
      <h2 className="text-h2 text-text-default">
        <FormattedMessage id="access.denied.title" />
      </h2>
      <p className="text-body text-text-secondary">
        <FormattedMessage
          id="access.denied.body"
          values={{ tenant: tenant.name }}
        />
      </p>
      <Button asChild variant="secondary">
        <Link to="/">
          <FormattedMessage id="access.denied.home" />
        </Link>
      </Button>
    </section>
  );
}
