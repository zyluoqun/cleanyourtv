package com.gathin.tvhome;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.UserManager;
import android.view.Gravity;
import android.widget.TextView;

/** HOME must exist before credential storage is unlocked on this TCL firmware. */
public class BootHomeActivity extends Activity {
    private BroadcastReceiver unlocked;
    private boolean opening;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        TextView loading = new TextView(this);
        loading.setText(R.string.app_name);
        loading.setTextColor(Color.WHITE);
        loading.setBackgroundColor(Color.BLACK);
        loading.setTextSize(28);
        loading.setGravity(Gravity.CENTER);
        setContentView(loading);
        openWhenUnlocked();
        if (opening || Build.VERSION.SDK_INT < Build.VERSION_CODES.N) return;
        unlocked = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                openWhenUnlocked();
            }
        };
        registerReceiver(unlocked, new IntentFilter(Intent.ACTION_USER_UNLOCKED));
        // Unlock may have happened between the first check and registration.
        openWhenUnlocked();
    }

    @Override
    protected void onResume() {
        super.onResume();
        openWhenUnlocked();
    }

    private void openWhenUnlocked() {
        if (opening) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            UserManager users = getSystemService(UserManager.class);
            if (users == null || !users.isUserUnlocked()) return;
        }
        opening = true;
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        if (unlocked != null) {
            unregisterReceiver(unlocked);
            unlocked = null;
        }
        super.onDestroy();
    }
}
