import { useBootstrap } from "@/shared/data/bootstrap-context";

/**
 * The tenant's words for the canonical concepts as ICU arguments: `site` stays `site`,
 * `site_sub_unit.plural` becomes `siteSubUnitPlural`. Messages name a term through its
 * argument ("{clientPlural} & {sitePlural}") and never write "Site" themselves.
 */
export function termValues(
  terminology: Readonly<Record<string, string>>,
): Record<string, string> {
  return Object.fromEntries(
    Object.entries(terminology).map(([key, label]) => [
      argumentName(key),
      label,
    ]),
  );
}

/** The signed-in tenant's terms, ready to pass as `values` to a message. */
export function useTerms(): Record<string, string> {
  return termValues(useBootstrap().terminology);
}

function argumentName(key: string): string {
  return key
    .replace(".plural", "_plural")
    .split("_")
    .map((part, index) =>
      index === 0 ? part : part.charAt(0).toUpperCase() + part.slice(1),
    )
    .join("");
}
