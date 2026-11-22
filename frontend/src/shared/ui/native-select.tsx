import { ChevronDown } from "lucide-react";
import type { ComponentProps } from "react";

import { cn } from "./cn";

/** The browser's own select, styled with the control tokens; enough for short fixed lists. */
export function NativeSelect({
  className,
  children,
  ...props
}: ComponentProps<"select">) {
  return (
    <span className="relative inline-flex">
      <select
        data-slot="select"
        className={cn(
          "h-(--control-height-md) min-w-0 cursor-pointer appearance-none rounded-md border border-border-input bg-bg-surface pr-10 pl-3 text-body text-text-default",
          "focus-visible:border-border-focus focus-visible:outline-2 focus-visible:outline-offset-0 focus-visible:outline-border-focus",
          "disabled:cursor-not-allowed disabled:bg-bg-subtle disabled:text-text-muted",
          className,
        )}
        {...props}
      >
        {children}
      </select>
      <ChevronDown
        aria-hidden="true"
        className="pointer-events-none absolute top-1/2 right-3 size-4 -translate-y-1/2 text-text-muted"
      />
    </span>
  );
}
