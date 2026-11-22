import type { ModuleView } from "@alliedpayroll/api-client";
import { useQuery } from "@tanstack/react-query";
import { useId } from "react";
import { FormattedMessage, useIntl } from "react-intl";

import { problemMessageId } from "@/shared/data/problem";
import { useTerms } from "@/shared/i18n/terminology";
import { Button } from "@/shared/ui/button";
import { Skeleton } from "@/shared/ui/skeleton";
import { Switch } from "@/shared/ui/switch";

import { modulesOptions, useSetModule } from "./modules-query";

/**
 * Settings, Modules: the optional modules of the organisation. A switch applies at once; the
 * navigation follows when the bootstrap comes back.
 */
export function ModulesPage() {
  const modules = useQuery(modulesOptions);

  return (
    <div className="flex flex-col gap-6">
      <header className="flex max-w-[640px] flex-col gap-1">
        <h2 className="text-h2 text-text-default">
          <FormattedMessage id="settings.modules.title" />
        </h2>
        <p className="text-small text-text-secondary">
          <FormattedMessage id="settings.modules.subtitle" />
        </p>
      </header>

      {modules.isPending && (
        <div className="flex flex-col gap-2" role="status" aria-busy="true">
          <span className="sr-only">
            <FormattedMessage id="settings.modules.loading" />
          </span>
          <Skeleton className="h-16 w-full" />
          <Skeleton className="h-16 w-full" />
          <Skeleton className="h-16 w-full" />
        </div>
      )}
      {modules.isError && (
        <div
          role="alert"
          className="flex flex-col items-start gap-3 rounded-lg border border-status-danger-bg bg-status-danger-bg px-6 py-5 text-status-danger-fg"
        >
          <p className="text-bodyStrong">
            <FormattedMessage id="settings.modules.error" />
          </p>
          <Button
            variant="secondary"
            size="sm"
            onClick={() => {
              void modules.refetch();
            }}
          >
            <FormattedMessage id="settings.users.error.retry" />
          </Button>
        </div>
      )}
      {modules.isSuccess && (
        <ul className="flex flex-col divide-y divide-border-divider rounded-lg border border-border-default bg-bg-surface">
          {modules.data.map((module) => (
            <ModuleRow key={module.code} module={module} />
          ))}
        </ul>
      )}
    </div>
  );
}

function ModuleRow({ module }: { module: ModuleView }) {
  const intl = useIntl();
  const terms = useTerms();
  const toggle = useSetModule();
  const nameId = useId();
  const descriptionId = useId();
  const name = intl.formatMessage(
    { id: `settings.modules.${module.code}.name` },
    terms,
  );

  return (
    <li className="flex items-start justify-between gap-6 px-5 py-4">
      <div className="flex min-w-0 flex-col gap-0.5">
        <span id={nameId} className="text-bodyStrong text-text-default">
          {name}
        </span>
        <span id={descriptionId} className="text-small text-text-secondary">
          <FormattedMessage
            id={`settings.modules.${module.code}.description`}
          />
        </span>
        {toggle.isError && (
          <p role="alert" className="pt-1 text-small text-status-danger-fg">
            <FormattedMessage id={problemMessageId(toggle.error)} />
          </p>
        )}
      </div>
      <div className="flex shrink-0 items-center gap-3 pt-0.5">
        <span className="text-small text-text-secondary" aria-hidden="true">
          <FormattedMessage
            id={
              module.enabled
                ? "settings.modules.state.on"
                : "settings.modules.state.off"
            }
          />
        </span>
        <Switch
          checked={module.enabled}
          disabled={toggle.isPending}
          aria-labelledby={nameId}
          aria-describedby={descriptionId}
          onCheckedChange={(enabled) => {
            toggle.mutate({ code: module.code, enabled });
          }}
        />
      </div>
    </li>
  );
}
