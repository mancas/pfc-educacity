package com.mancas.educacity;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mancas.album.storage.AlbumStorageDirFactory;
import com.mancas.album.storage.BaseAlbumDirFactory;
import com.mancas.album.storage.FroyoAlbumDirFactory;
import com.mancas.dialogs.PickPictureDialog;
import com.mancas.dialogs.PickPictureDialog.PickPictureListiner;
import com.mancas.utils.Utils;

public class MyAccountFragment extends Fragment
  implements PickPictureDialog.PickPictureListiner
  {
    public static final String TAG = "AccountFragment";
    private ImageView mProfileImage;

    public static final int TAKE_IMAGE_FROM_CAMERA = 1;
    public static final int TAKE_IMAGE_FROM_GALLERY = 2;
    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    private String mImageProfilePath;
    private final PickPictureListiner mPickPictureListener = this;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.educacity_my_account, container, false);
        mProfileImage = (ImageView) rootView.findViewById(R.id.image_profile);
        mProfileImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                PickPictureDialog dialog = PickPictureDialog.newInstance(mPickPictureListener);
                dialog.show(getFragmentManager(), TAG);
            }
        });

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            Fragment fragment =  getActivity().getFragmentManager().findFragmentById(R.id.container);
            if (fragment != null) getFragmentManager().beginTransaction().remove(fragment).commit();
        } catch (IllegalStateException e) {
            //handle this situation because you are necessary will get 
            //an exception here :-(
        	Log.d(TAG, e.getMessage());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == TAKE_IMAGE_FROM_CAMERA && resultCode == Activity.RESULT_OK) {
            Utils.setSquarePicture(mImageProfilePath, mProfileImage);
            galleryAddPicture();
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
                this.startActivityForResult(takePictureIntent, TAKE_IMAGE_FROM_CAMERA);
            }
        }
    }

    @Override
    public void onGalleryBtnClick(DialogFragment dialog)
    {
        // TODO Auto-generated method stub
        
    }
}
