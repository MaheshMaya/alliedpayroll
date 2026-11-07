import { FormattedMessage, useIntl } from "react-intl";

import { useBootstrap } from "@/shared/data/bootstrap-context";

const profileMessageIds: Record<string, string> = {
  STAFFING: "home.profile.STAFFING",
  FIELD_SERVICE: "home.profile.FIELD_SERVICE",
  CORPORATE: "home.profile.CORPORATE",
};

/**
 * Home for the sign-in slice: who you are and where you are. Attention rows, month-close
 * readiness and KPIs arrive with their modules; nothing is mocked in their place.
 */
export function HomePage() {
  const intl = useIntl();
  const { tenant, user } = useBootstrap();
  const profileId = profileMessageIds[tenant.industryProfileCode];
  const profile =
    profileId === undefined
      ? tenant.industryProfileCode
      : intl.formatMessage({ id: profileId });
  const displayName = user.displayName ?? user.subject;

  return (
    <div className="flex flex-col gap-6">
      <header className="flex flex-col gap-1">
        <h1 className="text-h1 text-text-default">
          <FormattedMessage id="home.title" />
        </h1>
        <p className="text-small text-text-secondary">
          <FormattedMessage
            id="home.subtitle"
            values={{ tenant: tenant.name, profile }}
          />
        </p>
      </header>

      <section
        aria-labelledby="workspace-title"
        className="rounded-lg border border-border-default bg-bg-surface shadow-sm"
      >
        <div className="border-b border-border-divider px-6 py-4">
          <h2 id="workspace-title" className="text-h3 text-text-default">
            <FormattedMessage id="home.workspace.title" />
          </h2>
        </div>
        <dl className="grid grid-cols-1 gap-x-8 gap-y-4 px-6 py-4 md:grid-cols-2">
          <Fact
            labelId="home.workspace.user"
            value={displayName}
            detail={user.email}
          />
          <Fact
            labelId="home.workspace.tenant"
            value={tenant.name}
            detail={tenant.slug}
          />
          <Fact labelId="home.workspace.profile" value={profile} />
          <Fact
            labelId="home.workspace.locale"
            value={
              intl.formatDisplayName(tenant.defaultLocale, {
                type: "language",
              }) ?? tenant.defaultLocale
            }
            detail={tenant.timezone}
          />
        </dl>
      </section>

      <section
        aria-labelledby="attention-title"
        className="rounded-lg border border-dashed border-border-default px-6 py-8 text-center"
      >
        <h2 id="attention-title" className="text-h3 text-text-default">
          <FormattedMessage id="home.empty.title" />
        </h2>
        <p className="mx-auto mt-1 max-w-prose text-body text-text-secondary">
          <FormattedMessage id="home.empty.body" />
        </p>
      </section>
    </div>
  );
}

function Fact({
  labelId,
  value,
  detail,
}: {
  labelId: string;
  value: string;
  detail?: string | undefined;
}) {
  return (
    <div className="flex flex-col gap-0.5">
      <dt className="text-overline tracking-[0.04em] text-text-muted uppercase">
        <FormattedMessage id={labelId} />
      </dt>
      <dd className="text-bodyStrong text-text-default">{value}</dd>
      {detail !== undefined && (
        <dd className="text-small text-text-secondary">{detail}</dd>
      )}
    </div>
  );
}
