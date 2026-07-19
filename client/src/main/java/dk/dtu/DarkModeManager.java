package dk.dtu;

import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import javafx.application.Application;
import javafx.scene.Scene;

import java.util.List;

/**
 * Manages theme switching for the application.
 *
 * Dark mode toggling swaps the GLOBAL AtlantaFX user-agent stylesheet between
 * PrimerLight and PrimerDark, so every window (including dialogs) follows the
 * theme automatically. The brand overlay is two files, managed together by
 * {@link #applyBrand(List)}:
 *
 *   common.css          structure + the warm-paper LIGHT tokens (always attached)
 *   theme-warm-dark.css warm-charcoal DARK token overrides (attached after
 *                       common.css only while dark mode is on)
 *
 * Dark mode state is static so dialogs created at any time (SettingsDialog,
 * ClientConnectDialog) pick up the current theme by calling applyBrand.
 */
public class DarkModeManager {

    private static final String COMMON_CSS = "/common.css";
    private static final String WARM_DARK_CSS = "/theme-warm-dark.css";

    private static boolean darkMode = false;

    private Scene currentScene;

    public DarkModeManager() {
    }

    public void setScene(Scene scene) {
        this.currentScene = scene;
        attachBrandStylesheet(scene);
        applyTheme();
    }

    public void toggleDarkMode() {
        darkMode = !darkMode;
        applyTheme();
    }

    public boolean isDarkMode() {
        return darkMode;
    }

    /**
     * Attach the brand overlay stylesheets to a scene (idempotent).
     * Public so dialogs with their own scenes can share the brand styling.
     */
    public void attachBrandStylesheet(Scene scene) {
        if (scene == null) {
            return;
        }
        applyBrand(scene.getStylesheets());
    }

    /**
     * Ensure the given stylesheet list carries the brand overlay for the
     * CURRENT theme: common.css always, plus theme-warm-dark.css after it in
     * dark mode (removed again in light mode). Safe to call repeatedly and
     * from dialogs (pass e.g. {@code getDialogPane().getStylesheets()}).
     */
    public static void applyBrand(List<String> stylesheets) {
        try {
            String common = DarkModeManager.class.getResource(COMMON_CSS).toExternalForm();
            String warmDark = DarkModeManager.class.getResource(WARM_DARK_CSS).toExternalForm();

            if (!stylesheets.contains(common)) {
                stylesheets.add(common);
            }
            if (darkMode) {
                // Order matters: the dark tokens must come after common.css.
                if (!stylesheets.contains(warmDark)) {
                    stylesheets.add(warmDark);
                }
            } else {
                stylesheets.remove(warmDark);
            }
        } catch (Exception e) {
            System.err.println("Could not load brand stylesheets: " + e.getMessage());
        }
    }

    private void applyTheme() {
        // The user-agent stylesheet is global, so this affects all open windows.
        if (darkMode) {
            Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
        } else {
            Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
        }
        // Swap the warm token overlay on the active scene to match.
        if (currentScene != null) {
            applyBrand(currentScene.getStylesheets());
        }
    }
}
