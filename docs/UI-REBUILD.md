# UI rebuild plan: strip the old styling, rebuild the visual layer properly

Purpose: a researched, discovery-first plan to rebuild the JavaFX client's LOOK (not its
functionality) so it is genuinely modern and both light and dark modes look great. Keep the
skeleton and all functionality; rebuild everything visual BETTER. Do NOT blindly overwrite CSS
again: that is what failed, because styling is spread across CSS + inline Java + fixed-color
PNG assets, so a new stylesheet gets fought by everything it did not remove.

## Confirmed diagnosis (from real investigation this session, not a guess)
- AtlantaFX 2.0.1 IS bundled in the installed app (`atlantafx-base-2.0.1.jar` is on the app
  classpath in `C:\Program Files\TodoList Client\app`) and the theme engine IS rendering:
  toggling dark mode produces AtlantaFX PrimerDark. So "styling does not apply" is NOT the
  problem. The problem is the restyle was superficial and sits on a messy base.
- Why it still looks bad:
  - The sidebar and row icons are fixed-color PNGs (`homeicon.png`, `filter.png`, `save.png`,
    `settings.png`, `gobackicon.png`, `RearrangeColumn.png`, `reordericon.png`,
    `deleteicon.png`, `load.png`). They do not adapt to the theme, so in dark mode they nearly
    vanish, and everywhere they look like old handmade paint art. 12 usages in Java.
  - The in-app welcome logo is still the old blue-circle `todo.png`, which clashes with the new
    violet checklist brand (`appicon.png`).
  - 28 inline `setStyle(...)` calls remain across 9 files, plus 6 hardcoded hex colors in Java.
    These override / fight the AtlantaFX theme and do not adapt to light/dark. Files:
    `ClientConnectDialog`, `scenes/B_LoginScreen`, `scenes/C_MainMenu`, `scenes/D_TodoListView`,
    `collumns/ListCompletionColumn`, `collumns/ListOwnerColumn`, `collumns/Priority`,
    `collumns/TaskOwnerColumn`, `collumns/TaskStatusColumn`.
  - The lists/tasks "tables" are not real `TableView`s: they are `ListView`s of manual `HBox`
    rows with a hand-built header (`C_MainMenu`, `D_TodoListView`, `collumns/*`). They are
    unstyled and sparse, so they look empty and plain.
  - `common.css` was rewritten as a slim brand overlay on AtlantaFX looked-up color variables,
    which is the right idea, but it cannot win against 28 inline styles + fixed PNGs.
- Net: light mode looks unchanged, dark mode looks broken (invisible icons, plain tables).

## Principle for the rebuild
Single styling authority. The ONLY sources of visual style should be:
1. AtlantaFX user-agent theme (`PrimerLight` / `PrimerDark`, swapped globally by
   `DarkModeManager`), and
2. ONE thin brand stylesheet that overrides ONLY AtlantaFX looked-up color variables
   (`-color-accent-*`, `-color-bg-*`, `-color-fg-*`, `-color-border-*`, etc.) so BOTH modes
   adapt automatically, plus a handful of semantic classes (status colors).
Everything else (inline `setStyle`, hardcoded hex, fixed-color PNG icons) must be REMOVED, not
layered over. Inline style is allowed only for genuinely dynamic per-instance values (e.g. a
user-chosen main-user color), never for theme.

## Cleanup plan (discovery-first, do this BEFORE writing new styles)
1. Inventory every styling source and read them, do not assume:
   - `grep -rn "setStyle" client/src/main/java` (28 calls) and decide per call: delete, or move
     to a style class in the brand stylesheet, or keep only if truly dynamic.
   - `grep -rn "#[0-9a-fA-F]\{6\}" client/src/main/java` (6 hardcoded colors): replace with
     theme variables / classes.
   - `grep -rn "/Icons/" client/src/main/java` (12 icon usages): replace PNG icons with
     theme-aware vector icons (see below).
   - `grep -rn "getStyleClass" client/src/main/java` (77 usages): audit which classes still
     exist in `common.css` after the rewrite; remove dead classes and dead references.
2. Establish the clean baseline: keep the AtlantaFX global theme in `ClientApp` +
   `DarkModeManager`; reduce `common.css` to the accent-variable overrides + semantic status
   classes only. Verify a screen renders correctly in BOTH modes with ZERO inline styles before
   proceeding.
3. Migrate screen by screen (`A_WelcomeScreen`, `B_LoginScreen`, `C_MainMenu`,
   `D_TodoListView`, `SettingsDialog`, `ClientConnectDialog`, the `collumns/*` cells), removing
   inline styles as you go and screenshot-verifying each.

