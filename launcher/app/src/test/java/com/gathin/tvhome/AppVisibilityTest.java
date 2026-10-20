package com.gathin.tvhome;

import org.junit.Test;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import static org.junit.Assert.*;

public class AppVisibilityTest {
    @Test public void upgradeKeepsLegacyHiddenChoicesUntilExplicitLibraryChange() {
        assertFalse(AppVisibility.shown("hidden.app", true, false, false));
        assertTrue(AppVisibility.shown("visible.app", false, false, false));
        assertTrue(AppVisibility.shown("hidden.app", true, true, false));
        assertFalse(AppVisibility.shown("visible.app", false, true, true));
    }

    @Test public void usbStaysPinnedEvenWhenLibraryRemovalExists() {
        assertTrue(AppVisibility.shown(AppVisibility.USB_PACKAGE, true, false, true));
    }
    @Test public void firstUseKeepsThemeHiddenApps() {
        assertTrue(AppVisibility.hidden(Arrays.asList("theme.app"), null).contains("theme.app"));
    }
    @Test public void savedChoicesCanShowAnAppHiddenByTheme() {
        assertTrue(AppVisibility.hidden(Arrays.asList("theme.app"), Collections.emptySet()).isEmpty());
    }
    @Test public void usbRemainsVisibleAndSavedSetIsNotMutated() {
        Set<String> saved = new HashSet<>(Arrays.asList(AppVisibility.USB_PACKAGE, "hidden.app"));
        Set<String> hidden = AppVisibility.hidden(Collections.emptyList(), saved);
        assertFalse(hidden.contains(AppVisibility.USB_PACKAGE));
        assertTrue(hidden.contains("hidden.app"));
        assertFalse(hidden.contains("new.app"));
        assertTrue(saved.contains(AppVisibility.USB_PACKAGE));
    }
}
