import type { TermView, TerminologyView } from "@alliedpayroll/api-client";
import { useQuery } from "@tanstack/react-query";
import { useNavigate, useSearch } from "@tanstack/react-router";
import { useState, type SubmitEvent } from "react";
import { FormattedMessage, useIntl } from "react-intl";

import { problemMessageId } from "@/shared/data/problem";
import { Button } from "@/shared/ui/button";
import { Input } from "@/shared/ui/input";
import { NativeSelect } from "@/shared/ui/native-select";
import { Skeleton } from "@/shared/ui/skeleton";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeaderCell,
  TableRow,
} from "@/shared/ui/table";

import { terminologyLocales, type TerminologyLocale } from "./locales";
import { terminologyOptions, useRelabel } from "./terminology-query";

const PLURAL = ".plural";

/**
 * Settings, Terminology: the organisation's words for the canonical concepts, one language at
 * a time. The language is URL state; edits stay local until saved.
 */
export function TerminologyPage() {
  const intl = useIntl();
  const search = useSearch({ from: "/settings/terminology" });
  const navigate = useNavigate({ from: "/settings/terminology" });
  const locale: TerminologyLocale = search.locale ?? "en-IN";
  const terminology = useQuery(terminologyOptions(locale));

  return (
    <div className="flex flex-col gap-6">
      <header className="flex flex-col gap-4 sm:flex-row sm:items-end sm:justify-between">
        <div className="flex max-w-[640px] flex-col gap-1">
          <h2 className="text-h2 text-text-default">
            <FormattedMessage id="settings.terminology.title" />
          </h2>
          <p className="text-small text-text-secondary">
            <FormattedMessage id="settings.terminology.subtitle" />
          </p>
        </div>
        <label className="flex shrink-0 flex-col gap-1">
          <span className="text-small text-text-secondary">
            <FormattedMessage id="settings.terminology.language" />
          </span>
          <NativeSelect
            value={locale}
            onChange={(event) => {
              const next = terminologyLocales.find(
                (code) => code === event.target.value,
              );
              void navigate({ search: { locale: next } });
            }}
          >
            {terminologyLocales.map((code) => (
              <option key={code} value={code} lang={code}>
                {/* Endonyms: each language is named in itself. */}
                {intl.formatMessage({
                  id: `settings.terminology.locale.${code}`,
                })}
              </option>
            ))}
          </NativeSelect>
        </label>
      </header>

      {terminology.isPending && (
        <div className="flex flex-col gap-2" role="status" aria-busy="true">
          <span className="sr-only">
            <FormattedMessage id="settings.terminology.loading" />
          </span>
          <Skeleton className="h-10 w-full" />
          <Skeleton className="h-(--table-rowHeight) w-full" />
          <Skeleton className="h-(--table-rowHeight) w-full" />
        </div>
      )}
      {terminology.isError && (
        <div
          role="alert"
          className="flex flex-col items-start gap-3 rounded-lg border border-status-danger-bg bg-status-danger-bg px-6 py-5 text-status-danger-fg"
        >
          <p className="text-bodyStrong">
            <FormattedMessage id="settings.terminology.error" />
          </p>
          <Button
            variant="secondary"
            size="sm"
            onClick={() => {
              void terminology.refetch();
            }}
          >
            <FormattedMessage id="settings.users.error.retry" />
          </Button>
        </div>
      )}
      {terminology.isSuccess && (
        <TerminologyForm
          key={terminology.data.locale}
          view={terminology.data}
        />
      )}
    </div>
  );
}

