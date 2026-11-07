import { FormattedMessage } from "react-intl";

import { readCsrfToken } from "@/shared/data/api-client";
import { Button } from "@/shared/ui/button";

/**
 * Sign-out is a full-page form post to the backend, which ends the session, then the Keycloak
 * session, and lands back on this host's root. A plain form keeps it working without scripts.
 */
export function SignOutForm() {
  return (
    <form method="post" action="/auth/logout">
      <input type="hidden" name="_csrf" value={readCsrfToken() ?? ""} />
      <Button type="submit" variant="secondary">
        <FormattedMessage id="auth.signOut" />
      </Button>
    </form>
  );
}
