# HANDOVER

## Date, branch, PR, CI
- 2026-07-21. Branch: `main` (both TodoList and patrickrobelweb). No open PRs; everything from this session is merged.
- Latest release: `v2.0.2` (client MSI + DMG published, guard green). API live at `https://api.todolist.patrickrobel.dk`. Web live at `patrickrobel.dk/todo`.

## TLDR of session outcome
The whole TodoList product was unified onto ONE source of truth (a Java HTTP API in this repo on the existing Neon Postgres), the web/phone app was redesigned to look like the desktop app, the desktop was moved onto the API, jSpace was retired, and several follow-on fixes shipped as v2.0.1 and v2.0.2. All done and merged:
- API module (`api/`, `dk.dtu.api`) on Neon, deployed on Dokploy behind Traefik TLS (#23, #24).
- Website `/todo` is now a thin same-origin proxy to the API; old Neon backend removed (patrickrobelweb #105).
- Website `/todo` redesigned to the desktop "Soft Warm Minimal" brand + desktop-parity tables, responsive (patrickrobelweb #111).
- Desktop client repointed off jSpace onto the API with email/password login (#19); parity write-endpoints for desktop-only fields added (#30); jSpace server module retired (#25).
- v2.0.1: fixed the desktop launch crash (installer runtime was missing `java.net.http` + `jdk.jfr`) and added a jdeps CI guard so a missing module can never silently ship again (#33).
- v2.0.2: per-user view state (filters, column visibility/order/width, sort, reordering) now auto-persists locally and restores on open (#37); all desktop dialogs got an owner + window-modality (fixes the macOS "app slides away" bug) and are branded + dark-mode aware (#38).
- Added a committed seed-user tool + `scripts/seed-user.ps1`/`.sh` to create API/Neon accounts (#35); onboarded the second user (Patrick's partner).
- Rotated the Neon password (it had leaked into a pasted log); updated Dokploy + Vercel.

Board: TodoList dev board = GitHub Project #7. Open/future issue: #29 (deep dead-code sweep).

## Prioritized next steps
1. Make sure BOTH desktop clients are on v2.0.x (Patrick on v2.0.2; partner should take the v2.0.2 update banner). Only then decommission the live jSpace Dokploy service + `/opt/todolist` compose on the VPS. Old pre-2.0.0 clients still speak jSpace and break the moment it is off.
2. Do TodoList #29: remove the now-dead jSpace remnants (`shared` `TupleSpaces`, `Config` jSpace URI methods, the now-ignored `requestsUri`/`responsesUri` params threaded through client `methods/`+`collumns/`+`scenes/`). Keep the build green.
3. Optional: consider adding a short ViewPrefs / prepareDialog note to CLAUDE.md "Notable conventions" (new this session); and baking `TODOLIST_API_URL` into the installer instead of the now-irrelevant `TODOLIST_SERVER_HOST`.

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
Run the website locally:
```
cd "C:\Users\pr\repos\1-Personal\patrickrobelweb\website"; pnpm install; pnpm dev
```

## Gotchas discovered this session
- Installer runtime modules: the client is packaged with a slimmed jlink runtime; any JDK module the app references must be in the `--add-modules` list in `.github/workflows/build-installers.yml` (both jobs) AND `build-installers.ps1`, or the app crashes silently at launch. A jdeps guard step now fails CI if the list is incomplete (it caught `jdk.jfr`). Currently required: java.net.http, jdk.jfr, jdk.charsets, java.datatransfer, plus the earlier set.
- Neon URL has inline `user:password@host`; the Java API parses credentials out (`DataSources.parse`) rather than prefixing `jdbc:`. Use the UNPOOLED Neon string for the API (HikariCP over Neon's pgBouncer breaks prepared statements).
- `TODO_SESSION_SECRET` must match on Dokploy (API) and Vercel (website) or tokens do not interoperate. The Dokploy `DATABASE_URL` is set BY HAND; Neon password rotations must be copied there manually (Vercel auto-syncs via the Neon integration; Dokploy does not).
- Dokploy autodeploys the API from `main`, so any merge to TodoList `main` rebuilds the API.
- The desktop auto-updater checks GitHub Releases on launch and shows an "Update available" banner (8s timeout, correct repo). v1.3.x builds had a broken updater (old repo name); v2.0.1+ work.
- View state persists via `dk.dtu.ViewPrefs` (Java Preferences, per signed-in user, this machine only). Dialogs must be prepared via `DarkModeManager.prepareDialog(dialog, owner)` to get owner + WINDOW_MODAL + brand/dark styling.
- Boards: TodoList = Project #7; patrickrobelweb = #2.

## Open decisions waiting on Patrick
- Decommission the live jSpace service now (partner confirmed on v2.0.2?) or wait a bit longer.
- Schedule the #29 dead-code cleanup, or leave parked.

## Environment state
- No dev servers or Docker started by this session; Docker Desktop is not running. The desktop client you launched is your own process.
- Both repos on `main` only; all session worktrees removed and all session branches deleted (local + origin).
- This `HANDOVER.md` is written but NOT committed (current branch is protected `main`). `.claude/.codev-ack` has this session's line (expected, gitignored intent). The live API, redesigned website, and v2.0.2 release are all deployed.
