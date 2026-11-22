import {
  listModules,
  setModule,
  type ModuleView,
} from "@alliedpayroll/api-client";
import {
  queryOptions,
  useMutation,
  useQueryClient,
} from "@tanstack/react-query";

import { problemFrom } from "@/shared/data/problem";
import { ApiError } from "@/shared/data/session";

export const modulesKeys = { all: ["modules"] as const };

export const modulesOptions = queryOptions<ModuleView[], ApiError>({
  queryKey: modulesKeys.all,
  queryFn: async ({ signal }) => {
    const { data, response } = await listModules({ signal });
    if (data === undefined) {
      throw new ApiError(response?.status ?? 0, "/api/v1/modules");
    }
    return data;
  },
  staleTime: 30_000,
});

/** Switches a module; the bootstrap is refetched so the navigation follows. */
export function useSetModule() {
  const queryClient = useQueryClient();
  return useMutation<ModuleView, ApiError, { code: string; enabled: boolean }>({
    mutationFn: async ({ code, enabled }) => {
      const { data, error, response } = await setModule({
        path: { code },
        body: { enabled },
      });
      if (data === undefined) {
        throw problemFrom(error, response, `/api/v1/modules/${code}`);
      }
      return data;
    },
    onSuccess: async (module) => {
      queryClient.setQueryData<ModuleView[]>(modulesKeys.all, (current) =>
        current?.map((entry) => (entry.code === module.code ? module : entry)),
      );
      await queryClient.invalidateQueries({ queryKey: ["bootstrap"] });
    },
  });
}
