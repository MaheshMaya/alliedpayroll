import type { ComponentProps } from "react";

import { cn } from "./cn";

/** Two letters from a Latin-script name; the first two characters of the id when there is none. */
export function initialsOf(name: string | undefined, fallback: string): string {
  const words = (name ?? "").trim().split(/\s+/).filter(Boolean);
  if (words.length >= 2) {
    return `${words[0]?.charAt(0) ?? ""}${words[1]?.charAt(0) ?? ""}`.toUpperCase();
  }
  const source = words[0] ?? fallback;
  return source.slice(0, 2).toUpperCase();
}

type AvatarProps = ComponentProps<"span"> & {
  name: string | undefined;
  fallback: string;
};

export function Avatar({ name, fallback, className, ...props }: AvatarProps) {
  return (
    <span
      data-slot="avatar"
      aria-hidden="true"
      className={cn(
        "inline-flex size-8 shrink-0 items-center justify-center rounded-full bg-bg-selected text-bodyStrong text-text-link",
        className,
      )}
      {...props}
    >
      {initialsOf(name, fallback)}
    </span>
  );
}
