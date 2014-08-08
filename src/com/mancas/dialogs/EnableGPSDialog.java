package com.mancas.dialogs;

import com.mancas.educacity.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

public class EnableGPSDialog extends DialogFragment
{
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
                       getTargetFragment().startActivityForResult(intent, GPS_ENABLED);
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

    public void onDismiss(DialogInterface dialog)
    {
        super.onDismiss(dialog);
    }
}
