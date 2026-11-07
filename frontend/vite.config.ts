import { fileURLToPath } from "node:url";

import tailwindcss from "@tailwindcss/vite";
import { tanstackRouter } from "@tanstack/router-plugin/vite";
import react from "@vitejs/plugin-react";
import { defineConfig } from "vite";

export default defineConfig({
  plugins: [
    // The router plugin must run before React so the generated route tree exists when JSX is transformed.
    tanstackRouter({
      target: "react",
      autoCodeSplitting: true,
      routesDirectory: "./src/routes",
      generatedRouteTree: "./src/route-tree.gen.ts",
    }),
    react(),
    tailwindcss(),
  ],
  resolve: {
    alias: { "@": fileURLToPath(new URL("./src", import.meta.url)) },
  },
  server: {
    port: 5173,
    // Same-origin API in development: the tenant host header must reach the backend unchanged,
    // so the proxy keeps the origin (changeOrigin: false) and the session cookie stays first-party.
    proxy: Object.fromEntries(
      ["/api", "/auth", "/v3"].map((path) => [
        path,
        {
          target: process.env.VITE_DEV_API_TARGET ?? "http://localhost:8088",
          changeOrigin: false,
        },
      ]),
    ),
  },
  preview: { port: 4173 },
});
