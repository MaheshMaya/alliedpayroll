import { defineConfig } from "@hey-api/openapi-ts";

// Input is exported by the backend's OpenApiExportTest; output is compiled by `pnpm build`.
export default defineConfig({
  input: "../../openapi/backend.json",
  output: { path: "src", postProcess: ["prettier"] },
  plugins: [
    "@hey-api/client-fetch",
    "@hey-api/typescript",
    "@hey-api/sdk",
    "@tanstack/react-query",
  ],
});
