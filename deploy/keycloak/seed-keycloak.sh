#!/bin/bash
# Seeds the local Keycloak with the realm, the clients and the two demo tenants as organisations.
# Idempotent: re-running updates nothing that already exists. Development only.
set -euo pipefail

KC=/opt/keycloak/bin/kcadm.sh
REALM=alliedpayroll
URL="${KEYCLOAK_URL:-http://auth.localhost:8081}"
PASSWORD="${DEMO_USER_PASSWORD:-Welcome@123}"

$KC config credentials --server "$URL" --realm master \
  --user "${KEYCLOAK_ADMIN:-admin}" --password "${KEYCLOAK_ADMIN_PASSWORD:-admin}"

if ! $KC get "realms/$REALM" >/dev/null 2>&1; then
  $KC create realms -s realm="$REALM" -s enabled=true -s organizationsEnabled=true \
    -s displayName="Allied Payroll" -s loginTheme=keycloak -s sslRequired=none \
    -s registrationAllowed=false -s resetPasswordAllowed=true
  echo "realm $REALM created"
fi

client_id() {
  $KC get clients -r "$REALM" -q clientId="$1" --fields id --format csv --noquotes | head -n1
}

ensure_client() {
  local name="$1"; shift
  if [ -z "$(client_id "$name")" ]; then
    $KC create clients -r "$REALM" -s clientId="$name" -s publicClient=true -s protocol=openid-connect \
      -s standardFlowEnabled=true -s directAccessGrantsEnabled=false -s implicitFlowEnabled=false "$@"
    echo "client $name created"
  fi
  local id; id="$(client_id "$name")"
  # Re-applying the settings keeps redirect URIs in step with this file after a port or host change.
  $KC update "clients/$id" -r "$REALM" "$@"
  local scope; scope="$($KC get client-scopes -r "$REALM" -q name=organization --fields id --format csv --noquotes | head -n1)"
  if [ -n "$scope" ]; then
    $KC update "clients/$id/default-client-scopes/$scope" -r "$REALM" >/dev/null 2>&1 || true
  fi
}

ensure_client alliedpayroll-web \
  -s 'redirectUris=["http://sentinel.localhost:5173/auth/callback","http://nimbus-it.localhost:5173/auth/callback","http://sentinel.localhost:8088/auth/callback","http://nimbus-it.localhost:8088/auth/callback"]' \
  -s 'webOrigins=["http://sentinel.localhost:5173","http://nimbus-it.localhost:5173"]' \
  -s 'attributes={"pkce.code.challenge.method":"S256","post.logout.redirect.uris":"http://sentinel.localhost:5173/*##http://nimbus-it.localhost:5173/*##http://sentinel.localhost:8088/*##http://nimbus-it.localhost:8088/*"}'

ensure_client alliedpayroll-mobile \
  -s 'redirectUris=["com.alliedpayroll.app://callback"]' \
  -s 'attributes={"pkce.code.challenge.method":"S256"}'

ensure_client alliedpayroll-kiosk \
  -s 'redirectUris=["com.alliedpayroll.kiosk://callback"]' \
  -s 'attributes={"pkce.code.challenge.method":"S256"}'

org_id() {
  # The image has no awk; match the alias column by hand.
  local line id alias
  while IFS= read -r line; do
    id="${line%%,*}"; alias="${line##*,}"
    if [ "$alias" = "$1" ]; then echo "$id"; return; fi
  done < <($KC get organizations -r "$REALM" -q search="$1" --fields id,alias --format csv --noquotes)
}

ensure_org() {
  local alias="$1" name="$2" domain="$3"
  if [ -z "$(org_id "$alias")" ]; then
    $KC create organizations -r "$REALM" -s name="$name" -s alias="$alias" -s enabled=true \
      -s "domains=[{\"name\":\"$domain\",\"verified\":true}]"
    echo "organisation $alias created"
  fi
}

user_id() {
  $KC get users -r "$REALM" -q username="$1" -q exact=true --fields id --format csv --noquotes | head -n1
}

ensure_user() {
  local username="$1" first="$2" last="$3" email="$4" org="$5"
  if [ -z "$(user_id "$username")" ]; then
    $KC create users -r "$REALM" -s username="$username" -s enabled=true -s emailVerified=true \
      -s firstName="$first" -s lastName="$last" -s email="$email"
    $KC set-password -r "$REALM" --username "$username" --new-password "$PASSWORD"
    echo "user $username created"
  fi
  local uid oid; uid="$(user_id "$username")"; oid="$(org_id "$org")"
  if ! $KC get "organizations/$oid/members/$uid" -r "$REALM" >/dev/null 2>&1; then
    $KC create "organizations/$oid/members" -r "$REALM" -b "\"$uid\""
    echo "user $username joined $org"
  fi
}

ensure_org sentinel "Sentinel Facility Services" sentinel.example
ensure_org nimbus-it "Nimbus IT Solutions" nimbus-it.example

# One user per persona and tenant. Fictional names; see backend/dev/seed/README.md.
ensure_user admin.sentinel   Asha    Varma    admin@sentinel.example      sentinel
ensure_user payroll.sentinel Rohan   Iyer     payroll@sentinel.example    sentinel
ensure_user hr.sentinel      Meera   Nair     hr@sentinel.example         sentinel
ensure_user guard.sentinel   Suresh  Kumar    guard@sentinel.example      sentinel
ensure_user admin.nimbus     Divya   Menon    admin@nimbus-it.example     nimbus-it
ensure_user payroll.nimbus   Karthik Rao      payroll@nimbus-it.example   nimbus-it
ensure_user staff.nimbus     Priya   Shetty   staff@nimbus-it.example     nimbus-it

echo "keycloak seed complete"
