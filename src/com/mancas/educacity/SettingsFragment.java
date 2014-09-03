package com.mancas.educacity;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.RingtonePreference;
import android.util.Log;

import com.mancas.database.Account.AccountEntry;
import com.mancas.database.DBHelper;
import com.mancas.database.DBHelper.DBHelperCallback;
import com.mancas.database.DBTaskUpdate;

public class SettingsFragment extends PreferenceFragment
    implements OnSharedPreferenceChangeListener, DBHelperCallback  {
    
    public static final String KEY_PREF_AUTO_SYNC = "pref_key_account_auto_sync";
    public static final String TAG = "Settings Fragment";
    private Context mContext;
    private DBHelperCallback mCallback = this;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.educacity_preferences);
        mContext = getActivity().getApplicationContext();
        bindOnPreferenceChange(findPreference(SettingsActivity.NOTIFICATIONS_RINGTONE_KEY));
        findPreference(SettingsActivity.PUBLIC_PROFILE_KEY).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                DBHelper helper = DBHelper.getInstance(mContext, mCallback);
                Boolean publicProfile = (Boolean) newValue;
                ContentValues values = new ContentValues();
                values.put(AccountEntry.COLUMN_PUBLIC, publicProfile ? 1 : 0);
                values.put(AccountEntry.COLUMN_SYNC, 0);
                DBTaskUpdate task = new DBTaskUpdate(AccountEntry.TABLE_NAME_WITH_PREFIX, values, null, null);
                helper.new AsyncUpdate().execute(task);
                return true;
            }
        });
    }
    
    private void bindOnPreferenceChange(Preference preference) {
        preference.setOnPreferenceChangeListener(sBindPreferenceChangeListener);
        sBindPreferenceChangeListener.onPreferenceChange(preference,
                getPreferenceManager().getSharedPreferences().getString(SettingsActivity.NOTIFICATIONS_RINGTONE_KEY, ""));
    }

    public void onResume()
    {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }
    
    public void onPause()
    {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
            String key) {
        Log.d(TAG, key);
        if (key.equals(SettingsActivity.AUTO_SYNC_KEY)) {
            ListPreference sAutoSync = (ListPreference) findPreference(key);
            sAutoSync.setSummary(sAutoSync.getEntry().toString());
        } else {
            if (key.equals(SettingsActivity.NOTIFICATIONS_RINGTONE_KEY)) {
                RingtonePreference sNotificationRingtone = (RingtonePreference) findPreference(key);
                Ringtone ringtone = RingtoneManager.getRingtone(sNotificationRingtone.getContext(),
                        Uri.parse(sharedPreferences.getString(key, "")));
                Log.d(TAG, ringtone.getTitle(sNotificationRingtone.getContext()));
                sNotificationRingtone.setSummary(ringtone.getTitle(sNotificationRingtone.getContext()));
            }
        }
    }
    
    private static Preference.OnPreferenceChangeListener sBindPreferenceChangeListener =
        new Preference.OnPreferenceChangeListener() {
        
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (preference instanceof RingtonePreference) {
                String value = newValue.toString();
                
                Ringtone ringtone = RingtoneManager.getRingtone(
                        preference.getContext(), Uri.parse(value));

                if (ringtone == null) {
                    // Clear the summary if there was a lookup error.
                    preference.setSummary(null);
                } else {
                    // Set the summary to reflect the new ringtone display
                    // name.
                    String sRingtoneName = ringtone.getTitle(preference.getContext());
                    preference.setSummary(sRingtoneName);
                }
            }
            return true;
        }
    };

    @Override
    public void onDatabaseOpen(SQLiteDatabase database) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onSelectReady(Cursor data) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onInsertReady(long id) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onUpdateReady(int rows) {
        // TODO Auto-generated method stub
        
    }
}
