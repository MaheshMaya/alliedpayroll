import { cva, type VariantProps } from "class-variance-authority";
import { Slot } from "radix-ui";
import type { ComponentProps } from "react";

import { cn } from "./cn";

const buttonVariants = cva(
  [
    "inline-flex shrink-0 items-center justify-center gap-2 whitespace-nowrap rounded-md text-bodyStrong",
    "transition-colors duration-(--duration-fast) ease-standard",
    "focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-border-focus",
    "disabled:pointer-events-none disabled:opacity-50",
    "[&_svg]:pointer-events-none [&_svg]:size-4 [&_svg]:shrink-0",
  ],
  {
    variants: {
      variant: {
        primary:
          "bg-accent-default text-accent-on hover:bg-accent-hover active:bg-accent-hover",
        secondary:
          "border border-border-input bg-bg-surface text-text-default hover:bg-bg-subtle active:bg-bg-selected",
        ghost: "text-text-default hover:bg-bg-subtle active:bg-bg-selected",
        // Danger has no hover token; fg is the darker step of the same ramp, as accent.hover is for accent.
        danger:
          "bg-status-danger-solid text-text-inverse hover:bg-status-danger-fg active:bg-status-danger-fg",
      },
      size: {
        sm: "h-(--control-height-sm) px-3 text-small",
        md: "h-(--control-height-md) px-(--control-paddingX)",
        lg: "h-(--control-height-lg) px-(--control-paddingX) text-bodyLg",
        icon: "size-(--control-height-md)",
      },
    },
    defaultVariants: { variant: "primary", size: "md" },
  },
);

type ButtonProps = ComponentProps<"button"> &
  VariantProps<typeof buttonVariants> & {
    /** Renders the child element (for example a router link) with the button styles instead of a <button>. */
    asChild?: boolean;
  };

export function Button({
  className,
  variant,
  size,
  asChild = false,
  type = "button",
  ...props
}: ButtonProps) {
  const Component = asChild ? Slot.Root : "button";
  return (
    <Component
      data-slot="button"
      className={cn(buttonVariants({ variant, size }), className)}
      {...(asChild ? {} : { type })}
      {...props}
    />
  );
}

export { buttonVariants };
