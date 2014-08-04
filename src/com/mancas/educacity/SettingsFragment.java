package com.mancas.educacity;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.RingtonePreference;
import android.util.Log;

public class SettingsFragment extends PreferenceFragment
	implements OnSharedPreferenceChangeListener  {
	
	public static final String KEY_PREF_AUTO_SYNC = "pref_key_account_auto_sync";
	public static final String KEY_PREF_NOTIFICATIONS_RINGTONE = "pref_key_notifications_ringtone";
	public static final String TAG = "Settings Fragment";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.educacity_preferences);
        
        bindOnPreferenceChange(findPreference(KEY_PREF_NOTIFICATIONS_RINGTONE));
    }
	
	private void bindOnPreferenceChange(Preference preference) {
		preference.setOnPreferenceChangeListener(sBindPreferenceChangeListener);
		sBindPreferenceChangeListener.onPreferenceChange(preference,
				getPreferenceManager().getSharedPreferences().getString(KEY_PREF_NOTIFICATIONS_RINGTONE, ""));
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
		if (key.equals(KEY_PREF_AUTO_SYNC)) {
			ListPreference sAutoSync = (ListPreference) findPreference(key);
			sAutoSync.setSummary(sAutoSync.getEntry().toString());
		} else {
			if (key.equals(KEY_PREF_NOTIFICATIONS_RINGTONE)) {
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


}
