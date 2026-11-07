import { readdirSync } from "node:fs";

import js from "@eslint/js";
import eslintReact from "@eslint-react/eslint-plugin";
import type { Linter } from "eslint";
import { defineConfig, globalIgnores } from "eslint/config";
import prettier from "eslint-config-prettier";
import { createTypeScriptImportResolver } from "eslint-import-resolver-typescript";
import { flatConfigs as importX } from "eslint-plugin-import-x";
import jsxA11y from "eslint-plugin-jsx-a11y";
import reactHooks from "eslint-plugin-react-hooks";
import { configs as storybook } from "eslint-plugin-storybook";
import globals from "globals";
import { configs as tseslint } from "typescript-eslint";

const root = import.meta.dirname;

// eslint-plugin-storybook 10.6 still types its rules against the ESLint 9 RuleContext, so ESLint 10's
// config types reject it at compile time even though the plugin runs fine.
const storybookConfigs = storybook[
  "flat/recommended"
] as unknown as Linter.Config[];

const featureNames = readdirSync(`${root}/src/features`, {
  withFileTypes: true,
})
  .filter((entry) => entry.isDirectory())
  .map((entry) => entry.name);

// A feature may import shared/* and its own folder, nothing else (react-web skill §1).
const featureZones = featureNames.map((name) => ({
  target: `./src/features/${name}`,
  from: "./src/features",
  except: [`./${name}`],
  message: `features/${name} may only import from shared/* or its own folder.`,
}));

export default defineConfig(
  globalIgnores([
    "dist",
    "storybook-static",
    "coverage",
    "playwright-report",
    "test-results",
    "src/route-tree.gen.ts",
  ]),
  js.configs.recommended,
  tseslint.strictTypeChecked,
  tseslint.stylisticTypeChecked,
  importX.recommended,
  importX.typescript,
  {
    languageOptions: {
      parserOptions: { projectService: true, tsconfigRootDir: root },
      globals: { ...globals.browser, ...globals.node },
    },
    settings: {
      "import-x/resolver-next": [
        createTypeScriptImportResolver({
          project: ["tsconfig.app.json", "tsconfig.node.json"],
          noWarnOnMultipleProjects: true,
        }),
      ],
    },
    rules: {
      "import-x/order": [
        "error",
        {
          groups: [
            "builtin",
            "external",
            "internal",
            "parent",
            "sibling",
            "index",
          ],
          "newlines-between": "always",
          alphabetize: { order: "asc", caseInsensitive: true },
        },
      ],
      "import-x/no-restricted-paths": [
        "error",
        {
          basePath: root,
          zones: [
            ...featureZones,
            {
              target: "./src/features",
              from: ["./src/app", "./src/routes"],
              message:
                "Features do not import the app layer; the app layer composes features.",
            },
            {
              target: "./src/shared",
              from: ["./src/features", "./src/app", "./src/routes"],
              message:
                "shared/* is the bottom layer and imports nothing above it.",
            },
          ],
        },
      ],
    },
  },
  {
    files: ["**/*.{ts,tsx}"],
    extends: [eslintReact.configs["recommended-typescript"]],
  },
  reactHooks.configs.flat.recommended,
  jsxA11y.flatConfigs.recommended,
  {
    files: ["src/features/**", "src/routes/**", "src/app/**"],
    rules: {
      "no-restricted-globals": [
        "error",
        {
          name: "fetch",
          message: "Call the API through the generated SDK in shared/data.",
        },
      ],
    },
  },
  storybookConfigs,
  prettier,
);
