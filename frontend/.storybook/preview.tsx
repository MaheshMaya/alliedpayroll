import type { Decorator, Preview } from "@storybook/react-vite";
import { useEffect, type ReactNode } from "react";

import { AppIntlProvider } from "../src/shared/i18n/app-intl-provider";
import { isLocale } from "../src/shared/i18n/locales";

import "../src/app/styles.css";

const asString = (value: unknown): string =>
  typeof value === "string" ? value : "";

// Mode attributes live on <html> so stories render exactly as the app does, including portals.
function TokenModes({
  theme,
  density,
  children,
}: {
  theme: string;
  density: string;
  children: ReactNode;
}) {
  useEffect(() => {
    const html = document.documentElement;
    html.dataset.theme = theme;
    html.dataset.density = density;
  }, [theme, density]);
  return children;
}

const withTokenModes: Decorator = (Story, { globals }) => (
  <TokenModes
    theme={asString(globals.theme)}
    density={asString(globals.density)}
  >
    <Story />
  </TokenModes>
);

const withIntl: Decorator = (Story, { globals }) => {
  const locale = asString(globals.locale);
  return (
    <AppIntlProvider locale={isLocale(locale) ? locale : "en-IN"}>
      <Story />
    </AppIntlProvider>
  );
};

const preview: Preview = {
  globalTypes: {
    theme: {
      description: "Colour mode",
      toolbar: { icon: "mirror", items: ["light", "dark"], dynamicTitle: true },
    },
    density: {
      description: "Control density",
      toolbar: {
        icon: "component",
        items: ["comfortable", "compact"],
        dynamicTitle: true,
      },
    },
    locale: {
      description: "Locale",
      toolbar: {
        icon: "globe",
        items: ["en-IN", "hi", "ta", "qps-ploc"],
        dynamicTitle: true,
      },
    },
  },
  initialGlobals: { theme: "light", density: "comfortable", locale: "en-IN" },
  decorators: [withTokenModes, withIntl],
  parameters: {
    a11y: { test: "error" },
    controls: { matchers: { color: /(background|color)$/i, date: /Date$/i } },
  },
};

export default preview;
