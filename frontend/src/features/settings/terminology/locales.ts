/** The languages the platform ships words for; the API refuses labels in any other. */
export const terminologyLocales = ["en-IN", "hi", "ta"] as const;

export type TerminologyLocale = (typeof terminologyLocales)[number];
