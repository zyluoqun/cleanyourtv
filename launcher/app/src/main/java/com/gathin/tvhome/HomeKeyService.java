package com.gathin.tvhome;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;

/**
 * Sends Home to this launcher, including Coocaa firmware with a hard-coded stock home component
 * and remotes that emit KEYCODE_ALL_APPS (Xiaomi sticks and boxes). Enabled only by user opt-in.
 *
 * Enable over adb:
 *   settings put secure enabled_accessibility_services com.gathin.tvhome/.HomeKeyService
 *   settings put secure accessibility_enabled 1
 */
public class HomeKeyService extends AccessibilityService {

    /** Remote buttons that should open Settings (config.xml, settings_key_scancodes and settings_key_keycodes). */
    private int[] settingsScancodes = new int[0];
    private int[] settingsKeycodes = new int[0];
    private boolean settingsPanel;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        settingsScancodes = getResources().getIntArray(R.array.settings_key_scancodes);
        // Keycodes and the panel choice can also come from a theme; theme.sh restarts this service.
        Config config = Config.load(this);
        settingsKeycodes = config.settingsKeycodes;
        settingsPanel = config.settingsKeyPanel;
    }

    private boolean isSettingsKey(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_SETTINGS) return true;
        for (int code : settingsScancodes) if (event.getScanCode() == code) return true;
        for (int code : settingsKeycodes) if (event.getKeyCode() == code) return true;
        return false;
    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        // The stock launcher handled the remote's Settings button; with it disabled the button does nothing.
        if (isSettingsKey(event)) {
            if (event.getAction() == KeyEvent.ACTION_UP && !event.isCanceled()) {
                Intent open = settingsPanel ? new Intent(this, QuickSettingsActivity.class)
                        : new Intent(Settings.ACTION_SETTINGS);
                startActivity(open.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
            return true;
        }
        if (event.getKeyCode() != KeyEvent.KEYCODE_ALL_APPS
                && event.getKeyCode() != KeyEvent.KEYCODE_HOME) return false;
        if (event.getAction() == KeyEvent.ACTION_UP && !event.isCanceled()) {
            android.util.Log.i("TvHomeKey", "Opening launcher for keyCode=" + event.getKeyCode());
            openHome();
        }
        return true;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
                || !"com.tianci.movieplatform".contentEquals(
                        event.getPackageName() == null ? "" : event.getPackageName())) return;
        CharSequence activity = event.getClassName();
        if (activity == null) return;
        // Some Coocaa HOME paths bypass key filtering and explicitly open these activities.
        // Redirect only the stock home window; playback, settings and other app windows are unaffected.
        if (!"com.coocaa.homepage.vast.HomePageActivity".contentEquals(activity)
                && !"com.ccos.cooui.movieplatform.MainActivity".contentEquals(activity)) return;
        android.util.Log.i("TvHomeKey", "Redirecting Coocaa home: " + activity);
        openHome();
    }

    private void openHome() {
        Intent home = new Intent(Intent.ACTION_MAIN)
                .addCategory(Intent.CATEGORY_HOME)
                .setPackage(getPackageName())
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        startActivity(home);
    }

    @Override
    public void onInterrupt() {
    }
}
