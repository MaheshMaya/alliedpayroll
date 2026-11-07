import themeKeys from "@alliedpayroll/design-tokens/theme-keys.json";
import { clsx, type ClassValue } from "clsx";
import { extendTailwindMerge } from "tailwind-merge";

// tailwind-merge only knows the default scales; the token names tell it that text-h1 is a size and text-text-muted a colour.
const twMerge = extendTailwindMerge({ extend: { theme: themeKeys } });

export function cn(...inputs: ClassValue[]): string {
  return twMerge(clsx(inputs));
}
