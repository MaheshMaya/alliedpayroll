#!/usr/bin/env bash
# Runs inside the backend-tools container. Gradle's client dies on the Windows bind mount, so the
# sources are synced into the container-side volume at /build and built there; the API contract the
# tests export is copied back so the repository sees it.
set -euo pipefail

src=/work
dst=/build
mkdir -p "$dst/backend" "$dst/api/openapi"

if [ "${1:-}" = "--clean" ]; then
  shift
  rm -rf "$dst/backend"
  mkdir -p "$dst/backend"
fi

# Sources only: build output, Gradle state and IDE folders stay on each side.
(cd "$src/backend" && tar --exclude=./build --exclude='./*/build' --exclude='./*/*/build' \
  --exclude=./.gradle --exclude='./*/.gradle' --exclude='./*/*/.gradle' --exclude='./*/bin' --exclude='./*/*/bin' \
  -cf - .) | tar -xf - -C "$dst/backend"
cp "$src/api/openapi/backend.json" "$dst/api/openapi/backend.json" 2>/dev/null || true

cd "$dst/backend"
rc=0
./gradlew --console=plain "$@" < <(sleep infinity) || rc=$?

cp "$dst/api/openapi/backend.json" "$src/api/openapi/backend.json" 2>/dev/null || true
# Formatted sources (spotlessApply) come back the same way they went in.
(cd "$dst/backend" && tar --exclude=./build --exclude='./*/build' --exclude='./*/*/build'   --exclude=./.gradle --exclude='./*/.gradle' --exclude='./*/*/.gradle' --exclude='./*/bin' --exclude='./*/*/bin'   -cf - .) | tar -xf - -C "$src/backend"
if [ "$rc" -ne 0 ]; then
  echo "gradle exited $rc; reports are inside the container volume under /build/backend/**/build/reports" >&2
fi
exit "$rc"
