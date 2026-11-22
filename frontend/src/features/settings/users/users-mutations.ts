import {
  disableUser,
  enableUser,
  inviteUser,
  replaceUserRoles,
  type InviteUserRequest,
  type UserDetail,
} from "@alliedpayroll/api-client";
import { useMutation, useQueryClient } from "@tanstack/react-query";

import { problemFrom } from "@/shared/data/problem";
import type { ApiError } from "@/shared/data/session";

import { usersKeys } from "./users-query";

function useUserMutation<Variables>(
  run: (variables: Variables) => Promise<UserDetail>,
) {
  const queryClient = useQueryClient();
  return useMutation<UserDetail, ApiError, Variables>({
    mutationFn: run,
    onSuccess: async (user) => {
      queryClient.setQueryData(usersKeys.detail(user.id), user);
      await queryClient.invalidateQueries({ queryKey: usersKeys.all });
    },
  });
}

export function useInviteUser() {
  return useUserMutation(async (body: InviteUserRequest) => {
    const { data, error, response } = await inviteUser({ body });
    if (data === undefined) throw problemFrom(error, response, "/api/v1/users");
    return data;
  });
}

/** Sends the version the screen showed, so a change made meanwhile is refused, not overwritten. */
export function useReplaceUserRoles() {
  return useUserMutation(
    async ({
      id,
      version,
      roleIds,
    }: {
      id: string;
      version: number;
      roleIds: string[];
    }) => {
      const { data, error, response } = await replaceUserRoles({
        path: { id },
        headers: { "If-Match": `"${String(version)}"` },
        body: { roleIds },
      });
      if (data === undefined) {
        throw problemFrom(error, response, `/api/v1/users/${id}/roles`);
      }
      return data;
    },
  );
}

export function useSetUserAccess() {
  return useUserMutation(
    async ({ id, enabled }: { id: string; enabled: boolean }) => {
      const call = enabled ? enableUser : disableUser;
      const { data, error, response } = await call({ path: { id } });
      if (data === undefined) {
        throw problemFrom(error, response, `/api/v1/users/${id}`);
      }
      return data;
    },
  );
}
