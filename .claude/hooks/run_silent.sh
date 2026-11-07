#!/usr/bin/env bash
# run_silent "description" "command"
# Runs the command with all output captured. Prints one line on success,
# the whole captured output on failure, and returns the command's exit code.
# Source this file, then call run_silent. Set RUN_SILENT_LOG to a file path to
# also collect failures there (used by verify.sh to report everything at once).

run_silent() {
  local desc="$1"
  local cmd="$2"
  local out
  out="$(mktemp "${TMPDIR:-/tmp}/run_silent.XXXXXX")"

  local rc=0
  bash -c "$cmd" >"$out" 2>&1 || rc=$?
  if [ "$rc" -eq 0 ]; then
    printf '✓ %s\n' "$desc"
    rm -f "$out"
    return 0
  fi

  printf '✗ %s (exit %d)\n' "$desc" "$rc"
  printf -- '--- %s: %s ---\n' "$desc" "$cmd"
  cat "$out"
  if [ -n "${RUN_SILENT_LOG:-}" ]; then
    {
      printf '✗ %s (exit %d)\n' "$desc" "$rc"
      printf -- '--- %s: %s ---\n' "$desc" "$cmd"
      cat "$out"
    } >>"$RUN_SILENT_LOG"
  fi
  rm -f "$out"
  return "$rc"
}
