import { Switch as RadixSwitch } from "radix-ui";
import type { ComponentProps } from "react";

import { cn } from "./cn";

/**
 * An on/off control for a setting that applies as soon as it changes. Radix gives the switch
 * role and keyboard; the caller names it with `aria-label` or `aria-labelledby` and shows the
 * state in words beside it, so colour is never the only signal.
 */
export function Switch({
  className,
  ...props
}: ComponentProps<typeof RadixSwitch.Root>) {
  return (
    <RadixSwitch.Root
      className={cn(
        "inline-flex h-6 w-10 shrink-0 cursor-pointer items-center rounded-full bg-border-strong p-0.5 transition-colors",
        "data-[state=checked]:bg-(--accent-default)",
        "focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-border-focus",
        "disabled:cursor-not-allowed disabled:opacity-60",
        className,
      )}
      {...props}
    >
      <RadixSwitch.Thumb className="block size-5 rounded-full bg-bg-surface shadow-sm transition-transform data-[state=checked]:translate-x-4" />
    </RadixSwitch.Root>
  );
}
