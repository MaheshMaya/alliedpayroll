import type { AuditEventView } from "@alliedpayroll/api-client";
import { useIntl } from "react-intl";

const SYSTEM_ACTOR = "00000000-0000-0000-0000-000000000001";

/** The actor as the list names them: a person, the system, or someone the tenant never provisioned. */
export function useActorName() {
  const intl = useIntl();
  return (event: AuditEventView): string => {
    if (event.actorDisplayName !== undefined) return event.actorDisplayName;
    if (event.actorType === "SYSTEM" || event.actorId === SYSTEM_ACTOR) {
      return intl.formatMessage({ id: "audit.actor.system" });
    }
    return intl.formatMessage({ id: "audit.actor.unknown" });
  };
}

/** The reason as a sentence; an unknown key shows as itself, never hidden. */
export function useReasonText() {
  const intl = useIntl();
  return (event: AuditEventView): string | undefined => {
    if (event.reasonKey === undefined) return undefined;
    return intl.formatMessage({
      id: `reason.${event.reasonKey}`,
      defaultMessage: event.reasonKey,
    });
  };
}
