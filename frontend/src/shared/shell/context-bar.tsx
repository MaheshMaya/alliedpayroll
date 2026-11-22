import { ChevronDown, Search } from "lucide-react";
import { useState } from "react";
import { FormattedMessage, useIntl } from "react-intl";

import { useBootstrap } from "@/shared/data/bootstrap-context";
import { currentPeriodLabel } from "@/shared/i18n/periods";
import { useTerms } from "@/shared/i18n/terminology";

/**
 * Global context bar from Figma (7:112): the four scope selectors and the search trigger. In
 * this slice the selectors show the tenant-wide scope and open nothing yet; each is a real
 * button so keyboard users meet the same control the finished version will have.
 */
export function ContextBar() {
  const intl = useIntl();
  const { tenant, features } = useBootstrap();
  const terms = useTerms();
  // The period chip reads the clock once per mount; a real selector replaces it with URL state.
  const [today] = useState(() => new Date());
  const chips = [
    {
      id: "legalEntity",
      labelId: "shell.context.legalEntity",
      value: tenant.name,
    },
    {
      id: "branch",
      labelId: "shell.context.branch",
      value: intl.formatMessage({ id: "shell.context.allBranches" }),
    },
    {
      id: "period",
      labelId: "shell.context.period",
      value: currentPeriodLabel(intl, today),
    },
    // A tenant without clients and sites has no client scope to pick.
    ...(features["module:CUSTOMERS"] === true
      ? [
          {
            id: "client",
            labelId: "shell.context.client",
            value: intl.formatMessage(
              { id: "shell.context.allClients" },
              terms,
            ),
          },
        ]
      : []),
  ];

  return (
    <div className="flex w-full items-center gap-2 border-b border-border-divider bg-bg-surface px-8 py-2.5">
      {chips.map((chip) => (
        <button
          key={chip.id}
          type="button"
          aria-disabled="true"
          className="flex shrink-0 items-center gap-2 rounded-md border border-border-default py-1.5 pr-2.5 pl-3 text-left whitespace-nowrap"
        >
          <span className="flex flex-col">
            <span className="text-overline tracking-[0.04em] text-text-muted uppercase">
              <FormattedMessage id={chip.labelId} values={terms} />
            </span>
            <span className="text-bodyStrong text-text-default">
              {chip.value}
            </span>
          </span>
          <ChevronDown
            aria-hidden="true"
            className="size-3.5 text-text-muted"
          />
        </button>
      ))}
      <div className="flex-1" />
      <button
        type="button"
        aria-disabled="true"
        aria-label={intl.formatMessage({ id: "shell.search.placeholder" })}
        className="flex h-10 w-[280px] items-center gap-2 rounded-md border border-border-input bg-bg-surface px-3 text-body text-text-muted"
      >
        <Search aria-hidden="true" className="size-4" />
        <span className="min-w-0 flex-1 truncate text-left">
          <FormattedMessage id="shell.search.placeholder" />
        </span>
        <kbd className="rounded-sm border border-border-default bg-bg-subtle px-1.5 py-0.5 font-mono text-code text-text-secondary">
          <FormattedMessage id="shell.search.shortcut" />
        </kbd>
      </button>
    </div>
  );
}