## Preferred styling (Patrick's choice, locked in)
- AtlantaFX **Primer** base with a **violet accent** (roughly `#5B4FE6` emphasis, cyan `#06B6D4`
  secondary), matching the new app icon and the patrickrobel.dk aurora accent.
- FULL light AND dark, and dark mode MUST look intentional (no invisible icons, real contrast).
- Modern, clean, tasteful (not flashy). Comfortable spacing, real table styling (zebra/hover,
  accent sort headers), card-like containers, clear primary actions in violet.
- Replace the old paint icons with crisp theme-colored **vector icons** (Ikonli, e.g.
  `ikonli-feather-pack` or `ikonli-material2`, added like AtlantaFX: a dependency in
  `client/pom.xml`, loads from the app classpath, do NOT add it to jlink/jpackage
  `--add-modules`). Color them via CSS so they follow light/dark.
- Replace the welcome logo with the violet checklist icon (`appicon.png`) or a vector mark.
- Keep the skeleton: 4 screens + persistent sidebar + dialogs + the jSpace tuple protocol and
  every feature (lists, tasks, priority, status, due date, owner, reorder, filter, dark mode).
  Rebuild only the visual layer.

## Tooling for the next session (use these, do not hand-guess)
- Visual direction: the `frontend-design`, `impeccable`, and `ui-ux-pro-max` skills for palette,
  spacing, hierarchy, and motion PRINCIPLES. They are web-oriented, so use them for the design
  system, then translate to JavaFX CSS + AtlantaFX variables.
- CRITICAL, how to actually SEE the JavaFX render (this session's mistake was shipping without
  looking): Playwright does NOT work on a JavaFX desktop app. Build a screenshot loop instead:
  - Option 1: run `mvn -pl client -am javafx:run` and capture the window with a small
    PowerShell/.NET script (find the "TodoList" window by title, `GetWindowRect`,
    `Graphics.CopyFromScreen` to a PNG), then Read the PNG and iterate. Capture only the app
    window, not the whole desktop.
  - Option 2: TestFX (JavaFX UI test framework) to drive the app and snapshot scenes/nodes to
    images, optionally headless via Monocle. Good for a repeatable per-screen visual check.
  - Verify EVERY visual change by rendering + screenshotting + eyeballing (and confirm with
    Patrick) before any release. A clean `mvn package` is NOT evidence the UI looks right.
- Icons: the Ikonli icon reference / AtlantaFX Sampler app for component + icon names and to
  preview Primer light/dark before coding.
- For the WEB todolist and the PWA (patrickrobelweb issue #90): THERE Playwright is correct, use
  the `website:verify` skill to screenshot pages in both themes.

## Verification discipline (the lesson from this session)
Never claim a visual change works from a compile alone. This session shipped the v1.2.0 restyle
without ever rendering it, so it looked unchanged/broken in practice. Always: change -> run ->
screenshot -> inspect -> confirm with Patrick -> only then tag a release.

## Release mechanics (unchanged, already working)
- Tag `vX.Y.Z` builds MSI + DMG via GitHub Actions and the in-app auto-updater delivers them
  (see the `self-updating-desktop-app` user skill and CLAUDE.md). Version is baked from the tag.
- The "remember the last connected server" work is already on branch `feat/remember-server`
  (commit `c6b987e`, build-verified): fold it into the rebuild or ship it once the UI is redone.
  Do NOT release it alone as the "styling fix"; it is a functional feature, not the redo.

## Files the rebuild will touch (map)
- Theme core: `ClientApp.java` (user-agent stylesheet), `DarkModeManager.java`,
  `common.css` (reduce to accent vars + semantic classes).
- Screens: `scenes/A_WelcomeScreen`, `scenes/B_LoginScreen`, `scenes/C_MainMenu`,
  `scenes/D_TodoListView`.
- Chrome + dialogs: `Sidebar.java` (icons), `ClientConnectDialog.java`, `SettingsDialog.java`.
- Tables: the `collumns/*` package (headers/cells) and the pseudo-table construction in
  `C_MainMenu` / `D_TodoListView` (consider moving to real `TableView`s while keeping behavior).
- Assets: `client/src/main/resources/Icons/*` (retire the paint PNGs; add vector icons + the
  violet logo).
- Build: `client/pom.xml` (add Ikonli). Do not touch the jlink/jpackage `--add-modules` lists.
