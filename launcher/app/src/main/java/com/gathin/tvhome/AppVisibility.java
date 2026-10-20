package com.gathin.tvhome;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Desktop-only choices. The USB entry stays available independently of app selection. */
final class AppVisibility {
    static final String USB_PACKAGE = "com.tianci.localmedia";
    static final String HIDDEN_KEY = "hidden_apps";

    static Set<String> hidden(List<String> defaults, Set<String> saved) {
        Set<String> result = saved == null ? new HashSet<>(defaults) : new HashSet<>(saved);
        result.remove(USB_PACKAGE);
        return result;
    }
}
