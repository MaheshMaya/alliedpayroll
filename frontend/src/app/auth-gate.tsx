import { useEffect, type ReactNode } from "react";
import { FormattedMessage } from "react-intl";

import { BootstrapProvider } from "@/shared/data/bootstrap-context";
import {
  redirectToLogin,
  useBootstrapQuery,
  useSession,
} from "@/shared/data/session";
import { SignOutForm } from "@/shared/shell/sign-out-form";
import { Button } from "@/shared/ui/button";

/**
 * Nothing renders until the backend confirms a session. No session sends the browser to the
 * backend's login endpoint, which runs the Keycloak flow for this tenant host and returns here.
 */
export function AuthGate({ children }: { children: ReactNode }) {
  const session = useSession();
  const signedOut = session.isSuccess && session.data === null;

  useEffect(() => {
    if (signedOut) {
      redirectToLogin(window.location.pathname + window.location.search);
    }
  }, [signedOut]);

  if (session.isPending || signedOut) {
    return <FullPageStatus messageId="auth.loading" />;
  }
  if (session.isError) {
    return (
      <FullPageStatus messageId="auth.error.title">
        <Button
          variant="secondary"
          onClick={() => {
            void session.refetch();
          }}
        >
          <FormattedMessage id="auth.error.retry" />
        </Button>
      </FullPageStatus>
    );
  }
  return <BootstrapGate>{children}</BootstrapGate>;
}

function BootstrapGate({ children }: { children: ReactNode }) {
  const bootstrap = useBootstrapQuery();
  if (bootstrap.isPending) {
    return <FullPageStatus messageId="auth.loading" />;
  }
  if (bootstrap.isError && bootstrap.error.code === "identity.user_disabled") {
    // The tenant disabled this person while their session was still open.
    return (
      <FullPageStatus messageId="auth.disabled">
        <SignOutForm />
      </FullPageStatus>
    );
  }
  if (bootstrap.isError && bootstrap.error.status === 403) {
    // Signed in at Keycloak, but not a member of this host's organisation.
    return (
      <FullPageStatus messageId="auth.notMember">
        <SignOutForm />
      </FullPageStatus>
    );
  }
  if (bootstrap.isError) {
    return (
      <FullPageStatus messageId="auth.error.title">
        <Button
          variant="secondary"
          onClick={() => {
            void bootstrap.refetch();
          }}
        >
          <FormattedMessage id="auth.error.retry" />
        </Button>
      </FullPageStatus>
    );
  }
  return (
    <BootstrapProvider value={bootstrap.data}>{children}</BootstrapProvider>
  );
}

function FullPageStatus({
  messageId,
  children,
}: {
  messageId: string;
  children?: ReactNode;
}) {
  return (
    <div
      role="status"
      className="flex min-h-svh flex-col items-center justify-center gap-4 bg-bg-canvas px-4 text-text-secondary"
    >
      <p className="text-body">
        <FormattedMessage id={messageId} />
      </p>
      {children}
    </div>
  );
}
