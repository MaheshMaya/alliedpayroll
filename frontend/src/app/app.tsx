import { QueryClientProvider } from "@tanstack/react-query";
import { RouterProvider } from "@tanstack/react-router";

import { AppIntlProvider } from "@/shared/i18n/app-intl-provider";

import { queryClient } from "./query-client";
import { router } from "./router";

export function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <AppIntlProvider>
        <RouterProvider router={router} />
      </AppIntlProvider>
    </QueryClientProvider>
  );
}
