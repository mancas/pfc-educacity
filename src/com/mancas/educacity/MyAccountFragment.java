package com.mancas.educacity;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mancas.album.storage.AlbumStorageDirFactory;
import com.mancas.album.storage.BaseAlbumDirFactory;
import com.mancas.album.storage.FroyoAlbumDirFactory;
import com.mancas.database.DBHelper;
import com.mancas.database.DBHelper.DBHelperCallback;
import com.mancas.dialogs.EditProfileNameDialog;
import com.mancas.dialogs.EditProfileNameDialog.EditProfileDialogCallbacks;
import com.mancas.dialogs.PickPictureDialog;
import com.mancas.dialogs.PickPictureDialog.PickPictureCallbacks;
import com.mancas.utils.AppUtils;
import com.mancas.utils.Utils;

public class MyAccountFragment extends Fragment
  implements PickPictureDialog.PickPictureCallbacks,
  EditProfileNameDialog.EditProfileDialogCallbacks
  {
    public static final String TAG = "AccountFragment";
    private ImageView mProfileImage;
    private TextView mProfileName;
    private ImageButton mEditNameBtn;

    public static final int TAKE_IMAGE_FROM_CAMERA = 1;
    public static final int TAKE_IMAGE_FROM_GALLERY = 2;
    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    private String mImageProfilePath;
    private final PickPictureCallbacks mPickPictureCallbacks = this;
    private final EditProfileDialogCallbacks mEditProfileCallbacks = this;
    private MyAccountCallbacks mAccountListener;
    private String mCurrentProfileName;
    private final String PROFILE_IMAGE_KEY = "profile.image.key";
    private final String PROFILE_NAME_KEY = "profile.name.key";


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mAccountListener = (MyAccountCallbacks) getActivity();
        } catch(ClassCastException ex) {
            Log.e(TAG, getActivity().toString() + " must implement MyAccountCallbacks");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }
        
        if (savedInstanceState != null) {
            mImageProfilePath = savedInstanceState.getString(PROFILE_IMAGE_KEY);
            mCurrentProfileName = savedInstanceState.getString(PROFILE_NAME_KEY);
        }
    }

    public void onResume()
    {
        super.onResume();
    }

    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putString(PROFILE_IMAGE_KEY, mImageProfilePath);
        outState.putString(PROFILE_NAME_KEY, mCurrentProfileName);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.educacity_my_account, container, false);
        mProfileImage = (ImageView) rootView.findViewById(R.id.image_profile);
        mProfileImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                PickPictureDialog dialog = PickPictureDialog.newInstance(mPickPictureCallbacks);
                dialog.show(getFragmentManager(), TAG);
            }
        });
        mProfileName = (TextView) rootView.findViewById(R.id.profile_name);
        mEditNameBtn = (ImageButton) rootView.findViewById(R.id.profile_edit_btn);
        mEditNameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditProfileNameDialog dialog =
                  EditProfileNameDialog.newInstance(mEditProfileCallbacks, mCurrentProfileName);
                dialog.show(getFragmentManager(), TAG);
            }
        });
        //Get the user name
        if (mCurrentProfileName == null) {
            mCurrentProfileName = mAccountListener.getCurrentProfileName();
        }
        mProfileName.setText(mCurrentProfileName);
        //Get the user profile image
        if (mImageProfilePath == null) {
            mImageProfilePath = mAccountListener.getCurrentProfileImage();
            if (mImageProfilePath != getActivity().getResources().getString(R.string.profile_image_default)) {
                Utils.setSquarePicture(mImageProfilePath, mProfileImage);
            }
        }
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == Activity.RESULT_OK) {
            switch(requestCode) {
            case TAKE_IMAGE_FROM_CAMERA:
                Utils.setSquarePicture(mImageProfilePath, mProfileImage);
                break;
            case TAKE_IMAGE_FROM_GALLERY:
                Utils.setSquarePicture(mImageProfilePath, mProfileImage);
                break;
            }
            galleryAddPicture();
            mAccountListener.updateProfileImage(mImageProfilePath);
        }
    }

    /* Photo album for this application */
    private String getAlbumName() {
        return getString(R.string.album_name);
    }

    private void galleryAddPicture() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mImageProfilePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
    }

    @Override
    public void onCameraBtnClick(DialogFragment dialog)
    {
        dialog.dismiss();
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = Utils.createImageFile(mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName()));
                mImageProfilePath = photoFile.getAbsolutePath();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.d("MY ACCOUNT", ex.getMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                takePictureIntent.putExtra("crop", "true");
                takePictureIntent.putExtra("aspectX", 1);
                takePictureIntent.putExtra("aspectY", 1);
                takePictureIntent.putExtra("outputX", 300);
                takePictureIntent.putExtra("outputY", 300);
                final ResolveInfo app =
                  AppUtils.getPreferredAppIfAvailable(takePictureIntent, getActivity().getPackageManager());

                if (app != null) {
                    takePictureIntent.setClassName(app.activityInfo.packageName, app.activityInfo.name);
                }

                this.startActivityForResult(takePictureIntent, TAKE_IMAGE_FROM_CAMERA);
            }
        }
    }

    @Override
    public void onGalleryBtnClick(DialogFragment dialog)
    {
        dialog.dismiss();
        Intent takePictureIntent = new Intent(Intent.ACTION_PICK);
        takePictureIntent.setType("image/*");
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = Utils.createImageFile(mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName()));
                mImageProfilePath = photoFile.getAbsolutePath();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.d("MY ACCOUNT", ex.getMessage());
            }
            takePictureIntent.putExtra("crop", "true");
            takePictureIntent.putExtra("aspectX", 1);
            takePictureIntent.putExtra("aspectY", 1);
            takePictureIntent.putExtra("outputX", 300);
            takePictureIntent.putExtra("outputY", 300);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(photoFile));
            final ResolveInfo app =
              AppUtils.getPreferredAppIfAvailable(takePictureIntent, getActivity().getPackageManager());
    
            if (app != null) {
                takePictureIntent.setClassName(app.activityInfo.packageName, app.activityInfo.name);
            }

            this.startActivityForResult(takePictureIntent, TAKE_IMAGE_FROM_GALLERY);
        }
    }

    // Listener to update remote data
    public static interface MyAccountCallbacks {
        public void updateAccountName(String name);
        public void updateProfileImage(String path);
        public String getCurrentProfileName();
        public String getCurrentProfileImage();
    }

    @Override
    public void onProfileNameChange(String name) {
        if (name.length() == 0) {
            mProfileName.setText(R.string.profile_name_default);
        } else {
            mProfileName.setText(name);
        }
        mAccountListener.updateAccountName(name);
    }
}
