import { X } from "lucide-react";
import { Dialog } from "radix-ui";
import type { ReactNode } from "react";

import { cn } from "./cn";

interface SheetProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  title: ReactNode;
  /** Read by assistive technology; visually hidden. */
  description: string;
  closeLabel: string;
  children: ReactNode;
  className?: string;
}

/**
 * A right-hand panel for a quick look at one record (ux-patterns, list page). Radix Dialog gives
 * the focus trap, Escape and the overlay; the panel is 420px, the Figma drawer width.
 */
export function Sheet({
  open,
  onOpenChange,
  title,
  description,
  closeLabel,
  children,
  className,
}: SheetProps) {
  return (
    <Dialog.Root open={open} onOpenChange={onOpenChange}>
      <Dialog.Portal>
        <Dialog.Overlay className="fixed inset-0 z-40 bg-bg-overlay" />
        <Dialog.Content
          className={cn(
            "fixed inset-y-0 right-0 z-50 flex w-full max-w-[420px] flex-col gap-6 overflow-y-auto border-l border-border-default bg-bg-surface p-6 shadow-lg",
            "duration-(--duration-base) ease-standard data-[state=open]:animate-in data-[state=open]:slide-in-from-right motion-reduce:animate-none",
            className,
          )}
        >
          <div className="flex items-start justify-between gap-4">
            <Dialog.Title className="text-h2 text-text-default">
              {title}
            </Dialog.Title>
            <Dialog.Close
              aria-label={closeLabel}
              className="flex size-(--control-height-sm) shrink-0 cursor-pointer items-center justify-center rounded-md text-text-muted hover:bg-bg-subtle hover:text-text-default focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-border-focus"
            >
              <X aria-hidden="true" className="size-4" />
            </Dialog.Close>
          </div>
          <Dialog.Description className="sr-only">
            {description}
          </Dialog.Description>
          {children}
        </Dialog.Content>
      </Dialog.Portal>
    </Dialog.Root>
  );
}
