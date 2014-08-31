package com.mancas.dialogs;

import com.mancas.educacity.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

/**
 * Class to create a custom dialog fragment to alert the user
 * that gps setting is disabled and he has to enable it
 * @author Manuel Casas Barrado
 * @version 1.0
 */
public class EnableGPSDialog extends DialogFragment
{
    /**
     * Intent Tag to check the activity result
     */
    public static final int GPS_ENABLED = 1; 

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.enable_gps)
               .setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       //Start settings activity
                       Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                       startActivity(intent);
                   }
               })
               .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // User cancelled the dialog
                       dialog.dismiss();
                   }
               });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onDismiss(DialogInterface dialog)
    {
        super.onDismiss(dialog);
    }
}
