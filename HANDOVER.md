# HANDOVER

## Date / branch
- Date: 2026-07-17
- Repo: `patr7257/-todolist-management-system`, branch `main`.
- The full rebuild plan is in `docs/UI-REBUILD.md` (read it first). The "remember server"
  feature is on branch `feat/remember-server` (`c6b987e`, build-verified, not merged).

## TLDR
Shipped this session and WORKING: the hosted jSpace server on the Dokploy VPS (Tailscale-only
at `100.100.220.67:9001`), the desktop client `v1.2.0` with an in-app auto-updater, GitHub
release-on-tag installers, downloads on patrickrobel.dk, and a separate Neon-backed `/todo`
web edition.

NOT good: the v1.2.0 visual restyle is superficial and looks bad, especially dark mode
(fixed-color PNG icons vanish, tables are plain, leftover inline styles fight the theme). This
needs a FULL visual rebuild, keeping the skeleton + functionality and rebuilding everything
visual BETTER. Root cause is confirmed (not a guess): AtlantaFX IS bundled and rendering (dark
mode = PrimerDark), but 28 inline `setStyle` calls, 6 hardcoded hex colors, 12 fixed-color PNG
icon usages, and the old blue logo sit on top of it. Overwriting CSS does not fix that; the old
styling must be inventoried and STRIPPED first.

## Prioritized next steps
1. Read `docs/UI-REBUILD.md` in full. It has the confirmed diagnosis, the discovery-first
   cleanup plan, the preferred styling (AtlantaFX Primer + violet, both modes, vector icons,
   new logo), the tooling, and the file map.
2. Do the inventory (the greps in the doc), then STRIP the old styling to a clean baseline
   (single styling authority: AtlantaFX theme + one thin accent-variable stylesheet). Do NOT
   just overwrite `common.css`.
3. Rebuild the visual layer screen by screen with a RENDER-AND-SCREENSHOT loop (Playwright does
   not work on JavaFX; use a window-capture script or TestFX). Verify every change visually and
   with Patrick before releasing.
4. Replace paint PNG icons with theme-colored vector icons (Ikonli) and the welcome logo with
   the violet `appicon.png`.
5. Fold in `feat/remember-server` (auto-remember the last server), then tag a release; the
   in-app updater delivers it.

## Verbatim resume commands (PowerShell)
Run the client from source to iterate on the look (welcome screen shows without a server):
```
cd "C:\Users\pr\repos\1-Personal\-todolist-management-system"; mvn -q -pl client -am javafx:run
```
Inventory the styling to strip (run from the repo root):
```
cd "C:\Users\pr\repos\1-Personal\-todolist-management-system"; git grep -n "setStyle" -- client/src/main/java
```
Check the hosted server is healthy:
```
ssh todolist-vps "docker ps --format '{{.Names}} {{.Status}}' | grep -i todo; docker logs --tail 20 todolist-server-1"
```

## Gotchas
- AtlantaFX renders fine (dark mode proves it). The visual problem is the leftover inline
  styles + fixed-color PNG icons + old logo, NOT a failure to load the theme.
- Overwriting `common.css` alone never works: 28 inline `setStyle` calls and fixed PNGs win over
  it. Inventory and remove them first.
- Playwright is for the WEB app (patrickrobelweb `/todo` + PWA issue #90), NOT for this JavaFX
  desktop app. For the desktop, capture the app window (PowerShell/.NET) or use TestFX.
- Never claim a UI change works from a clean `mvn package`; this session shipped v1.2.0 unseen
  and it looked broken. Render, screenshot, eyeball, confirm, THEN release.
- Two separate backends: desktop (jSpace server) vs web `/todo` (Neon). They do not share data.
  Unifying them is patrickrobelweb issue #90 (PWA + shared backend).

## Open decisions waiting on Patrick
- Ikonli icon pack (Feather vs Material2) for the new vector icons.
- Whether to move the pseudo-tables (ListView + manual HBox rows) to real `TableView`s during
  the rebuild (cleaner styling) or keep the current structure.
- The PWA / shared-backend direction in issue #90 (HTTP gateway vs unify on one DB + API).

## Environment state
- LEFT RUNNING (production, do NOT stop): the Hetzner VPS, Dokploy, and `todolist-server-1`.
  Reach it via `ssh todolist-vps`.
- Local: clean. `main` up to date, only-`main` branches except `feat/remember-server`
  (build-verified, unmerged). No dev servers or local Docker running.
