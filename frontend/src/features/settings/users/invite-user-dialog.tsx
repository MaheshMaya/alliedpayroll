import type { InviteUserRequest } from "@alliedpayroll/api-client";
import { useQuery } from "@tanstack/react-query";
import { useId, useState, type SubmitEvent } from "react";
import { FormattedMessage, useIntl } from "react-intl";

import { problemMessageId } from "@/shared/data/problem";
import { Button } from "@/shared/ui/button";
import { Dialog } from "@/shared/ui/dialog";
import { Input } from "@/shared/ui/input";
import { NativeSelect } from "@/shared/ui/native-select";
import { Skeleton } from "@/shared/ui/skeleton";

import { rolesOptions } from "../roles/roles-query";

import { RolePicker } from "./role-picker";
import { useInviteUser } from "./users-mutations";

type UserType = InviteUserRequest["userType"];
const invitableTypes = [
  "MANAGER",
  "EMPLOYEE",
  "ADMIN",
] as const satisfies readonly UserType[];

interface InviteUserDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onInvited: (userId: string) => void;
}

/**
 * Invite a person by email and choose their roles. The person gets their sign-in set up by the
 * identity provider and appears as Invited until they first sign in.
 */
export function InviteUserDialog({
  open,
  onOpenChange,
  onInvited,
}: InviteUserDialogProps) {
  const intl = useIntl();
  const roles = useQuery({ ...rolesOptions, enabled: open });
  const invite = useInviteUser();
  const nameId = useId();
  const emailId = useId();
  const typeId = useId();
  const [displayName, setDisplayName] = useState("");
  const [email, setEmail] = useState("");
  const [userType, setUserType] = useState<UserType>("MANAGER");
  const [roleIds, setRoleIds] = useState<Set<string>>(() => new Set());
  const [showMissingRole, setShowMissingRole] = useState(false);

  const reset = () => {
    setDisplayName("");
    setEmail("");
    setUserType("MANAGER");
    setRoleIds(new Set());
    setShowMissingRole(false);
    invite.reset();
  };

  const submit = (event: SubmitEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (roleIds.size === 0) {
      setShowMissingRole(true);
      return;
    }
    invite.mutate(
      {
        displayName: displayName.trim(),
        email: email.trim(),
        userType,
        roleIds: [...roleIds],
      },
      {
        onSuccess: (user) => {
          reset();
          onInvited(user.id);
        },
      },
    );
  };

  return (
    <Dialog
      open={open}
      onOpenChange={(next) => {
        if (!next) reset();
        onOpenChange(next);
      }}
      title={<FormattedMessage id="settings.users.invite.title" />}
      description={<FormattedMessage id="settings.users.invite.description" />}
      closeLabel={intl.formatMessage({ id: "settings.users.invite.cancel" })}
    >
      <form className="flex flex-col gap-5" onSubmit={submit}>
        <div className="flex flex-col gap-1">
          <label htmlFor={nameId} className="text-small text-text-secondary">
            <FormattedMessage id="settings.users.invite.name" />
          </label>
          <Input
            id={nameId}
            required
            maxLength={150}
            autoComplete="off"
            value={displayName}
            onChange={(event) => {
              setDisplayName(event.target.value);
            }}
          />
        </div>
        <div className="flex flex-col gap-1">
          <label htmlFor={emailId} className="text-small text-text-secondary">
            <FormattedMessage id="settings.users.invite.email" />
          </label>
          <Input
            id={emailId}
            type="email"
            required
            maxLength={80}
            autoComplete="off"
            value={email}
            onChange={(event) => {
              setEmail(event.target.value);
            }}
          />
          <p className="text-caption text-text-muted">
            <FormattedMessage id="settings.users.invite.emailHint" />
          </p>
        </div>
        <div className="flex flex-col gap-1">
          <label htmlFor={typeId} className="text-small text-text-secondary">
            <FormattedMessage id="settings.users.detail.type" />
          </label>
          <NativeSelect
            id={typeId}
            value={userType}
            onChange={(event) => {
              const next = invitableTypes.find((t) => t === event.target.value);
              if (next !== undefined) setUserType(next);
            }}
          >
            {invitableTypes.map((type) => (
              <option key={type} value={type}>
                {intl.formatMessage({ id: `user.type.${type}` })}
              </option>
            ))}
          </NativeSelect>
        </div>

        {roles.isPending && (
          <div className="flex flex-col gap-2" aria-busy="true">
            <Skeleton className="h-6 w-1/2" />
            <Skeleton className="h-6 w-2/3" />
            <Skeleton className="h-6 w-1/2" />
          </div>
        )}
        {roles.isSuccess && (
          <div className="flex flex-col gap-1">
            <RolePicker
              roles={roles.data}
              selected={roleIds}
              onChange={(next) => {
                setRoleIds(next);
                if (next.size > 0) setShowMissingRole(false);
              }}
              legendId="settings.users.invite.roles"
            />
            {showMissingRole && (
              <p role="alert" className="text-small text-status-danger-fg">
                <FormattedMessage id="settings.users.invite.rolesRequired" />
              </p>
            )}
          </div>
        )}
        {roles.isError && (
          <p role="alert" className="text-small text-status-danger-fg">
            <FormattedMessage id="settings.roles.error" />
          </p>
        )}

        {invite.isError && (
          <p
            role="alert"
            className="rounded-md bg-status-danger-bg px-3 py-2 text-small text-status-danger-fg"
          >
            <FormattedMessage id={problemMessageId(invite.error)} />
          </p>
        )}

        <div className="flex justify-end gap-3">
          <Button
            type="button"
            variant="secondary"
            onClick={() => {
              reset();
              onOpenChange(false);
            }}
          >
            <FormattedMessage id="settings.users.invite.cancel" />
          </Button>
          <Button type="submit" disabled={invite.isPending}>
            <FormattedMessage
              id={
                invite.isPending
                  ? "settings.users.invite.sending"
                  : "settings.users.invite.submit"
              }
            />
          </Button>
        </div>
      </form>
    </Dialog>
  );
}
