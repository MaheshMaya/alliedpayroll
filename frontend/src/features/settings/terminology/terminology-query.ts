import {
  getTerminology,
  relabelTerminology,
  type TerminologyView,
} from "@alliedpayroll/api-client";
import {
  queryOptions,
  useMutation,
  useQueryClient,
} from "@tanstack/react-query";

import { problemFrom } from "@/shared/data/problem";
import { ApiError } from "@/shared/data/session";

export const terminologyKeys = {
  locale: (locale: string) => ["terminology", locale] as const,
};

export function terminologyOptions(locale: string) {
  return queryOptions<TerminologyView, ApiError>({
    queryKey: terminologyKeys.locale(locale),
    queryFn: async ({ signal }) => {
      const { data, response } = await getTerminology({
        path: { locale },
        signal,
      });
      if (data === undefined) {
        throw new ApiError(
          response?.status ?? 0,
          `/api/v1/terminology/${locale}`,
        );
      }
      return data;
    },
    staleTime: 30_000,
  });
}

/**
 * Saves the changed labels of one locale; an empty label goes back to the default. The
 * bootstrap is refetched so the navigation and every screen use the new words.
 */
export function useRelabel() {
  const queryClient = useQueryClient();
  return useMutation<
    TerminologyView,
    ApiError,
    { locale: string; labels: Record<string, string> }
  >({
    mutationFn: async ({ locale, labels }) => {
      const { data, error, response } = await relabelTerminology({
        path: { locale },
        body: { labels },
      });
      if (data === undefined) {
        throw problemFrom(error, response, `/api/v1/terminology/${locale}`);
      }
      return data;
    },
    onSuccess: async (view) => {
      queryClient.setQueryData(terminologyKeys.locale(view.locale), view);
      await queryClient.invalidateQueries({ queryKey: ["bootstrap"] });
    },
  });
}
