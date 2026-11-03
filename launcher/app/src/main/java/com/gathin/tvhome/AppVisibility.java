package com.gathin.tvhome;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Desktop-only choices. The USB entry stays available independently of app selection. */
final class AppVisibility {
    static final String USB_PACKAGE = "com.tianci.localmedia";
    static final String TCL_USB_PACKAGE = "com.tcl.ui_mediaCenter";
    static final String HIDDEN_KEY = "hidden_apps";

    static boolean isUsbPackage(String pkg) {
        return USB_PACKAGE.equals(pkg) || TCL_USB_PACKAGE.equals(pkg);
    }

    static boolean shown(String pkg, boolean hidden, boolean added, boolean removed) {
        return isUsbPackage(pkg) || (!removed && (added || !hidden));
    }

    static Set<String> hidden(List<String> defaults, Set<String> saved) {
        Set<String> result = saved == null ? new HashSet<>(defaults) : new HashSet<>(saved);
        result.remove(USB_PACKAGE);
        result.remove(TCL_USB_PACKAGE);
        return result;
    }
}
