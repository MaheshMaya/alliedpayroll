import {
  Activity,
  Bell,
  CircleHelp,
  History,
  Keyboard,
  LoaderCircle,
  Pin,
  Zap,
  type LucideIcon,
} from "lucide-react";
import { useState } from "react";
import { useIntl } from "react-intl";

import { useBootstrap } from "@/shared/data/bootstrap-context";
import { Avatar } from "@/shared/ui/avatar";
import { cn } from "@/shared/ui/cn";

import { RailPanel } from "./rail-panel";
import { SignOutForm } from "./sign-out-form";

interface RailButtonSpec {
  id: string;
  labelId: string;
  keys: string;
  icon: LucideIcon;
}

// Order and shortcuts from the Figma Utility Rail (39:477).
const topButtons: readonly RailButtonSpec[] = [
  {
    id: "quick-actions",
    labelId: "shell.rail.quickActions",
    keys: "Alt Q",
    icon: Zap,
  },
  {
    id: "notifications",
    labelId: "shell.rail.notifications",
    keys: "Alt N",
    icon: Bell,
  },
  { id: "recent", labelId: "shell.rail.recent", keys: "Alt R", icon: History },
  { id: "pinned", labelId: "shell.rail.pinned", keys: "Alt P", icon: Pin },
  { id: "jobs", labelId: "shell.rail.jobs", keys: "Alt J", icon: LoaderCircle },
  {
    id: "activity",
    labelId: "shell.rail.activity",
    keys: "Alt A",
    icon: Activity,
  },
];
const bottomButtons: readonly RailButtonSpec[] = [
  {
    id: "shortcuts",
    labelId: "shell.rail.shortcuts",
    keys: "?",
    icon: Keyboard,
  },
  { id: "help", labelId: "shell.rail.help", keys: "", icon: CircleHelp },
];

/** Right rail, 56px: every button opens a 360px panel beside the page; Esc closes it. */
export function UtilityRail() {
  const intl = useIntl();
  const { user } = useBootstrap();
  const [openPanel, setOpenPanel] = useState<string | null>(null);
  const toggle = (id: string) => {
    setOpenPanel((current) => (current === id ? null : id));
  };
  const open = [...topButtons, ...bottomButtons].find(
    (b) => b.id === openPanel,
  );
  const profileLabel = intl.formatMessage({ id: "shell.rail.profile" });

  return (
    <>
      {openPanel === "profile" && (
        <RailPanel
          title={user.displayName ?? user.subject}
          onClose={() => {
            setOpenPanel(null);
          }}
        >
          <p className="text-small text-text-secondary">{user.email}</p>
          <SignOutForm />
        </RailPanel>
      )}
      {open !== undefined && (
        <RailPanel
          title={intl.formatMessage({ id: open.labelId })}
          onClose={() => {
            setOpenPanel(null);
          }}
        >
          <p className="text-body text-text-secondary">
            {intl.formatMessage({ id: "shell.rail.empty" })}
          </p>
        </RailPanel>
      )}
      <aside
        aria-label={intl.formatMessage({ id: "shell.rail.label" })}
        className="flex h-full w-[56px] shrink-0 flex-col items-center gap-1 border-l border-border-divider bg-bg-surface px-2 py-3"
      >
        <button
          type="button"
          onClick={() => {
            toggle("profile");
          }}
          aria-label={profileLabel}
          aria-expanded={openPanel === "profile"}
          title={profileLabel}
          className="flex size-10 items-center justify-center rounded-md hover:bg-bg-subtle"
        >
          <Avatar name={user.displayName} fallback={user.subject} />
        </button>
        <span aria-hidden="true" className="my-1 h-px w-6 bg-border-divider" />
        {topButtons.map((spec) => (
          <RailButton
            key={spec.id}
            spec={spec}
            active={openPanel === spec.id}
            onToggle={toggle}
          />
        ))}
        <span className="flex-1" />
        {bottomButtons.map((spec) => (
          <RailButton
            key={spec.id}
            spec={spec}
            active={openPanel === spec.id}
            onToggle={toggle}
          />
        ))}
      </aside>
    </>
  );
}

function RailButton({
  spec,
  active,
  onToggle,
}: {
  spec: RailButtonSpec;
  active: boolean;
  onToggle: (id: string) => void;
}) {
  const intl = useIntl();
  const name = intl.formatMessage({ id: spec.labelId });
  const hint =
    spec.keys === ""
      ? name
      : intl.formatMessage(
          { id: "shell.rail.shortcutHint" },
          { name, keys: spec.keys },
        );
  const Icon = spec.icon;
  return (
    <button
      type="button"
      onClick={() => {
        onToggle(spec.id);
      }}
      aria-label={name}
      aria-expanded={active}
      title={hint}
      className={cn(
        "flex size-10 items-center justify-center rounded-md text-text-secondary hover:bg-bg-subtle",
        active && "bg-bg-selected text-text-link",
      )}
    >
      <Icon aria-hidden="true" className="size-5" />
    </button>
  );
}
