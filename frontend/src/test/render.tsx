import {
  render,
  type RenderOptions,
  type RenderResult,
} from "@testing-library/react";
import type { ReactElement, ReactNode } from "react";

import { AppIntlProvider } from "@/shared/i18n/app-intl-provider";

function Providers({ children }: { children: ReactNode }) {
  return <AppIntlProvider>{children}</AppIntlProvider>;
}

export function renderWithProviders(
  ui: ReactElement,
  options?: Omit<RenderOptions, "wrapper">,
): RenderResult {
  return render(ui, { wrapper: Providers, ...options });
}
