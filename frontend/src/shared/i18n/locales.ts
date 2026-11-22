import enIN from "./messages/en-IN.json";
import hi from "./messages/hi.json";
import qpsPloc from "./messages/qps-ploc.json";
import ta from "./messages/ta.json";

export const defaultLocale = "en-IN";

// qps-ploc is the pseudo-locale (pnpm i18n:pseudo); it exposes hard-coded strings and clipping.
export const messagesByLocale = {
  "en-IN": enIN,
  hi,
  ta,
  "qps-ploc": qpsPloc,
} satisfies Record<string, Record<string, string>>;

export type Locale = keyof typeof messagesByLocale;

export function isLocale(value: string): value is Locale {
  return Object.hasOwn(messagesByLocale, value);
}
