import { useEffect, useState, type ReactNode } from "react";

import { ContextBar } from "./context-bar";
import { SideNav } from "./side-nav";
import { UtilityRail } from "./utility-rail";

const COLLAPSED_KEY = "shell.navCollapsed";

function readCollapsed(): boolean {
  try {
    return localStorage.getItem(COLLAPSED_KEY) === "true";
  } catch {
    return false;
  }
}

function isTyping(target: EventTarget | null): boolean {
  return (
    target instanceof HTMLElement &&
    (target.isContentEditable ||
      ["INPUT", "TEXTAREA", "SELECT"].includes(target.tagName))
  );
}

/** App Shell v2: side nav, context bar over the page, utility rail. "[" collapses the nav. */
export function AppShell({ children }: { children: ReactNode }) {
  const [collapsed, setCollapsed] = useState(readCollapsed);

  useEffect(() => {
    try {
      localStorage.setItem(COLLAPSED_KEY, String(collapsed));
    } catch {
      // A blocked storage only loses the preference for this visit.
    }
  }, [collapsed]);

  useEffect(() => {
    const onKey = (event: KeyboardEvent) => {
      if (
        event.key === "[" &&
        !event.ctrlKey &&
        !event.metaKey &&
        !event.altKey &&
        !isTyping(event.target)
      ) {
        event.preventDefault();
        setCollapsed((value) => !value);
      }
    };
    document.addEventListener("keydown", onKey);
    return () => {
      document.removeEventListener("keydown", onKey);
    };
  }, []);

  return (
    <div className="flex h-svh w-full overflow-hidden bg-bg-canvas">
      <SideNav
        collapsed={collapsed}
        onToggle={() => {
          setCollapsed((value) => !value);
        }}
      />
      <div className="flex min-w-0 flex-1 flex-col">
        <ContextBar />
        <main id="main" className="min-h-0 flex-1 overflow-y-auto px-8 py-6">
          {children}
        </main>
      </div>
      <UtilityRail />
    </div>
  );
}
