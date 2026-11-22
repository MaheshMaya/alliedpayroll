import type { ComponentProps } from "react";

import { cn } from "./cn";

// Tables use the compact density tokens (ui-design-system): row height and cell padding come from
// --table-rowHeight and --table-cellPaddingX, never from ad-hoc spacing.
export function Table({ className, ...props }: ComponentProps<"table">) {
  return (
    <div className="w-full overflow-x-auto rounded-lg border border-border-default bg-bg-surface">
      <table
        data-slot="table"
        className={cn(
          "w-full border-collapse text-body text-text-default",
          className,
        )}
        {...props}
      />
    </div>
  );
}

export function TableHead({ className, ...props }: ComponentProps<"thead">) {
  return (
    <thead
      className={cn("bg-bg-subtle text-caption text-text-muted", className)}
      {...props}
    />
  );
}

export function TableBody({ className, ...props }: ComponentProps<"tbody">) {
  return (
    <tbody
      className={cn("divide-y divide-border-divider", className)}
      {...props}
    />
  );
}

export function TableRow({ className, ...props }: ComponentProps<"tr">) {
  return (
    <tr
      className={cn("h-(--table-rowHeight) hover:bg-bg-subtle", className)}
      {...props}
    />
  );
}

export function TableHeaderCell({ className, ...props }: ComponentProps<"th">) {
  return (
    <th
      scope="col"
      className={cn(
        "h-10 px-(--table-cellPaddingX) text-left font-medium whitespace-nowrap",
        className,
      )}
      {...props}
    />
  );
}

export function TableCell({ className, ...props }: ComponentProps<"td">) {
  return (
    <td
      className={cn("px-(--table-cellPaddingX) py-2 align-middle", className)}
      {...props}
    />
  );
}
