# Build from the repository root so the workspace lockfile, design-tokens and the API contract
# are in the context:  docker build -f frontend/Dockerfile .
FROM node:22-alpine AS build
RUN npm install -g pnpm@12.18.0
WORKDIR /repo
COPY package.json pnpm-lock.yaml pnpm-workspace.yaml ./
COPY design-tokens ./design-tokens
COPY api/openapi ./api/openapi
COPY api/sdk/typescript/package.json api/sdk/typescript/tsconfig.json api/sdk/typescript/openapi-ts.config.ts ./api/sdk/typescript/
COPY frontend ./frontend
RUN pnpm install --frozen-lockfile --ignore-scripts --filter @alliedpayroll/web...
RUN pnpm api:generate \
 && pnpm --filter @alliedpayroll/design-tokens build \
 && pnpm --filter @alliedpayroll/web build

FROM nginx:alpine
COPY frontend/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /repo/frontend/dist /usr/share/nginx/html
EXPOSE 80
