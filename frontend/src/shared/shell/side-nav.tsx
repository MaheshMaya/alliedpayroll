import { Link } from "@tanstack/react-router";
import { ChevronDown, ChevronRight, PanelLeft } from "lucide-react";
import { useState } from "react";
import { FormattedMessage, useIntl } from "react-intl";

import { useBootstrap } from "@/shared/data/bootstrap-context";
import { initialsOf } from "@/shared/ui/avatar";
import { cn } from "@/shared/ui/cn";

import {
  primaryNav,
  settingsNav,
  visibleNav,
  type NavGroup,
  type NavLeaf,
} from "./nav-config";

interface SideNavProps {
  collapsed: boolean;
  onToggle: () => void;
}

/**
 * Primary navigation from the Figma Side Nav (39:265): dark, 232px, one group open at a time,
 * collapsible to a 72px rail with icons and tooltips. Active items carry aria-current.
 */
export function SideNav({ collapsed, onToggle }: SideNavProps) {
  const intl = useIntl();
  const { tenant, features } = useBootstrap();
  const [openGroup, setOpenGroup] = useState<string | null>(null);
  const entries = visibleNav(primaryNav, features);

  return (
    <nav
      aria-label={intl.formatMessage({ id: "shell.nav.label" })}
      data-collapsed={collapsed ? "true" : "false"}
      className={cn(
        "flex h-full shrink-0 flex-col gap-1 overflow-y-auto bg-bg-nav px-2 pt-5 pb-4 text-text-nav",
        collapsed ? "w-[72px]" : "w-[232px]",
      )}
    >
      <div
        className={cn(
          "flex items-center gap-3 pb-4",
          collapsed ? "flex-col px-0" : "pr-1 pl-2",
        )}
      >
        <span
          aria-hidden="true"
          className="flex size-8 shrink-0 items-center justify-center rounded-md bg-accent-default text-caption text-accent-on"
        >
          {initialsOf(tenant.name, tenant.slug)}
        </span>
        {!collapsed && (
          <div className="flex min-w-0 flex-1 flex-col">
            <span className="truncate text-bodyStrong text-accent-on">
              {tenant.name}
            </span>
            <span className="text-caption">
              <FormattedMessage id="shell.brand.product" />
            </span>
          </div>
        )}
        <button
          type="button"
          onClick={onToggle}
          aria-expanded={!collapsed}
          aria-label={intl.formatMessage({
            id: collapsed ? "shell.nav.expand" : "shell.nav.collapse",
          })}
          title={intl.formatMessage(
            { id: "shell.rail.shortcutHint" },
            {
              name: intl.formatMessage({
                id: collapsed ? "shell.nav.expand" : "shell.nav.collapse",
              }),
              keys: "[",
            },
          )}
          className="flex size-8 shrink-0 items-center justify-center rounded-md hover:bg-bg-navActive focus-visible:outline-text-nav"
        >
          <PanelLeft className="size-[18px]" />
        </button>
      </div>

      {entries.map((entry) =>
        entry.kind === "group" ? (
          <Group
            key={entry.id}
            group={entry}
            collapsed={collapsed}
            open={openGroup === entry.id}
            onToggle={() => {
              setOpenGroup((current) =>
                current === entry.id ? null : entry.id,
              );
            }}
          />
        ) : (
          <Leaf key={entry.id} item={entry} collapsed={collapsed} />
        ),
      )}

      <div className="flex-1" />
      <div className="h-px w-full bg-bg-navActive" />
      <Leaf item={settingsNav} collapsed={collapsed} />
    </nav>
  );
}

const rowClass =
  "flex h-10 w-full items-center gap-3 rounded-md px-3 text-body hover:bg-bg-navActive focus-visible:outline-text-nav";

function Leaf({ item, collapsed }: { item: NavLeaf; collapsed: boolean }) {
  const intl = useIntl();
  const label = intl.formatMessage({ id: item.labelId });
  const Icon = item.icon;
  const content = (
    <>
      <Icon aria-hidden="true" className="size-[18px] shrink-0" />
      {!collapsed && <span className="min-w-0 flex-1 truncate">{label}</span>}
    </>
  );
  if (item.to === undefined) {
    return (
      <span
        aria-disabled="true"
        title={collapsed ? label : undefined}
        className={cn(
          rowClass,
          "cursor-not-allowed opacity-60 hover:bg-transparent",
        )}
      >
        {content}
        {!collapsed && <span className="sr-only">{label}</span>}
      </span>
    );
  }
  return (
    <Link
      to={item.to}
      title={collapsed ? label : undefined}
      aria-label={collapsed ? label : undefined}
      activeOptions={{ exact: true }}
      activeProps={{
        "aria-current": "page",
        className: cn(
          rowClass,
          "relative bg-bg-navActive text-bodyStrong text-accent-on",
        ),
      }}
      inactiveProps={{ className: rowClass }}
    >
      {content}
    </Link>
  );
}

function Group({
  group,
  collapsed,
  open,
  onToggle,
}: {
  group: NavGroup;
  collapsed: boolean;
  open: boolean;
  onToggle: () => void;
}) {
  const intl = useIntl();
  const label = intl.formatMessage({ id: group.labelId });
  const Icon = group.icon;
  const panelId = `nav-group-${group.id}`;
  return (
    <div className="flex w-full flex-col gap-0.5">
      <button
        type="button"
        onClick={onToggle}
        aria-expanded={open}
        aria-controls={panelId}
        title={collapsed ? label : undefined}
        className={cn(rowClass, open && "text-bodyStrong text-accent-on")}
      >
        <Icon aria-hidden="true" className="size-[18px] shrink-0" />
        {!collapsed && (
          <>
            <span className="min-w-0 flex-1 truncate text-left">{label}</span>
            {open ? (
              <ChevronDown aria-hidden="true" className="size-4" />
            ) : (
              <ChevronRight aria-hidden="true" className="size-4" />
            )}
          </>
        )}
        {collapsed && <span className="sr-only">{label}</span>}
      </button>
      {open && !collapsed && (
        <ul id={panelId} className="flex flex-col gap-0.5 pt-0.5 pb-1">
          {group.children.map((child) => (
            <li key={child.id}>
              <span
                aria-disabled="true"
                className="flex h-8 w-full cursor-not-allowed items-center rounded-md pr-3 pl-[42px] text-body opacity-60"
              >
                <FormattedMessage id={child.labelId} />
              </span>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
