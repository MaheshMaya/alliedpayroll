import type { ReactNode } from "react";
import { IntlProvider } from "react-intl";

import { defaultLocale, messagesByLocale, type Locale } from "./locales";

interface AppIntlProviderProps {
  locale?: Locale;
  children: ReactNode;
}

export function AppIntlProvider({
  locale = defaultLocale,
  children,
}: AppIntlProviderProps) {
  return (
    <IntlProvider
      locale={locale}
      defaultLocale={defaultLocale}
      messages={messagesByLocale[locale]}
    >
      {children}
    </IntlProvider>
  );
}
