---
name: backpressure
description: Use whenever you write or change code in Allied Payroll and need to prove it works — after an edit set, at the end of a plan phase, before calling a slice done, or when a check fails. Defines the verification ladder (format and typecheck, module tests, slice and golden tests, full suite in CI), the run_silent wrapper that keeps check output out of context, fail-fast and filtering flags per toolchain, the red-before-green and never-weaken-a-test rules, and when to hand a run to a subagent. Pairs with testing-strategy (what tests exist) and engineering-workflow (when a slice is done).
---

# Backpressure

An agent succeeds in proportion to its ability to verify its own work. Verification is cheap when it runs often, fails fast and returns only what matters. This skill turns that into a fixed ladder of checks and a fixed way of running them, so that neither the model nor the reviewer has to decide what to run or what to read.

Adapted from HumanLayer's writing on context-efficient backpressure and harness engineering; the rules below are the project's own.

## 1. The ladder

| When | Run | Budget |
|---|---|---|
| After every edit set (a coherent change to one module or feature) | Formatter check and typecheck or compile for the touched layer | Seconds |
| At the end of each plan phase | The touched module's unit tests, plus any slice test the phase names | Under 2 minutes |
| Before saying a slice or task is done | Mandatory slice tests (`testing-strategy` §2), golden files if payroll or compliance changed, the Playwright or integration flow for the headline task | Under 10 minutes |
| On push and in CI only | The full pipeline (`testing-strategy` §7) | Not run locally at every stop |

The Stop hook in `.claude/hooks/verify.sh` runs the first rung automatically whenever the agent stops. Everything above that rung is run deliberately, by the agent, at the moment the table says.

Never run the full suite at every stop. Never skip the first rung because "it is a small change".

## 2. Running a check

Every check goes through `run_silent`, sourced from `.claude/hooks/run_silent.sh`:

```bash
source .claude/hooks/run_silent.sh
run_silent "payroll unit tests" "./gradlew -q :backend:modules:payroll:test --fail-fast"
```

- Success prints one line with a check mark and nothing else.
- Failure prints the captured output in full, then the exit code is returned.
- Several checks in one go: list them one after another; the first failure stops the batch.

What it replaces, and why these are forbidden:

- `cmd | head -50` or `cmd | tail -50`: truncates the one line that mattered and forces a rerun.
- `cmd > /dev/null; echo done`: swallows the failure, and the agent re-investigates later at a higher cost.
- Reading thousands of lines of passing output: fills context with hay and leads to hallucinated file contents.

## 3. Fail fast and filter, per toolchain

| Layer | Fast checks (rung 1) | Tests (rung 2 and 3) | Flags |
|---|---|---|---|
| Backend (Gradle, root `gradlew`) | `./gradlew -q spotlessCheck compileJava compileTestJava` | `./gradlew -q :backend:modules:{name}:test` | `--fail-fast`, `-q`; add `--tests 'ClassName'` to narrow; `--console=plain` in hooks |
| Web (`frontend`, pnpm) | `pnpm --dir frontend typecheck` then `pnpm --dir frontend lint` | `pnpm --dir frontend test -- --run` | Vitest `--bail=1 --reporter=dot`; Playwright `--max-failures=1 --reporter=line` |
| Mobile (`mobile`) | `dart format --output=none --set-exit-if-changed mobile` then `flutter analyze` | `flutter test` | `--reporter=compact`; run one file with `flutter test test/path_test.dart` |
| OpenAPI (`api/openapi`) | `spectral lint` and `openapi-diff` against `main` | Contract tests via the SDK | Run only when the contract changed |

Commands are the ones ADR 0002 and the client skills already assume. When `CLAUDE.md` gains the real build commands, they win over this table, and this table is updated in the same change.

When a failure is long, isolate the assertion before reading further: `grep -nE "FAILED|Expected|Actual|Error|at com\.alliedpayroll"` on the captured file. Read the first failure, fix it, rerun. One failure at a time.

## 4. Rules that keep tests honest

1. **Red before green.** A new test is run once before the production change and must fail. A test that passes on the pre-change code proves nothing. Say in the handoff that the red run was seen.
2. **Never weaken a test to pass it.** Assertions are not loosened, cases are not deleted, tolerances are not widened, and tests are not skipped or quarantined to get a green run. A test may change only when the requirement changed, and the change is named in the commit.
3. **Regression stays green.** Earlier slices' tests run with the current slice's tests at rung 3. A defect carried forward is a defect.
4. **Golden files are data, not targets.** A payroll or compliance golden file changes only with the domain expert's sign-off (`testing-strategy` §3). Regenerating a golden file to match new output is not a fix.
5. **Report real results.** Quote the check mark lines or the failure. "Should pass" and "tests look fine" are not results. If a check was not run, say so.
6. **Verify the slice end to end.** A backend slice is exercised with curl against the running API; a web slice in a browser through Playwright; a mobile slice on a device or emulator. Compile and unit tests alone do not close a slice.

## 5. Hand long runs to a subagent

Runs that produce more than a screen of output, or take more than a couple of minutes, go to a subagent with a narrow brief: the exact command, the working directory, and the instruction to return only pass or fail, the count, and for each failure the test name, the assertion, and `file:line`. The parent reads the summary, not the log. Do the same for log digging, `gh run view`, `kubectl logs` and database queries.

Do not delegate the fix; the parent makes the change, then reruns the narrow check itself.

## 6. Hooks

- **Stop:** `.claude/hooks/verify.sh` runs rung 1 for every layer that exists, silently on success. On failure it prints only the errors to stderr and exits with code 2, which returns the agent to the work. It skips itself when invoked as a continuation of a previous stop, so it cannot loop.
- Hooks stay fast. A check that takes longer than about a minute moves out of the Stop hook and into the ladder's later rungs, run by the agent or CI.
- Add a hook only after a real failure that a hook would have caught. Remove hooks that stop earning their place.

## 7. Checklist for a handoff

- [ ] Rung 1 clean on every touched layer (hook silent or check marks quoted)
- [ ] Module tests run for each plan phase; results quoted
- [ ] New tests were seen red first
- [ ] No test weakened, skipped or quarantined to get green
- [ ] Slice verified end to end with the tool the layer needs
- [ ] Golden files unchanged, or changed with named sign-off
- [ ] Anything not run is listed as not run
