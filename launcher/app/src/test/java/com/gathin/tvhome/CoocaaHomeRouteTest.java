package com.gathin.tvhome;

import org.junit.Test;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.*;

public class CoocaaHomeRouteTest {
    private static final class Properties implements CoocaaHomeRoute.Properties {
        final Map<String, String> values = new HashMap<>();
        int writes;
        public String get(String key) { return values.getOrDefault(key, ""); }
        public void set(String key, String value) { values.put(key, value); writes++; }
    }

    @Test public void disabledOptionLeavesFirmwareSettingsAlone() throws Exception {
        Properties props = new Properties();
        CoocaaHomeRoute.configure(false, "com.gathin.tvhome", props);
        assertEquals(0, props.writes);
    }

    @Test public void bootResetToStockIsRepairedWithoutAccessibility() throws Exception {
        Properties props = new Properties();
        props.values.put("persist.service.homepage.pkg", "com.tianci.movieplatform");
        props.values.put("persist.service.homepage.cls", "com.coocaa.homepage.vast.HomePageActivity");
        CoocaaHomeRoute.configure(true, "com.gathin.tvhome", props);
        assertEquals("com.gathin.tvhome", props.get("persist.service.homepage.pkg"));
        assertEquals("com.gathin.tvhome.MainActivity", props.get("persist.service.homepage.cls"));
        assertEquals(2, props.writes);
    }

    @Test public void returningHomeDoesNotRewriteAnExistingRoute() throws Exception {
        Properties props = new Properties();
        CoocaaHomeRoute.configure(true, "com.gathin.tvhome", props);
        props.writes = 0;
        CoocaaHomeRoute.configure(true, "com.gathin.tvhome", props);
        assertEquals(0, props.writes);
    }

    @Test public void alternateApplicationIdKeepsTheRealActivityClass() throws Exception {
        Properties props = new Properties();
        CoocaaHomeRoute.configure(true, "custom.launcher", props);
        assertEquals("custom.launcher", props.get("persist.service.homepage.pkg"));
        assertEquals("com.gathin.tvhome.MainActivity", props.get("persist.service.homepage.cls"));
    }
}
