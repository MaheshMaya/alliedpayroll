import type { UserDetail } from "@alliedpayroll/api-client";
import { useState } from "react";
import { FormattedMessage, useIntl } from "react-intl";

import { useBootstrap } from "@/shared/data/bootstrap-context";
import { problemMessageId } from "@/shared/data/problem";
import { Button } from "@/shared/ui/button";
import { Dialog } from "@/shared/ui/dialog";

import { useSetUserAccess } from "./users-mutations";

/**
 * Disable or enable a user. Disabling signs them out everywhere, so it asks first and says so; the
 * signed-in administrator does not get the action on their own record.
 */
export function UserAccessActions({ user }: { user: UserDetail }) {
  const intl = useIntl();
  const { user: me } = useBootstrap();
  const access = useSetUserAccess();
  const [confirming, setConfirming] = useState(false);
  const disabled = user.status === "DISABLED" || user.status === "LOCKED";

  if (me.id === user.id) return null;

  if (disabled) {
    return (
      <div className="flex flex-col items-start gap-2">
        <Button
          variant="secondary"
          disabled={access.isPending}
          onClick={() => {
            access.mutate({ id: user.id, enabled: true });
          }}
        >
          <FormattedMessage id="settings.users.access.enable" />
        </Button>
        {access.isError && (
          <p role="alert" className="text-small text-status-danger-fg">
            <FormattedMessage id={problemMessageId(access.error)} />
          </p>
        )}
      </div>
    );
  }

  return (
    <>
      <Button
        variant="secondary"
        className="self-start"
        onClick={() => {
          access.reset();
          setConfirming(true);
        }}
      >
        <FormattedMessage id="settings.users.access.disable" />
      </Button>
      <Dialog
        open={confirming}
        onOpenChange={setConfirming}
        title={
          <FormattedMessage
            id="settings.users.access.confirmTitle"
            values={{ name: user.displayName }}
          />
        }
        description={
          <FormattedMessage id="settings.users.access.confirmBody" />
        }
        closeLabel={intl.formatMessage({ id: "settings.users.access.keep" })}
      >
        {access.isError && (
          <p
            role="alert"
            className="rounded-md bg-status-danger-bg px-3 py-2 text-small text-status-danger-fg"
          >
            <FormattedMessage id={problemMessageId(access.error)} />
          </p>
        )}
        <div className="flex justify-end gap-3">
          <Button
            variant="secondary"
            onClick={() => {
              setConfirming(false);
            }}
          >
            <FormattedMessage id="settings.users.access.keep" />
          </Button>
          <Button
            variant="danger"
            disabled={access.isPending}
            onClick={() => {
              access.mutate(
                { id: user.id, enabled: false },
                {
                  onSuccess: () => {
                    setConfirming(false);
                  },
                },
              );
            }}
          >
            <FormattedMessage id="settings.users.access.disable" />
          </Button>
        </div>
      </Dialog>
    </>
  );
}
