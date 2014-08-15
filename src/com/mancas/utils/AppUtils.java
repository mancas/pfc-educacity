package com.mancas.utils;

import java.util.List;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

public class AppUtils
{
    public static ResolveInfo getPreferredApp(Intent intent, PackageManager packageManager)
    {
        //First we need to check if there is a default app to perform the intent
        final ResolveInfo defaultResolution =
          packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (defaultResolution != null) {
            final ActivityInfo activity = defaultResolution.activityInfo;
            if (!activity.name.equals("com.android.internal.app.ResolverActivity")) {
                return defaultResolution;
            }
        }
        
        //If not, we will return the first app available to perform the intent
        final List<ResolveInfo> activities =
          packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (!activities.isEmpty()) {
            return activities.get(0);
        }

        return null;
    }

    public static ResolveInfo getPreferredAppIfAvailable(Intent intent, PackageManager packageManager)
    {
        //First we need to check if there is a default app to perform the intent
        final ResolveInfo defaultResolution =
          packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (defaultResolution != null) {
            final ActivityInfo activity = defaultResolution.activityInfo;
            if (!activity.name.equals("com.android.internal.app.ResolverActivity")) {
                return defaultResolution;
            }
        }

        return null;
    }
}
