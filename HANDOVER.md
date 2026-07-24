# HANDOVER

## Date, branch, PR, CI
- 2026-07-24. Branch: `main`. PR #40 (issue #29 dead-code sweep) and PR #42 (issue #41 auto-release CI) squash-merged into `main`; branches deleted local + origin. No open PRs.
- CI now exists on two levels: `ci.yml` gates every PR (build + tests + module guard), and `build-installers.yml` auto-releases installers on every non-docs merge to `main` (patch bump from the latest release; manual `v*` tags still work for minor/major).

## TLDR of session outcome
Both remaining next steps from the previous handover are DONE:
- Issue #29 dead-code sweep merged as PR #40 (+186/-1224, 40 files): deleted `TupleSpaces`(+test), the jSpace URI/port/host members of `Config`, the whole `dk.dtu.shared.models` package, the dead `requestsUri`/`responsesUri` param threading (54 call sites through `methods/`+`collumns/`+`scenes/`), the stubbed session export/import feature (`DataManagement` + Sidebar save/load UI), and legacy `ServerPrefs` ip/port keys. Issue #29 closed, board card Done.
- Installers now bake `-Dtodolist.api.url` (repo variable `TODOLIST_API_URL`, falling back to the production URL) instead of the dead `-Dtodolist.server.ip`/`-Dtodolist.port`; the unused `TODOLIST_SERVER_HOST` repo variable was deleted.
- CLAUDE.md updated: stale jSpace/module/test lines fixed; new conventions documented (ViewPrefs view-state persistence, `DarkModeManager.prepareDialog` for all dialogs).
- jSpace VPS decommission EXECUTED (partner confirmed on v2.0.2): `todolist-server-1` container, `todolist-server:latest` image, `todolist_todolist-data` volume, and `/opt/todolist` removed from the VPS. Live API verified up afterwards (HTTP 401 on unauthenticated requests, as expected). Dokploy auto-redeployed the API from the merged `main`.

The TodoList product is now fully post-jSpace with zero dead transport code.

## Prioritized next steps
1. Smoke-test the auto-released MSI from the PR #42 merge (expected v2.0.3): take the in-app update banner, then launch, sign in, change a task status, confirm no save/load session buttons in the sidebar. Partner's client should offer the same update.
2. Nothing else is pending; new work starts fresh from the board.

## Verbatim resume commands (PowerShell)
Run the API locally (embedded Postgres if no DATABASE_URL is set):
```
cd "C:\Users\pr\repos\1-Personal\TodoList"; mvn -q install -DskipTests; mvn -pl api exec:java
```
Run the desktop client (defaults to the live API; sign in with your web account):
```
cd "C:\Users\pr\repos\1-Personal\TodoList"; mvn -q install -DskipTests; mvn -pl client javafx:run
```
Seed a new login account into Neon (prompts for the Neon unpooled URL, then email/name/password):
```
cd "C:\Users\pr\repos\1-Personal\TodoList"; .\scripts\seed-user.ps1
```

## Gotchas discovered this session
- Every non-docs merge to `main` now SHIPS a release automatically (patch bump). Docs-only merges (`*.md`, `docs/`, `.claude/`) skip it. The jlink/jpackage module list is single-sourced in `scripts/installer-modules.txt`; the guard (`scripts/check-installer-modules.ps1`) runs in PR CI and before each release build.
- `client/pom.xml` had no direct gson dependency; the client used Gson transitively via `shared`. When `shared` dropped gson (models package deleted), gson had to be added directly to `client/pom.xml`. Watch for other transitive-only dependencies when trimming `shared`.
- The old export/import UI lived entirely in `Sidebar.java`, not `SettingsDialog.java` (earlier notes pointed at the wrong file).
- The TodoList dev board (Project #7) has only Todo / In Progress / Done, no "In review" column.
- The client baked default API URL comes from `Config.DEFAULT_API_BASE_URL`; the `TODOLIST_API_URL` repo variable is optional (only needed to point release builds elsewhere).
- Dokploy autodeploys the API from `main`: every merge rebuilds and restarts the live API container (new container suffix each time; brief restart blip).

## Open decisions waiting on Patrick
- None. (The "tag v2.0.3?" question resolved itself: the auto-release on the PR #42 merge ships it.)

## Environment state
- Nothing left running locally: no dev servers, no Docker; the embedded test Postgres shut itself down after `mvn install`.
- VPS: jSpace stack fully gone; remaining tenants are the live TodoList API (Dokploy, behind Traefik TLS), Catan, and Dokploy infra.
- Repo on `main` only; all session branches deleted local + origin. This `HANDOVER.md` is written but NOT committed (protected `main`); `.claude/.codev-ack` is locally modified (expected, session decision lines).
