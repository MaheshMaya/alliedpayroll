# ADR 0005: Javadoc where it adds meaning, not on every member

Status: accepted, 2026-11-21.

## Context

ADR 0002 adopted Google Java Style with Checkstyle's `google_checks`. That configuration includes `MissingJavadocMethod` and `MissingJavadocType`, which demand a Javadoc on every public type, constructor and method. The anti-slop standard approved on 2026-11-04 says the opposite about comments: they state a reason or a contract the code cannot express, and a comment that restates the signature is noise to be removed. The first CI run surfaced the conflict with forty findings such as "Missing a Javadoc comment for 'of'" on `TenantId.of(String)`.

## Decision

Keep `google_checks` and suppress exactly `MissingJavadocMethod` and `MissingJavadocType` through `backend/config/checkstyle/checkstyle-suppressions.xml`. Every other Javadoc check stays on, so a Javadoc that is written is well formed (summary sentence, `@param` and `@return` consistency, no forbidden fragments).

Javadoc is expected on: published module `api` packages (interfaces, DTOs, events other modules consume), platform library entry points, anything whose behaviour a reader cannot infer from the name and types, and every class that encodes a rule (why it exists, what it guarantees). It is not expected on self-describing accessors, constructors that only assign fields, trivial factories, or overrides.

## Consequences

- Reviewers judge Javadoc by whether it says something the code does not, following `antislop-code`.
- Published `api` packages without Javadoc are a review finding even though Checkstyle no longer reports them.
- Should the team want the mechanical rule back for `api` packages only, the suppression file can be narrowed by path without touching the Google configuration.
