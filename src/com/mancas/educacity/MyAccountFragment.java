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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mancas.album.storage.AlbumStorageDirFactory;
import com.mancas.album.storage.BaseAlbumDirFactory;
import com.mancas.album.storage.FroyoAlbumDirFactory;
import com.mancas.database.Account.AccountEntry;
import com.mancas.database.DBHelper;
import com.mancas.database.DBTaskQuery;
import com.mancas.database.DBHelper.DBHelperCallback;
import com.mancas.database.Image.ImageEntry;
import com.mancas.dialogs.EditProfileNameDialog;
import com.mancas.dialogs.EditProfileNameDialog.EditProfileDialogCallbacks;
import com.mancas.dialogs.PickPictureDialog;
import com.mancas.dialogs.PickPictureDialog.PickPictureCallbacks;
import com.mancas.utils.AppUtils;
import com.mancas.utils.Utils;

public class MyAccountFragment extends Fragment
  implements PickPictureDialog.PickPictureCallbacks,
  EditProfileNameDialog.EditProfileDialogCallbacks, DBHelperCallback
  {
    /**
     * Debug Tag for use logging debug output to LogCat
     */
    public static final String TAG = "MyAccountFragment";
    /**
     * {@link ImageView} where the profile image will be placed
     */
    private ImageView mProfileImage;
    /**
     * {@link TextView} where the user name will be displayed
     */
    private TextView mProfileName;
    /**
     * {@link ImageButton} the user will click to change his name
     */
    private ImageButton mEditNameBtn;

    /**
     * The album of the application where stores images
     */
    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    /**
     * Current profile image path
     */
    private String mImageProfilePath;
    /**
     * A reference to this object which is going to handler {@link PickPictureCallbacks}
     */
    private final PickPictureCallbacks mPickPictureCallbacks = this;
    /**
     * A reference to this object which is going to handler {@link EditProfileDialogCallbacks}
     */
    private final EditProfileDialogCallbacks mEditProfileCallbacks = this;
    /**
     * A instance of the listener which will take care of handle notifications of this class
     */
    private MyAccountCallbacks mAccountListener;
    /**
     * Current user name
     */
    private String mCurrentProfileName;
    /**
     * Tag used for save in a bundle the current profile image
     */
    private final String PROFILE_IMAGE_KEY = "profile.image.key";
    /**
     * Tag used for save in a bundle the current user name
     */
    private final String PROFILE_NAME_KEY = "profile.name.key";
    /**
     * An instance of {@link DBHelper} used to manage changes in user data
     */
    private DBHelper mDatabaseManager;

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

        mDatabaseManager = DBHelper.getInstance(getActivity().getApplicationContext(), this);
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
        getCurrentProfileName();
        //Get the user profile image
        getCurrentProfileImage();
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
            case AppUtils.TAKE_IMAGE_FROM_CAMERA:
                Utils.setSquarePicture(mImageProfilePath, mProfileImage);
                break;
            case AppUtils.TAKE_IMAGE_FROM_GALLERY:
                Utils.setSquarePicture(mImageProfilePath, mProfileImage);
                break;
            }
            AppUtils.galleryAddPicture(getActivity(), mImageProfilePath);
            mAccountListener.updateProfileImage(mImageProfilePath);
        }
    }

    /**
     *  Photo album for this application
     **/
    private String getAlbumName() {
        return getString(R.string.album_name);
    }

    /**
     * Handler of the camera take photo action
     */
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
                Log.e(TAG, ex.getMessage());
                final Toast toast = new Toast(getActivity().getApplicationContext());
                toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.BOTTOM, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setText(R.string.error_file_create);
                toast.show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent = AppUtils.configureCropIntent(takePictureIntent);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                final ResolveInfo app =
                  AppUtils.getPreferredAppIfAvailable(takePictureIntent, getActivity().getPackageManager());

                if (app != null) {
                    takePictureIntent.setClassName(app.activityInfo.packageName, app.activityInfo.name);
                }

                this.startActivityForResult(takePictureIntent, AppUtils.TAKE_IMAGE_FROM_CAMERA);
            }
        }
    }

    /**
     * Handler of the galery import photo action
     */
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
                Log.e(TAG, ex.getMessage());
                final Toast toast = new Toast(getActivity().getApplicationContext());
                toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.BOTTOM, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setText(R.string.error_file_create);
                toast.show();
            }
            takePictureIntent = AppUtils.configureCropIntent(takePictureIntent);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(photoFile));
            final ResolveInfo app =
              AppUtils.getPreferredAppIfAvailable(takePictureIntent, getActivity().getPackageManager());
    
            if (app != null) {
                takePictureIntent.setClassName(app.activityInfo.packageName, app.activityInfo.name);
            }

            this.startActivityForResult(takePictureIntent, AppUtils.TAKE_IMAGE_FROM_GALLERY);
        }
    }

    /**
     * Callbacks to notify changes in the user account
     * @author Manuel Casas Barrado
     * @version 1.0
     */
    public static interface MyAccountCallbacks {
        public void updateAccountName(String name);
        public void updateProfileImage(String path);
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

    /**
     * Retrieve the current profile name from database
     */
    public void getCurrentProfileName()
    {
        DBTaskQuery task = new DBTaskQuery(AccountEntry.TABLE_NAME_WITH_PREFIX, AccountEntry.TABLE_PROJECTION,
                null, null, null, null, AccountEntry.DEFUALT_TABLE_ORDER);
        String name = mDatabaseManager.getProfileName(task);

        if (TextUtils.isEmpty(name)) {
            name = getResources().getString(R.string.profile_name_default);
        }
        mCurrentProfileName = name;
        mProfileName.setText(name);
    }

    /**
     * Retrieve the current profile image path from database
     */
    public void getCurrentProfileImage()
    {
        long image_id = AppUtils.getAccountImageID(getActivity().getApplicationContext());
        String[] selectArgs = {String.valueOf(image_id)};
        DBTaskQuery task = new DBTaskQuery(ImageEntry.TABLE_NAME_WITH_PREFIX, ImageEntry.TABLE_PROJECTION,
                ImageEntry.DEFAULT_TABLE_SELECTION, selectArgs, null, null, ImageEntry.DEFUALT_TABLE_ORDER);
        String path = mDatabaseManager.getProfileImagePath(task);
        if (TextUtils.isEmpty(path)) {
            getResources().getString(R.string.profile_image_default);
        }

        mImageProfilePath = path;
        if (mImageProfilePath != getActivity().getResources().getString(R.string.profile_image_default)) {
            Utils.setSquarePicture(mImageProfilePath, mProfileImage);
        }
    }

    @Override
    public void onDatabaseOpen(SQLiteDatabase database) {
    }

    @Override
    public void onSelectReady(Cursor data) {
    }

    @Override
    public void onInsertReady(long id) {
    }

    @Override
    public void onUpdateReady(int rows) {
    }
}
