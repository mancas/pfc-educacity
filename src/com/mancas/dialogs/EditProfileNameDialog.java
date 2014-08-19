package com.mancas.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.mancas.educacity.R;

/**
 * Class to create a custom dialog fragment with a text field
 * to let user edit his name
 * @author Manuel Casas Barrado
 * @version 1.0
 */
public class EditProfileNameDialog extends DialogFragment
{
    /**
     * Dialog listener who implements the necessary callbacks
     */
    private static EditProfileDialogCallbacks mCallbacks;
    /**
     * Edit text view where the user will write his name
     */
    private EditText mNewName;
    /**
     * Current user name
     */
    private static String mCurrentName;

    /**
     * Method that return a new instance of EditProfileDialog
     * @param callbacks an instance of the class which is going to handle events
     * @param currentName the current user profile name
     * @return an instance of EditProfileDialog
     */
    public static EditProfileNameDialog newInstance(EditProfileDialogCallbacks callbacks,
      String currentName)
    {
        EditProfileNameDialog dialog = new EditProfileNameDialog();
        mCallbacks = callbacks;
        mCurrentName = currentName;
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get LayoutInflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.educacity_edit_field, null);
        mNewName = (EditText) rootView.findViewById(R.id.edit_profile_name);

        // Need to set the current name
        mNewName.setText(mCurrentName);

        builder.setView(rootView);
        builder.setTitle(R.string.profile_name);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       //Notify new name
                       mCallbacks.onProfileNameChange(mNewName.getText().toString());
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

    /**
     * Callback interface used to listen for changes in the profile name
     * The handle class must implement it
     * @author Manuel Casas Barrado
     * @version 1.0
     */
    public static interface EditProfileDialogCallbacks {
        public void onProfileNameChange(String name);
    }
}
