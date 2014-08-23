package com.mancas.utils;

import java.io.File;
import java.util.List;

import com.mancas.educacity.SettingsActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Class with some useful methods to treat with system applications
 * @author Manuel Casas Barrado
 * @version 1.0
 */
public class AppUtils
{
    /**
     * Tag for store the account ID in android saved preferences in order
     * to improve performance when updating database entries
     */
    public static final String ACCOUNT_ID_KEY = "account.id.key";
    /**
     * Tag for store the account image ID in android saved preferences in order
     * to improve performance when updating database entries
     */
    public static final String ACCOUNT_IMAGE_ID_KEY = "account.image.id.key";
    /**
     * Tag for handle camera pick picture action
     */
    public static final int TAKE_IMAGE_FROM_CAMERA = 1;
    /**
     * Tag for handle gallery pick picture action
     */
    public static final int TAKE_IMAGE_FROM_GALLERY = 2;

    /**
     * Method that check if there is a preferred application to perform the intent
     * given in the intent argument
     * @param intent action to perform
     * @param packageManager a reference to the PackageManager of the application
     * @return the ResolveInfo object relative to the application which is going to perform
     * the action specified in the intent argument
     */
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

    /**
     * Method that check if there is a preferred application to perform the intent
     * given in the intent argument
     * @param intent action to perform
     * @param packageManager a reference to the PackageManager of the application
     * @return the ResolveInfo object relative to the application which is going to perform
     * the action specified in the intent argument or null if there is no preferred application
     */
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

    /**
     * Shows the progress UI and hides the form.
     * @param context the context of the application
     * @param status view to show the user the status progress
     * @param form view of the form which is going to be processed
     * @param show if true, status progress will be shown
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public static void showProgress(Context context, final View status, final View form, final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = context.getResources().getInteger(android.R.integer.config_shortAnimTime);

            status.setVisibility(View.VISIBLE);
            status.animate().setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            status.setVisibility(show ? View.VISIBLE
                                    : View.GONE);
                        }
                    });

            form.setVisibility(View.VISIBLE);
            form.animate().setDuration(shortAnimTime)
                    .alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            form.setVisibility(show ? View.GONE
                                    : View.VISIBLE);
                        }
                    });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            status.setVisibility(show ? View.VISIBLE : View.GONE);
            form.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Retrieve from Android Shared Preferences the account ID.
     * @param context the context of the application
     * @return the current account ID, or -1 if ID is not defined
     */
    public static long getAccountID(Context context)
    {
        // Read in the flag indicating the account ID
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        return sp.getLong(ACCOUNT_ID_KEY, -1);
    }

    /**
     * Set the new current account ID
     * @param context the context of the application
     * @param id the ID of the account
     */
    public static void setAccountID(Context context, long id)
    {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = sp.edit();
        editor.putLong(ACCOUNT_ID_KEY, id);
        editor.commit();
    }

    /**
     * Retrieve from Android Shared Preferences the account image ID.
     * @param context the context of the application
     * @return the current account image ID, or -1 if ID is not defined
     */
    public static long getAccountImageID(Context context)
    {
        // Read in the flag indicating the account ID
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        return sp.getLong(ACCOUNT_IMAGE_ID_KEY, -1);
    }

    /**
     * Set the new current account ID
     * @param context the context of the application
     * @param id the ID of the account image
     */
    public static void setAccountImageID(Context context, long id)
    {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = sp.edit();
        editor.putLong(ACCOUNT_IMAGE_ID_KEY, id);
        editor.commit();
    }

    /**
     * Read from Android Shared Preferences the setting key and returns his current value.
     * @param context the context of the application
     * @param key the shared preference key
     * @param type class of the shared preference
     * @return the current value of the shared preference specified in key argument
     */
    public static String readPreference(Context context, String key, int type)
    {
        // Read in the flag indicating the account ID
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        switch (type){
        case SettingsActivity.BOOL_TYPE:
            return String.valueOf(sp.getBoolean(key, true));
        case SettingsActivity.STRING_TYPE:
        default:
            return sp.getString(key, "");
            
        }
    }

    /**
     * Configure an intent to crop an square image
     * @param intent the intent to configure
     * @return the configured intent
     */
    public static Intent configureCropIntent(Intent intent)
    {
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);

        return intent;
    }

    /**
     * Send a broadcast in order to add the new image to the gallery
     * @param activity
     * @param newImage the image path to add to the gallery
     */
    public static void galleryAddPicture(Activity activity, String newImage) {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(newImage);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        activity.sendBroadcast(mediaScanIntent);
    }
}
