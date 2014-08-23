package com.mancas.educacity;

import android.app.Activity;
import android.os.Bundle;

public class SettingsActivity extends Activity {
    //Tags for settings key
    public static final String PUBLIC_PROFILE_KEY = "pref_key_account_public";
    public static final String AUTO_SYNC_KEY = "pref_key_account_auto_sync";
    public static final String NOTIICATIONS_KEY = "pref_key_notifications_enabled";
    public static final String NOTIFICATIONS_RINGTONE_KEY = "pref_key_notifications_ringtone";
    public static final String NOTIFICATIONS_VIBRATE_KEY = "pref_key_notifications_vibrate";
    public static final int BOOL_TYPE = 1;
    public static final int STRING_TYPE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
