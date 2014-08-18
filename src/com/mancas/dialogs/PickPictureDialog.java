package com.mancas.dialogs;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mancas.educacity.R;
import com.mancas.utils.AppUtils;

public class PickPictureDialog extends DialogFragment
{
    //private Drawable mCameraIcon;
    private RelativeLayout mCameraLayout, mGalleryLayout;
    static PickPictureCallbacks mCallbacks;
    private PickPictureDialog mDialog;

    public static PickPictureDialog newInstance(PickPictureCallbacks callbacks)
    {
        PickPictureDialog dialog = new PickPictureDialog();
        mCallbacks = callbacks;
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDialog = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.educacity_pick_picture, container, false);
        //Set dialog title
        getDialog().setTitle(R.string.profile_image);

       //Set listener to layouts
        mCameraLayout = (RelativeLayout) rootView.findViewById(R.id.take_picture);
        mCameraLayout.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mCallbacks.onCameraBtnClick(mDialog);
            }
        });
        mGalleryLayout = (RelativeLayout) rootView.findViewById(R.id.pick_picture);
        mGalleryLayout.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mCallbacks.onGalleryBtnClick(mDialog);
            }
        });

        //Getting views to add info
        ImageView mImageViewCamera = (ImageView) rootView.findViewById(R.id.camera_image);
        ImageView mImageViewGallery = (ImageView) rootView.findViewById(R.id.gallery_image);

        TextView mTextViewCamera = (TextView) rootView.findViewById(R.id.camera_label);
        TextView mTextViewGallery = (TextView) rootView.findViewById(R.id.gallery_label);

        setCameraInfo(mImageViewCamera, mTextViewCamera);
        setGalleryInfo(mImageViewGallery, mTextViewGallery);

        return rootView;
    }

    public void onDismiss(DialogInterface dialog)
    {
        super.onDismiss(dialog);
    }

    public static interface PickPictureCallbacks {
        public void onCameraBtnClick(DialogFragment dialog);
        public void onGalleryBtnClick(DialogFragment dialog);
    }
    
    private void setCameraInfo(ImageView imageView, TextView textView)
    {
        PackageManager packageManager = getActivity().getPackageManager();
        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ResolveInfo app = AppUtils.getPreferredApp(intent, packageManager);
        setLabelAndImage(app, imageView, textView);
    }

    private void setGalleryInfo(ImageView imageView, TextView textView)
    {
        PackageManager packageManager = getActivity().getPackageManager();
        final Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        ResolveInfo app = AppUtils.getPreferredApp(intent, packageManager);
        setLabelAndImage(app, imageView, textView);
    }
    
    private void setLabelAndImage(ResolveInfo app,
      ImageView imageView, TextView textView)
    {
        PackageManager packageManager = getActivity().getPackageManager();
        imageView.setImageDrawable(app.loadIcon(packageManager));
        textView.setText(app.loadLabel(packageManager));
    }
}
