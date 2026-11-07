import flatTokens from "@alliedpayroll/design-tokens/tokens.flat.json";
import type { Meta, StoryObj } from "@storybook/react-vite";
import { z } from "zod";

const tokenSchema = z.object({
  path: z.string(),
  collection: z.string(),
  type: z.string(),
  value: z.union([z.string(), z.number()]),
  description: z.string().optional(),
  modes: z.record(z.string(), z.union([z.string(), z.number()])).optional(),
});

const tokens = Object.entries(
  z.record(z.string(), tokenSchema).parse(flatTokens),
).map(([name, token]) => ({
  name,
  ...token,
}));

type Token = (typeof tokens)[number];

const byPrefix = (prefix: string) =>
  tokens.filter((token) => token.path.startsWith(prefix));

function groupBy(list: Token[], depth: number): Map<string, Token[]> {
  const groups = new Map<string, Token[]>();
  for (const token of list) {
    const key = token.path.split("/").slice(0, depth).join("/");
    groups.set(key, [...(groups.get(key) ?? []), token]);
  }
  return groups;
}

function Swatch({ token }: { token: Token }) {
  return (
    <li className="flex items-center gap-3">
      <span
        aria-hidden="true"
        className="size-10 shrink-0 rounded-sm border border-border-default"
        style={{ backgroundColor: `var(--${token.name})` }}
      />
      <span className="flex min-w-0 flex-col">
        <code className="text-code text-text-default">--{token.name}</code>
        <span className="text-caption text-text-muted">
          {String(token.value)}
          {token.modes?.dark !== undefined &&
            ` · dark ${String(token.modes.dark)}`}
        </span>
      </span>
    </li>
  );
}

function ColourScales() {
  const groups = [
    ...groupBy([...byPrefix("color/"), ...byPrefix("accent/")], 2),
  ];
  return (
    <div className="flex flex-col gap-8">
      {groups.map(([group, list]) => (
        <section key={group} aria-labelledby={`scale-${group}`}>
          <h2 id={`scale-${group}`} className="mb-3 text-h3 text-text-default">
            {group}
          </h2>
          <ul className="grid grid-cols-1 gap-3 md:grid-cols-2 xl:grid-cols-3">
            {list.map((token) => (
              <Swatch key={token.name} token={token} />
            ))}
          </ul>
        </section>
      ))}
    </div>
  );
}

function SpacingScale() {
  return (
    <ul className="flex flex-col gap-2">
      {byPrefix("space/").map((token) => (
        <li key={token.name} className="flex items-center gap-4">
          <code className="w-24 text-code text-text-default">
            --{token.name}
          </code>
          <span className="w-12 text-caption text-text-muted">
            {String(token.value)}
          </span>
          <span
            aria-hidden="true"
            className="h-4 bg-accent-default"
            style={{ width: `var(--${token.name})` }}
          />
        </li>
      ))}
    </ul>
  );
}

const meta = { title: "Foundations/Tokens" } satisfies Meta;

export default meta;

export const Colour: StoryObj = { render: () => <ColourScales /> };

export const Spacing: StoryObj = { render: () => <SpacingScale /> };
