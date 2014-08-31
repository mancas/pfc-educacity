package com.mancas.educacity;

import com.mancas.utils.AppUtils;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;

public class ProximityIntentReceiver extends BroadcastReceiver {
    
    private static final int NOTIFICATION_ID = 1000;

    @Override
    public void onReceive(Context context, Intent intent) {
        
        String key = LocationManager.KEY_PROXIMITY_ENTERING;

        Boolean entering = intent.getBooleanExtra(key, false);
        String notificationPreference = AppUtils.readPreference(context, SettingsActivity.NOTIICATIONS_KEY, SettingsActivity.BOOL_TYPE);

        if (entering && Boolean.getBoolean(notificationPreference)) {
            Log.d(getClass().getSimpleName(), "entering");

            NotificationManager notificationManager = 
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            String title = intent.getStringExtra(SaveStateMapFragment.PROXIMITY_ALERT_TITLE);

            NotificationCompat.Builder notification = createNotification(context, title);

            notificationManager.notify(NOTIFICATION_ID, notification.build());
        }
    }
    
    private Builder createNotification(Context context, String title) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                //.setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(title);
        String uri = AppUtils.readPreference(context, SettingsActivity.NOTIFICATIONS_RINGTONE_KEY, SettingsActivity.STRING_TYPE);
        mBuilder.setSound(Uri.parse(uri));
        boolean vibrate = Boolean.getBoolean(
                AppUtils.readPreference(context, SettingsActivity.NOTIFICATIONS_VIBRATE_KEY, SettingsActivity.BOOL_TYPE));
        if (vibrate)
            mBuilder.setVibrate(SettingsActivity.VIBRATE_PATTERN);

        return mBuilder;
    }
}