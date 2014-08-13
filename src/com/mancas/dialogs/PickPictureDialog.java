package com.mancas.dialogs;

import java.util.List;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mancas.educacity.R;

public class PickPictureDialog extends DialogFragment
{
    //private Drawable mCameraIcon;
    private RelativeLayout mCameraLayout, mGalleryLayout;
    static PickPictureListener mListener;
    private PickPictureDialog mDialog;

    public static PickPictureDialog newInstance(PickPictureListener listener)
    {
        PickPictureDialog dialog = new PickPictureDialog();
        mListener = listener;
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

    public interface PickPictureListener {
        public void onCameraBtnClick(DialogFragment dialog);
        public void onGalleryBtnClick(DialogFragment dialog);
    }
    
    private void setCameraInfo(ImageView imageView, TextView textView)
    {
        PackageManager pm = getActivity().getPackageManager();
        final Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final List<ResolveInfo> activities = pm.queryIntentActivities(i, 0);
Log.d("DIALOG", "" + activities.size());
        setLabelAndImage(activities, imageView, textView);
    }

    private void setGalleryInfo(ImageView imageView, TextView textView)
    {
        PackageManager pm = getActivity().getPackageManager();
        final Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        final List<ResolveInfo> activities = pm.queryIntentActivities(i, 0);
Log.d("DIALOG", "" + activities.size());
        setLabelAndImage(activities, imageView, textView);
    }
    
    private void setLabelAndImage(List<ResolveInfo> activities,
      ImageView imageView, TextView textView)
    {
        PackageManager pm = getActivity().getPackageManager();
        for (ResolveInfo resolveInfo : activities) {
            imageView.setImageDrawable(resolveInfo.loadIcon(pm));
            textView.setText(resolveInfo.loadLabel(pm));
        }
    }
}
