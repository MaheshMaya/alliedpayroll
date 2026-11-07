import type { ComponentProps } from "react";

import { cn } from "./cn";

export function Input({
  className,
  type = "text",
  ...props
}: ComponentProps<"input">) {
  return (
    <input
      type={type}
      data-slot="input"
      className={cn(
        "h-(--control-height-md) w-full min-w-0 rounded-md border border-border-input bg-bg-surface px-3 text-body text-text-default",
        "placeholder:text-text-muted selection:bg-bg-selected",
        "transition-colors file:border-0 file:bg-transparent file:text-bodyStrong file:text-text-default",
        "focus-visible:border-border-focus focus-visible:outline-2 focus-visible:outline-offset-0 focus-visible:outline-border-focus",
        "aria-invalid:border-status-danger-solid aria-invalid:focus-visible:outline-status-danger-solid",
        "disabled:cursor-not-allowed disabled:bg-bg-subtle disabled:text-text-muted",
        className,
      )}
      {...props}
    />
  );
}
