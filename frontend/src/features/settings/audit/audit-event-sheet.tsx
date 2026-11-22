import type { AuditEventView } from "@alliedpayroll/api-client";
import type { ReactNode } from "react";
import { FormattedDate, FormattedMessage, useIntl } from "react-intl";

import { Sheet } from "@/shared/ui/sheet";

import { useActorName, useReasonText } from "./audit-labels";

interface AuditEventSheetProps {
  event: AuditEventView;
  onClose: () => void;
}

/** Quick look at one event: who, from where, on what, why, and the masked before and after. */
export function AuditEventSheet({ event, onClose }: AuditEventSheetProps) {
  const intl = useIntl();
  const actorName = useActorName();
  const reasonText = useReasonText();
  const reason = reasonText(event);
  const fields = event.changedFields;
  const hasValues =
    fields.length > 0 &&
    (event.oldValues !== undefined || event.newValues !== undefined);

  return (
    <Sheet
      open
      onOpenChange={(open) => {
        if (!open) onClose();
      }}
      title={intl.formatMessage({ id: `audit.action.${event.action}` })}
      description={intl.formatMessage({ id: "settings.audit.detail.title" })}
      closeLabel={intl.formatMessage({ id: "settings.audit.detail.close" })}
    >
      <div className="flex flex-col gap-6">
        <p className="text-small text-text-muted">
          <FormattedDate
            value={event.occurredAt}
            day="numeric"
            month="short"
            year="numeric"
            hour="2-digit"
            minute="2-digit"
            second="2-digit"
          />
        </p>
        <dl className="grid grid-cols-[auto_1fr] gap-x-6 gap-y-3 text-body">
          <Fact labelId="settings.audit.detail.actor">{actorName(event)}</Fact>
          <Fact labelId="settings.audit.detail.channel">
            <FormattedMessage
              id={`audit.channel.${event.channel}`}
              defaultMessage={event.channel}
            />
          </Fact>
          {event.clientIp !== undefined && (
            <Fact labelId="settings.audit.detail.ip">
              <span className="font-mono text-small">{event.clientIp}</span>
            </Fact>
          )}
          {event.entityType !== undefined && (
            <Fact labelId="settings.audit.detail.entity">
              <span className="font-mono text-small">
                {event.entityType}
                {event.entityLabel !== undefined && ` · ${event.entityLabel}`}
              </span>
            </Fact>
          )}
          {reason !== undefined && (
            <Fact labelId="settings.audit.detail.reason">{reason}</Fact>
          )}
        </dl>

        <section
          aria-labelledby="audit-changes"
          className="flex flex-col gap-2"
        >
          <h3
            id="audit-changes"
            className="text-overline tracking-[0.04em] text-text-muted uppercase"
          >
            <FormattedMessage id="settings.audit.detail.changes" />
          </h3>
          {hasValues ? (
            <table className="w-full border-collapse text-small">
              <thead className="text-caption text-text-muted">
                <tr>
                  <th scope="col" className="py-1 pr-3 text-left font-medium" />
                  <th scope="col" className="py-1 pr-3 text-left font-medium">
                    <FormattedMessage id="settings.audit.detail.before" />
                  </th>
                  <th scope="col" className="py-1 text-left font-medium">
                    <FormattedMessage id="settings.audit.detail.after" />
                  </th>
                </tr>
              </thead>
              <tbody className="divide-y divide-border-divider">
                {fields.map((field) => (
                  <tr key={field}>
                    <th
                      scope="row"
                      className="py-1.5 pr-3 text-left font-mono text-caption text-text-secondary"
                    >
                      {field}
                    </th>
                    <td className="py-1.5 pr-3 font-mono text-text-muted">
                      {show(event.oldValues?.[field])}
                    </td>
                    <td className="py-1.5 font-mono text-text-default">
                      {show(event.newValues?.[field])}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          ) : (
            <p className="text-small text-text-muted">
              <FormattedMessage id="settings.audit.detail.noChanges" />
            </p>
          )}
        </section>
      </div>
    </Sheet>
  );
}

function show(value: unknown): string {
  if (value === undefined || value === null) return "";
  return typeof value === "string" ? value : JSON.stringify(value);
}

function Fact({ labelId, children }: { labelId: string; children: ReactNode }) {
  return (
    <>
      <dt className="text-small text-text-muted">
        <FormattedMessage id={labelId} />
      </dt>
      <dd className="min-w-0 text-text-default">{children}</dd>
    </>
  );
}
