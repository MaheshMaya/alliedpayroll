import { FormattedMessage } from "react-intl";

import { Badge } from "@/shared/ui/badge";

import type { UserStatus } from "./users-query";

// Status colours are the fixed status tokens: a disabled or locked account must read as such
// even when the tenant's accent is close to the info blue.
const statusVariant = {
  ACTIVE: "success",
  INVITED: "info",
  DISABLED: "neutral",
  LOCKED: "danger",
} as const satisfies Record<
  UserStatus,
  "success" | "info" | "neutral" | "danger"
>;

export function UserStatusBadge({ status }: { status: UserStatus }) {
  return (
    <Badge status={statusVariant[status]} dot>
      <FormattedMessage id={`user.status.${status}`} />
    </Badge>
  );
}