export function TerminologyForm({ view }: { view: TerminologyView }) {
  const intl = useIntl();
  const relabel = useRelabel();
  const byKey = new Map(view.terms.map((term) => [term.key, term]));
  const [draft, setDraft] = useState<Record<string, string>>(() =>
    labelsOf(view),
  );
  const [saved, setSaved] = useState(false);

  const changes = Object.fromEntries(
    view.terms
      .filter((term) => effective(term, draft[term.key]) !== term.label)
      .map((term) => [term.key, (draft[term.key] ?? "").trim()]),
  );
  const dirty = Object.keys(changes).length > 0;
  const concepts = view.terms
    .map((term) => term.key)
    .filter((key) => !key.endsWith(PLURAL));

  const set = (key: string, value: string) => {
    setSaved(false);
    setDraft((current) => ({ ...current, [key]: value }));
  };

  const submit = (event: SubmitEvent<HTMLFormElement>) => {
    event.preventDefault();
    relabel.mutate(
      { locale: view.locale, labels: changes },
      {
        onSuccess: (updated) => {
          setDraft(labelsOf(updated));
          setSaved(true);
        },
      },
    );
  };

  return (
    <form onSubmit={submit} className="flex flex-col gap-4">
      <Table>
        <TableHead>
          <TableRow>
            <TableHeaderCell>
              <FormattedMessage id="settings.terminology.column.term" />
            </TableHeaderCell>
            <TableHeaderCell>
              <FormattedMessage id="settings.terminology.column.singular" />
            </TableHeaderCell>
            <TableHeaderCell>
              <FormattedMessage id="settings.terminology.column.plural" />
            </TableHeaderCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {concepts.map((concept) => {
            const singular = byKey.get(concept);
            const plural = byKey.get(concept + PLURAL);
            if (singular === undefined || plural === undefined) return null;
            const name = intl.formatMessage({
              id: `settings.terminology.concept.${concept}.name`,
            });
            const custom =
              effective(singular, draft[singular.key]) !==
                singular.defaultLabel ||
              effective(plural, draft[plural.key]) !== plural.defaultLabel;
            return (
              <TableRow key={concept} className="align-top">
                <TableCell className="py-3">
                  <div className="flex max-w-[280px] flex-col gap-0.5">
                    <span className="text-bodyStrong text-text-default">
                      {name}
                    </span>
                    <span className="text-caption text-text-muted">
                      <FormattedMessage
                        id={`settings.terminology.concept.${concept}.hint`}
                      />
                    </span>
                    {custom && (
                      <span className="flex flex-wrap items-center gap-x-2 pt-1 text-caption text-text-secondary">
                        <FormattedMessage
                          id="settings.terminology.default"
                          values={{
                            singular: singular.defaultLabel,
                            plural: plural.defaultLabel,
                          }}
                        />
                        <button
                          type="button"
                          className="cursor-pointer text-text-link underline-offset-2 hover:underline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-border-focus"
                          onClick={() => {
                            set(singular.key, singular.defaultLabel);
                            set(plural.key, plural.defaultLabel);
                          }}
                        >
                          <FormattedMessage id="settings.terminology.useDefault" />
                        </button>
                      </span>
                    )}
                  </div>
                </TableCell>
                {[singular, plural].map((term) => (
                  <TableCell key={term.key} className="py-3">
                    <Input
                      lang={view.locale}
                      maxLength={80}
                      value={draft[term.key] ?? ""}
                      placeholder={term.defaultLabel}
                      aria-label={intl.formatMessage(
                        {
                          id:
                            term === singular
                              ? "settings.terminology.singularLabel"
                              : "settings.terminology.pluralLabel",
                        },
                        { term: name },
                      )}
                      onChange={(event) => {
                        set(term.key, event.target.value);
                      }}
                    />
                  </TableCell>
                ))}
              </TableRow>
            );
          })}
        </TableBody>
      </Table>

      <div className="sticky bottom-0 flex flex-wrap items-center gap-3 border-t border-border-divider bg-bg-canvas py-3">
        <Button type="submit" disabled={!dirty || relabel.isPending}>
          <FormattedMessage id="settings.terminology.save" />
        </Button>
        <Button
          type="button"
          variant="ghost"
          disabled={!dirty || relabel.isPending}
          onClick={() => {
            relabel.reset();
            setDraft(labelsOf(view));
          }}
        >
          <FormattedMessage id="settings.terminology.discard" />
        </Button>
        <span role="status" className="text-small text-text-secondary">
          {saved && !dirty && (
            <FormattedMessage id="settings.terminology.saved" />
          )}
        </span>
        {relabel.isError && (
          <p role="alert" className="text-small text-status-danger-fg">
            <FormattedMessage id={problemMessageId(relabel.error)} />
          </p>
        )}
      </div>
    </form>
  );
}

function labelsOf(view: TerminologyView): Record<string, string> {
  return Object.fromEntries(view.terms.map((term) => [term.key, term.label]));
}

/** What the term would show with this draft: an empty field means the default. */
function effective(term: TermView, value: string | undefined): string {
  const trimmed = (value ?? "").trim();
  return trimmed === "" ? term.defaultLabel : trimmed;
}
