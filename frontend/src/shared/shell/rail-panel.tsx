import { X } from "lucide-react";
import { useEffect, useRef, type ReactNode } from "react";
import { useIntl } from "react-intl";

interface RailPanelProps {
  title: string;
  onClose: () => void;
  children: ReactNode;
}

/** 360px overlay beside the page; takes focus on open, Esc or the close button returns it. */
export function RailPanel({ title, onClose, children }: RailPanelProps) {
  const intl = useIntl();
  const closeRef = useRef<HTMLButtonElement>(null);

  useEffect(() => {
    const previous = document.activeElement;
    closeRef.current?.focus();
    const onKey = (event: KeyboardEvent) => {
      if (event.key === "Escape") onClose();
    };
    document.addEventListener("keydown", onKey);
    return () => {
      document.removeEventListener("keydown", onKey);
      if (previous instanceof HTMLElement) previous.focus();
    };
  }, [onClose]);

  return (
    <section
      role="dialog"
      aria-modal="false"
      aria-label={title}
      className="flex h-full w-[360px] shrink-0 flex-col border-l border-border-divider bg-bg-surface shadow-lg"
    >
      <header className="flex items-center gap-3 border-b border-border-divider px-5 py-3">
        <h2 className="min-w-0 flex-1 truncate text-h3 text-text-default">
          {title}
        </h2>
        <button
          ref={closeRef}
          type="button"
          onClick={onClose}
          aria-label={intl.formatMessage({ id: "shell.panel.close" })}
          className="flex size-8 items-center justify-center rounded-md text-text-secondary hover:bg-bg-subtle"
        >
          <X aria-hidden="true" className="size-4" />
        </button>
      </header>
      <div className="flex flex-col gap-4 px-5 py-4">{children}</div>
    </section>
  );
}
