package com.mancas.educacity;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;

public class ProximityIntentReceiver extends BroadcastReceiver {
    
    private static final int NOTIFICATION_ID = 1000;

    @Override
    public void onReceive(Context context, Intent intent) {
        
        String key = LocationManager.KEY_PROXIMITY_ENTERING;

        Boolean entering = intent.getBooleanExtra(key, false);

        if (entering) {
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

        return mBuilder;
    }
}