#!/usr/bin/env bash
# Stop hook: rung 1 of the backpressure ladder (format check + typecheck/compile)
# for every layer that exists in the repo. Silent on success. On failure prints
# only the captured errors to stderr and exits 2, which sends the agent back to
# the work. Skips itself when it is already the reason the agent continued, so
# it cannot loop. Safe to install before any code exists: no layer, no checks.

set -u

input="$(cat 2>/dev/null || true)"
case "$input" in
  *'"stop_hook_active":true'*|*'"stop_hook_active": true'*) exit 0 ;;
esac

root="${CLAUDE_PROJECT_DIR:-$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)}"
cd "$root" || exit 0

# shellcheck source=run_silent.sh
. "$root/.claude/hooks/run_silent.sh"

export RUN_SILENT_LOG
RUN_SILENT_LOG="$(mktemp "${TMPDIR:-/tmp}/verify.XXXXXX")"
failed=0

run() {
  run_silent "$1" "$2" >/dev/null 2>&1 || failed=1
}

if [ -f "backend/gradlew" ]; then
  jdk="${ALLIED_JDK:-$HOME/.jdks/jdk-25.0.5+8}"
  gradle_env=""
  if [ -d "$jdk" ]; then
    gradle_env="JAVA_HOME='$jdk' JAVA_OPTS='-Djava.net.preferIPv4Stack=true' "
  fi
  run "backend format check and compile"     "cd backend && ${gradle_env}./gradlew -q --console=plain spotlessCheck compileJava compileTestJava"
fi
if [ -f "api/sdk/typescript/package.json" ]; then
  run "api client build" "pnpm --filter @alliedpayroll/api-client --silent build"
fi

if [ -f "frontend/package.json" ]; then
  if grep -q '"typecheck"' frontend/package.json; then
    run "web typecheck" "pnpm --dir frontend --silent typecheck"
  fi
  if grep -q '"lint"' frontend/package.json; then
    run "web lint" "pnpm --dir frontend --silent lint"
  fi
fi

if [ -f "mobile/pubspec.yaml" ]; then
  run "mobile format check" \
    "dart format --output=none --set-exit-if-changed mobile/lib mobile/test"
  run "mobile analyze" "flutter analyze --no-pub mobile"
fi

if [ "$failed" -ne 0 ]; then
  {
    echo "verify.sh: rung 1 checks failed. Fix these before stopping:"
    cat "$RUN_SILENT_LOG"
  } >&2
  rm -f "$RUN_SILENT_LOG"
  exit 2
fi

rm -f "$RUN_SILENT_LOG"
exit 0
