# Allied Payroll mobile

Flutter app with three modes: employee, supervisor and kiosk. It talks to the one backend through
the generated `allied_api` client (`api/sdk/dart`) and is themed from `allied_tokens`
(`design-tokens/flutter`). Business rules live on the server; the app collects input and shows results.

## Run the checks

Everything runs in the `mobile-tools` compose service, so the host needs only Docker:

```sh
scripts/flutter pub get
scripts/dart run build_runner build --delete-conflicting-outputs   # Riverpod providers
scripts/flutter gen-l10n                                            # ARB -> lib/l10n/generated
scripts/dart format --set-exit-if-changed lib test
scripts/flutter analyze
scripts/dart run custom_lint
scripts/flutter test
scripts/flutter build apk --debug --dart-define-from-file=env/dev.json
```

## Flavors

One binary; `env/dev.json`, `env/staging.json` and `env/prod.json` set the API domain, the OIDC
issuer and the client through `--dart-define-from-file`. The API host is `<workspace>.<API_DOMAIN>`:
the person types the workspace code on the sign-in screen and the app requests the matching
`organization:<workspace>` scope from Keycloak.

## Running on a phone against the compose stack

`*.localhost` names resolve only on the development machine, so a phone cannot reach
`sentinel.localhost:8088` directly. Until a shared dev environment exists, point `API_DOMAIN` and
`OIDC_ISSUER` at a host name the phone resolves to this machine.
