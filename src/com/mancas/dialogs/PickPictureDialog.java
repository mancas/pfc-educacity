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

/**
 * Class to create a custom dialog fragment to prompt the user
 * how to complete the task of taking a picture
 * @author Manuel Casas Barrado
 * @version 1.0
 */
public class PickPictureDialog extends DialogFragment
{
    /**
     * Layout where the user touch to take a photo with the camera
     */
    private RelativeLayout mCameraLayout;
    /**
     * Layout where the user touch to pick a photo from gallery
     */
    private RelativeLayout mGalleryLayout;
    /**
     * Dialog listener who implements the necessary callbacks
     */
    static PickPictureCallbacks mCallbacks;
    /**
     * A reference to this object
     */
    private final PickPictureDialog mDialog = this;
    /**
     * Title of the dialog
     */
    private static String mTitle;

    /**
     * Method that returns a new instance of PickPictureDialog
     * @param callbacks an instance of the class which is going to handle events
     * @return an instance of {@link PickPictureDialog}
     */
    public static PickPictureDialog newInstance(PickPictureCallbacks callbacks)
    {
        PickPictureDialog dialog = new PickPictureDialog();
        mCallbacks = callbacks;
        return dialog;
    }

    /**
     * Method that returns a new instance of PickPictureDialog and set
     * the title of the dialog
     * @param callbacks an instance of the class which is going to handle events
     * @param title the title of the dialog
     * @return an instance of {@link PickPictureDialog}
     */
    public static PickPictureDialog newInstance(PickPictureCallbacks callbacks,
            String title) {
        mTitle = title;
        return newInstance(callbacks);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.educacity_pick_picture, container, false);
        //Set dialog title
        if (mTitle != null) {
            getDialog().setTitle(mTitle);
        } else {
            getDialog().setTitle(R.string.profile_image);
        }

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

    @Override
    public void onDismiss(DialogInterface dialog)
    {
        super.onDismiss(dialog);
    }

    /**
     * Callback interface used to listen for touch events in the layout
     * @author Manuel Casas Barrado
     * @version 1.0
     */
    public static interface PickPictureCallbacks {
        public void onCameraBtnClick(DialogFragment dialog);
        public void onGalleryBtnClick(DialogFragment dialog);
    }

    /**
     * Method that fill the camera layout with the information relative to
     * preferred camera or if there is not default camera, the first application
     * which can handle the request
     * @param imageView view where the application icon will be placed
     * @param textView view where the application label will be placed
     */
    private void setCameraInfo(ImageView imageView, TextView textView)
    {
        PackageManager packageManager = getActivity().getPackageManager();
        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ResolveInfo app = AppUtils.getPreferredApp(intent, packageManager);
        setLabelAndImage(app, imageView, textView);
    }

    /**
     * Method that fill the camera layout with the information relative to
     * preferred gallery or if there is not default gallery, the first application
     * which can handle the request
     * @param imageView view where the application icon will be placed
     * @param textView view where the application label will be placed
     */
    private void setGalleryInfo(ImageView imageView, TextView textView)
    {
        PackageManager packageManager = getActivity().getPackageManager();
        final Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        ResolveInfo app = AppUtils.getPreferredApp(intent, packageManager);
        setLabelAndImage(app, imageView, textView);
    }

    /**
     * Method that set application label and icon in their views
     * @param app ResolveInfo object of the application
     * @param imageView view where the application icon will be placed
     * @param textView view where the application label will be placed
     */
    private void setLabelAndImage(ResolveInfo app,
      ImageView imageView, TextView textView)
    {
        PackageManager packageManager = getActivity().getPackageManager();
        imageView.setImageDrawable(app.loadIcon(packageManager));
        textView.setText(app.loadLabel(packageManager));
    }
}
