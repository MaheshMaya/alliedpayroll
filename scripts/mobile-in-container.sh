#!/usr/bin/env bash
# Runs inside the mobile-tools container. Gradle (behind `flutter build apk`) and build_runner do not
# cope with the Windows bind mount, so the Dart sources are synced into the volume at /build, the
# command runs there, and the results (formatted sources, goldens, generated code) are synced back.
set -euo pipefail

src=/work
dst=/build
# Every Dart package in the repository, because they depend on each other by path.
packages=(mobile design-tokens/flutter api/sdk/dart)
tool="$1"
shift

if [ "${1:-}" = "--clean" ]; then
  shift
  rm -rf "${dst:?}"/*
fi

# Copies the Dart packages one way. With --mirror the target's sources are replaced first, so a
# file deleted on the host disappears from the volume too; build output and caches stay either way.
sync() {
  local from="$1" to="$2" mode="${3:-}" package
  for package in "${packages[@]}"; do
    [ -d "$from/$package" ] || continue
    mkdir -p "$to/$package"
    if [ "$mode" = "--mirror" ]; then
      find "$to/$package" -mindepth 1 -maxdepth 1 ! -name build ! -name .dart_tool -exec rm -rf {} +
    fi
    (cd "$from/$package" && tar --exclude=./build --exclude=./.dart_tool --exclude='./android/.gradle' \
      --exclude='./ios/Pods' -cf - .) | tar -xf - -C "$to/$package"
  done
}

sync "$src" "$dst" --mirror
mkdir -p "$dst/api/openapi" && cp "$src/api/openapi/backend.json" "$dst/api/openapi/" 2>/dev/null || true

workdir="$dst/${MOBILE_DIR:-mobile}"
mkdir -p "$workdir"
cd "$workdir"
rc=0
"$tool" "$@" || rc=$?

sync "$dst" "$src"
if [ "$rc" -ne 0 ]; then
  echo "$tool exited $rc" >&2
fi
exit "$rc"
