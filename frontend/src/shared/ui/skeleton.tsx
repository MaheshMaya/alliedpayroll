import type { ComponentProps } from "react";

import { cn } from "./cn";

/** A content-shaped placeholder while data loads; sized by the caller to match what replaces it. */
export function Skeleton({ className, ...props }: ComponentProps<"div">) {
  return (
    <div
      aria-hidden="true"
      className={cn(
        "animate-pulse rounded-sm bg-bg-subtle motion-reduce:animate-none",
        className,
      )}
      {...props}
    />
  );
}
