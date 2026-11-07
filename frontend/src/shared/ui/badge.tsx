import { cva, type VariantProps } from "class-variance-authority";
import type { ComponentProps } from "react";

import { cn } from "./cn";

// Status colours are fixed tokens, never brandable (ui-design-system). The dot repeats the status in the
// solid shade so the badge still reads when bg and text tints are close.
const badgeVariants = cva(
  "inline-flex h-5 shrink-0 items-center gap-1.5 whitespace-nowrap rounded-sm px-2 text-caption font-medium [&>svg]:size-3",
  {
    variants: {
      status: {
        neutral:
          "bg-status-neutral-bg text-status-neutral-fg [&>[data-dot]]:bg-status-neutral-solid",
        success:
          "bg-status-success-bg text-status-success-fg [&>[data-dot]]:bg-status-success-solid",
        warning:
          "bg-status-warning-bg text-status-warning-fg [&>[data-dot]]:bg-status-warning-solid",
        danger:
          "bg-status-danger-bg text-status-danger-fg [&>[data-dot]]:bg-status-danger-solid",
        info: "bg-status-info-bg text-status-info-fg [&>[data-dot]]:bg-status-info-solid",
      },
    },
    defaultVariants: { status: "neutral" },
  },
);

type BadgeProps = ComponentProps<"span"> &
  VariantProps<typeof badgeVariants> & {
    /** Shows the status dot in front of the label. */
    dot?: boolean;
  };

export function Badge({
  className,
  status,
  dot = false,
  children,
  ...props
}: BadgeProps) {
  return (
    <span
      data-slot="badge"
      className={cn(badgeVariants({ status }), className)}
      {...props}
    >
      {dot && (
        <span data-dot aria-hidden="true" className="size-1.5 rounded-full" />
      )}
      {children}
    </span>
  );
}

export { badgeVariants };
