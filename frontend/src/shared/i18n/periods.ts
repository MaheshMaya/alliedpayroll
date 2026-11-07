import type { IntlShape } from "react-intl";

/** Indian financial year label for a date: April 2026 to March 2027 is "FY 2026-27". */
export function financialYearLabel(date: Date): string {
  const year = date.getFullYear();
  const startYear = date.getMonth() >= 3 ? year : year - 1;
  const endYear = (startYear + 1) % 100;
  return `FY ${String(startYear)}-${endYear.toString().padStart(2, "0")}`;
}

/** "Nov 2026 · FY 2026-27", the period chip's default value. */
export function currentPeriodLabel(intl: IntlShape, date: Date): string {
  const month = intl.formatDate(date, { month: "short", year: "numeric" });
  return `${month} · ${financialYearLabel(date)}`;
}
