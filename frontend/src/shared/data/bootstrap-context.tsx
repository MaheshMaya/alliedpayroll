import type { Bootstrap } from "@alliedpayroll/api-client";
import { createContext, use, type ReactNode } from "react";

const BootstrapContext = createContext<Bootstrap | null>(null);

export function BootstrapProvider({
  value,
  children,
}: {
  value: Bootstrap;
  children: ReactNode;
}) {
  return <BootstrapContext value={value}>{children}</BootstrapContext>;
}

/** Tenant, user, permissions and module flags of the signed-in session. */
export function useBootstrap(): Bootstrap {
  const value = use(BootstrapContext);
  if (value === null) {
    throw new Error("useBootstrap must be used inside BootstrapProvider");
  }
  return value;
}
