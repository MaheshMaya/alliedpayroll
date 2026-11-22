import {
  Calendar,
  ChartColumn,
  Clock,
  House,
  Inbox,
  Landmark,
  MapPin,
  Settings,
  Users,
  Wallet,
  type LucideIcon,
} from "lucide-react";

/** Built destinations; a nav item without one renders disabled. */
export type NavPath = "/" | "/settings/users";

export interface NavLeaf {
  kind: "item";
  id: string;
  labelId: string;
  icon: LucideIcon;
  /** Route path; absent while the screen is not built yet. */
  to?: NavPath;
  /** Module flag that must be on; absent means always shown. */
  module?: string;
  /** Permission the API must have granted; absent means every member. */
  permission?: string;
}

export interface NavGroup {
  kind: "group";
  id: string;
  labelId: string;
  icon: LucideIcon;
  module?: string;
  permission?: string;
  children: readonly Omit<NavLeaf, "icon">[];
}

export type NavEntry = NavLeaf | NavGroup;

// Order and grouping follow the Figma Side Nav (39:265). Labels are message keys so industry
// terminology can replace them; groups for switched-off modules are hidden, never disabled.
export const primaryNav: readonly NavEntry[] = [
  { kind: "item", id: "home", labelId: "shell.nav.home", icon: House, to: "/" },
  {
    kind: "item",
    id: "approvals",
    labelId: "shell.nav.approvals",
    icon: Inbox,
  },
  {
    kind: "item",
    id: "month-close",
    labelId: "shell.nav.monthClose",
    icon: Calendar,
  },
  {
    kind: "group",
    id: "people",
    labelId: "shell.nav.people",
    icon: Users,
    children: [
      { kind: "item", id: "employees", labelId: "shell.nav.people.employees" },
      {
        kind: "item",
        id: "onboarding",
        labelId: "shell.nav.people.onboarding",
      },
    ],
  },
  {
    kind: "group",
    id: "time",
    labelId: "shell.nav.time",
    icon: Clock,
    children: [
      { kind: "item", id: "attendance", labelId: "shell.nav.time.attendance" },
      { kind: "item", id: "leave", labelId: "shell.nav.time.leave" },
    ],
  },
  {
    kind: "group",
    id: "payroll",
    labelId: "shell.nav.payroll",
    icon: Wallet,
    children: [
      { kind: "item", id: "runs", labelId: "shell.nav.payroll.runs" },
      {
        kind: "item",
        id: "structures",
        labelId: "shell.nav.payroll.structures",
      },
      { kind: "item", id: "arrears", labelId: "shell.nav.payroll.arrears" },
      { kind: "item", id: "payslips", labelId: "shell.nav.payroll.payslips" },
      { kind: "item", id: "bank", labelId: "shell.nav.payroll.bank" },
    ],
  },
  {
    kind: "group",
    id: "statutory",
    labelId: "shell.nav.statutory",
    icon: Landmark,
    children: [
      { kind: "item", id: "filings", labelId: "shell.nav.statutory.filings" },
      {
        kind: "item",
        id: "registers",
        labelId: "shell.nav.statutory.registers",
      },
    ],
  },
  {
    kind: "group",
    id: "clients-sites",
    labelId: "shell.nav.clientsSites",
    icon: MapPin,
    module: "module:CUSTOMERS",
    children: [
      {
        kind: "item",
        id: "clients",
        labelId: "shell.nav.clientsSites.clients",
      },
      { kind: "item", id: "sites", labelId: "shell.nav.clientsSites.sites" },
    ],
  },
  {
    kind: "item",
    id: "reports",
    labelId: "shell.nav.reports",
    icon: ChartColumn,
  },
];

export const settingsNav: NavLeaf = {
  kind: "item",
  id: "settings",
  labelId: "shell.nav.settings",
  icon: Settings,
  to: "/settings/users",
  permission: "identity:users_read",
};

export function visibleNav<T extends NavEntry>(
  entries: readonly T[],
  features: Readonly<Record<string, boolean>>,
  permissions: readonly string[],
): T[] {
  return entries.filter(
    (entry) =>
      (entry.module === undefined || features[entry.module] === true) &&
      (entry.permission === undefined ||
        permissions.includes(entry.permission)),
  );
}
