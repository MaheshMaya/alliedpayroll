import { QueryClient } from "@tanstack/react-query";

// Lists stay fresh for 30 s (react-web skill §4); resources override per query key.
export const queryClient = new QueryClient({
  defaultOptions: {
    queries: { staleTime: 30_000, retry: 1, refetchOnWindowFocus: false },
  },
});
