package com.mancas.dialogs;

import java.util.List;

import com.mancas.educacity.MyAccountFragment;
import com.mancas.educacity.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class PickPictureDialog extends DialogFragment
{
    //private Drawable mCameraIcon;
    private RelativeLayout mCameraLayout, mGalleryLayout;
    static PickPictureListiner mListener;
    private PickPictureDialog mDialog;

    public static PickPictureDialog newInstance(PickPictureListiner listener)
    {
        PickPictureDialog dialog = new PickPictureDialog();
        mListener = listener;
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.profile_image);
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.educacity_pick_picture, null);
        builder.setView(rootView);

        mDialog = this;

        //TODO
        /*PackageManager pm = getActivity().getPackageManager();
        final Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final List<ResolveInfo> activities = pm.queryBroadcastReceivers(i, 0);
        for (ResolveInfo resolveInfo : activities) {
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            Log.d("DIALOG", resolveInfo.resolvePackageName);
            if (activityInfo != null) {
                //mCameraIcon = activityInfo.loadIcon(pm);
            }
        }
        
        ImageView iv = (ImageView) rootView.findViewById(R.id.camera_image);
        //iv.setImageDrawable(mCameraIcon);*/
        //Set listener to layouts
        mCameraLayout = (RelativeLayout) rootView.findViewById(R.id.take_picture);
        mCameraLayout.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mListener.onCameraBtnClick(mDialog);
            }
        });
        mGalleryLayout = (RelativeLayout) rootView.findViewById(R.id.pick_picture);
        mGalleryLayout.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mListener.onGalleryBtnClick(mDialog);
            }
        });

        // Create the AlertDialog object and return it
        return builder.create();
    }

    public void onDismiss(DialogInterface dialog)
    {
        super.onDismiss(dialog);
    }

    public interface PickPictureListiner {
        public void onCameraBtnClick(DialogFragment dialog);
        public void onGalleryBtnClick(DialogFragment dialog);
    }
}
