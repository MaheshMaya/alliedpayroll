import { X } from "lucide-react";
import { Dialog as RadixDialog } from "radix-ui";
import type { ReactNode } from "react";

import { cn } from "./cn";

interface DialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  title: ReactNode;
  /** One sentence under the title: what this dialog does or what will happen. */
  description: ReactNode;
  closeLabel: string;
  children: ReactNode;
  className?: string;
}

/**
 * A centred modal for a short task or a confirmation (ux-patterns: forms, irreversible actions).
 * Radix gives the focus trap, Escape and the overlay; the content scrolls when it is taller than
 * the viewport.
 */
export function Dialog({
  open,
  onOpenChange,
  title,
  description,
  closeLabel,
  children,
  className,
}: DialogProps) {
  return (
    <RadixDialog.Root open={open} onOpenChange={onOpenChange}>
      <RadixDialog.Portal>
        {/* Above the sheet (z-50), so a confirmation from a sheet dims the sheet too. */}
        <RadixDialog.Overlay className="fixed inset-0 z-60 bg-bg-overlay" />
        <RadixDialog.Content
          className={cn(
            "fixed top-1/2 left-1/2 z-70 flex max-h-[calc(100dvh-2rem)] w-[calc(100vw-2rem)] max-w-[520px] -translate-x-1/2 -translate-y-1/2 flex-col gap-5 overflow-y-auto rounded-lg border border-border-default bg-bg-surface p-6 shadow-lg",
            className,
          )}
        >
          <div className="flex items-start justify-between gap-4">
            <div className="flex flex-col gap-1">
              <RadixDialog.Title className="text-h2 text-text-default">
                {title}
              </RadixDialog.Title>
              <RadixDialog.Description className="text-body text-text-secondary">
                {description}
              </RadixDialog.Description>
            </div>
            <RadixDialog.Close
              aria-label={closeLabel}
              className="flex size-(--control-height-sm) shrink-0 cursor-pointer items-center justify-center rounded-md text-text-muted hover:bg-bg-subtle hover:text-text-default focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-border-focus"
            >
              <X aria-hidden="true" className="size-4" />
            </RadixDialog.Close>
          </div>
          {children}
        </RadixDialog.Content>
      </RadixDialog.Portal>
    </RadixDialog.Root>
  );
}
