import type { InputHTMLAttributes, ReactNode } from "react";

import { cn } from "./cn";

interface CheckboxProps extends Omit<
  InputHTMLAttributes<HTMLInputElement>,
  "type"
> {
  label: ReactNode;
  /** A second line under the label, such as a code or a consequence. */
  hint?: ReactNode;
}

/**
 * A native checkbox with its label as the hit area. Native keeps keyboard, form and screen-reader
 * behaviour for free; the accent colour comes from the token.
 */
export function Checkbox({
  label,
  hint,
  className,
  disabled,
  ...props
}: CheckboxProps) {
  return (
    <label
      className={cn(
        "flex min-h-(--control-height-sm) items-start gap-3 rounded-md py-1.5",
        disabled ? "cursor-not-allowed opacity-60" : "cursor-pointer",
        className,
      )}
    >
      <input
        type="checkbox"
        disabled={disabled}
        className="mt-0.5 size-4 shrink-0 accent-(--accent-default) focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-border-focus"
        {...props}
      />
      <span className="flex min-w-0 flex-col">
        <span className="text-body text-text-default">{label}</span>
        {hint !== undefined && (
          <span className="text-caption text-text-muted">{hint}</span>
        )}
      </span>
    </label>
  );
}
