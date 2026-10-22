package com.gathin.tvhome;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Prefer TV entry points, then include ordinary launcher apps without duplicate packages. */
final class AppCatalog {
    static List<ResolveInfo> discover(Context context) {
        return discover(context, context.getPackageManager().queryIntentActivities(
                new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER), 0));
    }

    static List<ResolveInfo> discover(Context context, List<ResolveInfo> leanback) {
        PackageManager pm = context.getPackageManager();
        Map<String, ResolveInfo> apps = new LinkedHashMap<>();
        for (String category : new String[]{Intent.CATEGORY_LEANBACK_LAUNCHER, Intent.CATEGORY_LAUNCHER}) {
            for (ResolveInfo ri : Intent.CATEGORY_LEANBACK_LAUNCHER.equals(category) ? leanback
                    : pm.queryIntentActivities(new Intent(Intent.ACTION_MAIN).addCategory(category), 0)) {
                if (ri.activityInfo == null || !ri.activityInfo.exported || !ri.activityInfo.enabled) continue;
                String pkg = ri.activityInfo.packageName;
                if (!pkg.equals(context.getPackageName()) && !apps.containsKey(pkg)) apps.put(pkg, ri);
            }
        }
        try {
            ActivityInfo activity = pm.getActivityInfo(new ComponentName(AppVisibility.USB_PACKAGE,
                    "com.coocaa.filemanager.MainActivity"), 0);
            if (activity.exported && activity.enabled && activity.applicationInfo.enabled) {
                ResolveInfo usb = new ResolveInfo();
                usb.activityInfo = activity;
                apps.put(AppVisibility.USB_PACKAGE, usb);
            }
        } catch (PackageManager.NameNotFoundException ignored) {
            // Other TV brands have no Coocaa file manager.
        }
        return new ArrayList<>(apps.values());
    }
}
