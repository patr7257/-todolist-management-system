# HANDOVER

## Date / branch / PR / CI
- Date: 2026-07-19
- Branch: `main` (all work merged; no open PRs, no feature branches).
- Latest release: **v1.3.2** (tag on `main`). CI (Build Installers) green: Windows + macOS
  client/server installers attached to the GitHub release.

## TLDR of session outcome
DONE and shipped (v1.3.0 -> v1.3.1 -> v1.3.2):
- Full client visual rebuild: Ikonli vector icons (`dk.dtu.ui.Icons`), violet checklist logo,
  single styling authority (AtlantaFX Primer + one `common.css`), dark mode that looks intentional.
- Real JavaFX `TableView` for lists + tasks via the `dk.dtu.ui.Tables` adapter (reuses the
  `dk.dtu.collumns.*` `Column` classes): resizable + auto-fit columns, an "Auto-fit columns"
  button, vertical dividers, single scrollbar, status row tints, drag-reorder.
- Dark native Windows title bar via DWM (`dk.dtu.ui.WindowChrome`, JNA).
- Remember-server auto-connect.
- Performance: `dk.dtu.methods.Spaces` connection pool (reuse one `RemoteSpace` per space,
  `IO_LOCK` serializes ops) + `Users.getUsersCached` (one user query per view, not per row).
- Fixed the v1.3.0 packaged-app crash (jlink runtime was missing `java.logging`, which Ikonli
  needs) by expanding the CI `--add-modules` list; that fix is v1.3.1.
- v1.3.2 installer was downloaded and its installer launched on Patrick's PC.

## Prioritized next steps
1. Confirm v1.3.2 on Patrick's PC: dark title bar in dark mode, Auto-fit button, and that
   actions (create list, click into a list) now feel snappy against the hosted server.
2. If it still is not instant enough: add optimistic UI (reflect the user's change immediately,
   persist in the background, revert on failure). That is the next perf lever after connection reuse.
3. Install v1.3.2 on the girlfriend's PC (in-app updater, or the release MSI).
4. Verify the in-app updater actually works on v1.3.2 (Settings -> Updates should say "current"
   quickly). On v1.2.0 it hung on "Checking..."; if it still hangs, move the version check off the
   rate-limited GitHub API to the `releases/latest` redirect.
5. Separate track (different repo): the phone/PWA todolist + shared backend is `patrickrobelweb`
   issue #90, not this repo.

## Verbatim resume commands (PowerShell)
Build everything (jSpace must already be installed locally):
```
cd "C:\Users\pr\repos\1-Personal\TodoList"; mvn clean install
```
Run the client from source against the hosted server (your normal server):
```
cd "C:\Users\pr\repos\1-Personal\TodoList\client"; mvn -q javafx:run -Djavafx.run.jvmArgs="-Dtodolist.server.ip=100.100.220.67 -Dtodolist.port=9001"
```
Screenshot the running client window (auto-numbered PNGs in `screenshots\`):
```
cd "C:\Users\pr\repos\1-Personal\TodoList"; powershell -ExecutionPolicy Bypass -File scripts\snap.ps1
```

## Gotchas discovered this session
- Packaged (jpackage/jlink) app crashes SILENTLY at startup if the runtime misses a JDK module a
  dependency uses. Ikonli + JNA need `java.logging`; CI `--add-modules` now lists the full set
  (see CLAUDE.md "Packaging installers"). `mvn javafx:run` works even when packaging is broken, so
  ALWAYS test the real MSI, never just the dev run, before trusting a release.
- Auto-fit columns: JavaFX 21 has NO `resizeColumnToFitContent` on the skin. It lives on the
  per-column `TableColumnHeader.resizeColumnToFitContent(int)`, reached by reflecting
  skin -> `getTableHeaderRow` -> `getRootHeader` -> `getColumnHeaders` (see `dk.dtu.ui.Tables`),
  and needs `--add-opens javafx.controls/javafx.scene.control.skin=ALL-UNNAMED` (in `client/pom.xml`
  and the CI client jpackage step).
- Automated screenshots of the JavaFX app (PrintWindow / CopyFromScreen) are UNRELIABLE for layout
  (GPU surface; resizing via Win32 MoveWindow desyncs the scene). For centering/layout truth use
  Patrick's OS screenshots or runtime `localToScene` bounds logging, not my captures.
- jSpace: `new RemoteSpace(uri)` is a full TCP + handshake every time. Always use
  `dk.dtu.methods.Spaces.get(uri)` (pooled) under `Spaces.IO_LOCK`. The NotificationListener's
  long-blocking `get` MUST stay on its own dedicated connection OUTSIDE that lock.
- Demo server users are Alice/Bob/Charlize; the Patrick/Elinooh login buttons are configured
  main-users that only exist on the real hosted server.

## Open decisions waiting on Patrick
- Ship optimistic UI as the next perf step (a v1.3.3)? yes/no.
- Install v1.3.2 on the girlfriend's PC now? yes/no.
- Next session focus: this desktop app, or the `patrickrobelweb` phone/PWA + shared backend (#90)?

## Environment state
- LEFT RUNNING (production, do NOT stop): the hosted jSpace server on the Dokploy VPS, reachable
  over Tailscale at `100.100.220.67:9001`.
- Local: clean. All dev Java stopped; no local server/client running; no Docker; `main` up to date;
  no feature branches; single worktree (`main`). The v1.3.2 client installer was launched on
  Patrick's PC for him to click through.
