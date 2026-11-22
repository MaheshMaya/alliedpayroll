import { useQuery } from "@tanstack/react-query";
import { useId, useState, type SubmitEvent } from "react";
import { FormattedMessage, useIntl } from "react-intl";

import { problemMessageId } from "@/shared/data/problem";
import { Button } from "@/shared/ui/button";
import { Dialog } from "@/shared/ui/dialog";
import { Input } from "@/shared/ui/input";

import { PermissionChecklist } from "./permission-checklist";
import { permissionsOptions, useCreateRole } from "./roles-query";

const CODE_PATTERN = "[A-Z][A-Z0-9_]{1,39}";

interface NewRoleDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onCreated: (roleId: string) => void;
}

/** A role of the tenant's own: a short code, a name people recognise, and what it allows. */
export function NewRoleDialog({
  open,
  onOpenChange,
  onCreated,
}: NewRoleDialogProps) {
  const intl = useIntl();
  const catalogue = useQuery({ ...permissionsOptions, enabled: open });
  const create = useCreateRole();
  const nameId = useId();
  const codeId = useId();
  const descriptionId = useId();
  const [name, setName] = useState("");
  const [code, setCode] = useState("");
  const [description, setDescription] = useState("");
  const [permissions, setPermissions] = useState<Set<string>>(() => new Set());

  const reset = () => {
    setName("");
    setCode("");
    setDescription("");
    setPermissions(new Set<string>());
    create.reset();
  };

  const submit = (event: SubmitEvent<HTMLFormElement>) => {
    event.preventDefault();
    const trimmed = description.trim();
    create.mutate(
      {
        code,
        name: name.trim(),
        ...(trimmed === "" ? {} : { description: trimmed }),
        permissions: [...permissions],
      },
      {
        onSuccess: (role) => {
          reset();
          onCreated(role.id);
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
      title={<FormattedMessage id="settings.roles.new.title" />}
      description={<FormattedMessage id="settings.roles.new.description" />}
      closeLabel={intl.formatMessage({ id: "settings.roles.new.cancel" })}
    >
      <form className="flex flex-col gap-5" onSubmit={submit}>
        <div className="flex flex-col gap-1">
          <label htmlFor={nameId} className="text-small text-text-secondary">
            <FormattedMessage id="settings.roles.new.name" />
          </label>
          <Input
            id={nameId}
            required
            maxLength={80}
            value={name}
            onChange={(event) => {
              setName(event.target.value);
            }}
          />
        </div>
        <div className="flex flex-col gap-1">
          <label htmlFor={codeId} className="text-small text-text-secondary">
            <FormattedMessage id="settings.roles.new.code" />
          </label>
          <Input
            id={codeId}
            required
            pattern={CODE_PATTERN}
            maxLength={40}
            className="font-mono"
            aria-describedby={`${codeId}-hint`}
            value={code}
            onChange={(event) => {
              setCode(event.target.value.toUpperCase().replace(/[\s-]/g, "_"));
            }}
          />
          <p id={`${codeId}-hint`} className="text-caption text-text-muted">
            <FormattedMessage id="settings.roles.new.codeHint" />
          </p>
        </div>
        <div className="flex flex-col gap-1">
          <label
            htmlFor={descriptionId}
            className="text-small text-text-secondary"
          >
            <FormattedMessage id="settings.roles.new.purpose" />
          </label>
          <Input
            id={descriptionId}
            maxLength={255}
            value={description}
            onChange={(event) => {
              setDescription(event.target.value);
            }}
          />
        </div>
        {catalogue.isSuccess && (
          <PermissionChecklist
            catalogue={catalogue.data}
            selected={permissions}
            onChange={setPermissions}
          />
        )}
        {catalogue.isError && (
          <p role="alert" className="text-small text-status-danger-fg">
            <FormattedMessage id="settings.roles.error" />
          </p>
        )}
        {create.isError && (
          <p
            role="alert"
            className="rounded-md bg-status-danger-bg px-3 py-2 text-small text-status-danger-fg"
          >
            <FormattedMessage id={problemMessageId(create.error)} />
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
            <FormattedMessage id="settings.roles.new.cancel" />
          </Button>
          <Button type="submit" disabled={create.isPending}>
            <FormattedMessage id="settings.roles.new.submit" />
          </Button>
        </div>
      </form>
    </Dialog>
  );
}
