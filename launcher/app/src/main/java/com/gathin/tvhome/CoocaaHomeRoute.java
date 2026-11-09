package com.gathin.tvhome;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.lang.reflect.Method;

/** Reapplies this firmware's native Home route after it resets the target during boot. */
final class CoocaaHomeRoute {
    interface Properties {
        String get(String key) throws Exception;
        void set(String key, String value) throws Exception;
    }

    private static final class SystemProperties implements Properties {
        private final Method read;
        private final Method write;

        SystemProperties() throws ReflectiveOperationException {
            Class<?> type = Class.forName("android.os.SystemProperties");
            read = type.getMethod("get", String.class);
            write = type.getMethod("set", String.class, String.class);
        }

        public String get(String key) throws Exception {
            return (String) read.invoke(null, key);
        }

        public void set(String key, String value) throws Exception {
            write.invoke(null, key, value);
        }
    }

    private CoocaaHomeRoute() {
    }

    static void apply(Context context) {
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.M
                || !context.getResources().getBoolean(R.bool.coocaa_native_home)) return;
        try {
            configure(true, context.getPackageName(), new SystemProperties());
        } catch (Exception unavailable) {
            // Other firmware may refuse property writes; Home must still open normally.
            Log.w("CoocaaHomeRoute", "Native Home route unavailable", unavailable);
        }
    }

    static void configure(boolean enabled, String packageName, Properties properties) throws Exception {
        if (!enabled) return;
        String activity = "com.gathin.tvhome.MainActivity";
        if (!activity.equals(properties.get("persist.service.homepage.cls"))) {
            properties.set("persist.service.homepage.cls", activity);
        }
        if (!packageName.equals(properties.get("persist.service.homepage.pkg"))) {
            properties.set("persist.service.homepage.pkg", packageName);
        }
    }
}
