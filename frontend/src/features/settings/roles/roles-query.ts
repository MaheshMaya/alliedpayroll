import {
  createRole,
  listPermissions,
  listRoles,
  replaceRolePermissions,
  type CreateRoleRequest,
  type PermissionDefinition,
  type RoleView,
} from "@alliedpayroll/api-client";
import {
  queryOptions,
  useMutation,
  useQueryClient,
} from "@tanstack/react-query";

import { problemFrom } from "@/shared/data/problem";
import { ApiError } from "@/shared/data/session";

export const rolesKeys = {
  all: ["roles"] as const,
  permissions: ["permissions"] as const,
};

export const rolesOptions = queryOptions<RoleView[], ApiError>({
  queryKey: rolesKeys.all,
  queryFn: async ({ signal }) => {
    const { data, response } = await listRoles({ signal });
    if (data === undefined) {
      throw new ApiError(response?.status ?? 0, "/api/v1/roles");
    }
    return data;
  },
  staleTime: 60_000,
});

export const permissionsOptions = queryOptions<
  PermissionDefinition[],
  ApiError
>({
  queryKey: rolesKeys.permissions,
  queryFn: async ({ signal }) => {
    const { data, response } = await listPermissions({ signal });
    if (data === undefined) {
      throw new ApiError(response?.status ?? 0, "/api/v1/permissions");
    }
    return data;
  },
  staleTime: 10 * 60_000,
});

/** The catalogue grouped by module, modules and codes in a stable order. */
export function byModule(
  permissions: readonly PermissionDefinition[],
): [string, PermissionDefinition[]][] {
  const groups = new Map<string, PermissionDefinition[]>();
  for (const permission of permissions) {
    const group = groups.get(permission.moduleCode) ?? [];
    group.push(permission);
    groups.set(permission.moduleCode, group);
  }
  return [...groups.entries()]
    .sort(([a], [b]) => a.localeCompare(b))
    .map(([module, entries]) => [
      module,
      entries.sort((a, b) => a.code.localeCompare(b.code)),
    ]);
}

export function useCreateRole() {
  const queryClient = useQueryClient();
  return useMutation<RoleView, ApiError, CreateRoleRequest>({
    mutationFn: async (body) => {
      const { data, error, response } = await createRole({ body });
      if (data === undefined)
        throw problemFrom(error, response, "/api/v1/roles");
      return data;
    },
    onSuccess: () => queryClient.invalidateQueries({ queryKey: rolesKeys.all }),
  });
}

export function useReplaceRolePermissions() {
  const queryClient = useQueryClient();
  return useMutation<RoleView, ApiError, { id: string; permissions: string[] }>(
    {
      mutationFn: async ({ id, permissions }) => {
        const { data, error, response } = await replaceRolePermissions({
          path: { id },
          body: { permissions },
        });
        if (data === undefined) {
          throw problemFrom(error, response, `/api/v1/roles/${id}/permissions`);
        }
        return data;
      },
      onSuccess: async () => {
        await queryClient.invalidateQueries({ queryKey: rolesKeys.all });
        // A role change moves the permissions shown on every user who holds it.
        await queryClient.invalidateQueries({ queryKey: ["users"] });
      },
    },
  );
}
