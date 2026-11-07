import enIN from "./messages/en-IN.json";
import qpsPloc from "./messages/qps-ploc.json";

export const defaultLocale = "en-IN";

// qps-ploc is the pseudo-locale (pnpm i18n:pseudo); it exposes hard-coded strings and clipping.
export const messagesByLocale = {
  "en-IN": enIN,
  "qps-ploc": qpsPloc,
} satisfies Record<string, Record<string, string>>;

export type Locale = keyof typeof messagesByLocale;

export function isLocale(value: string): value is Locale {
  return Object.hasOwn(messagesByLocale, value);
}
