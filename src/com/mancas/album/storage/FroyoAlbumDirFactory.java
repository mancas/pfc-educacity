package com.mancas.album.storage;

import java.io.File;

import android.os.Environment;

/**
 * Class to retrieve the File object associated with an album folder
 * This class is only used when API level is less than 8 (Froyo 2.2)
 * @author Manuel Casas Barrado
 * @version 1.0
 */
public final class FroyoAlbumDirFactory extends AlbumStorageDirFactory {

    @Override
    public File getAlbumStorageDir(String albumName) {
        // TODO Auto-generated method stub
        return new File(
          Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES
          ), 
          albumName
        );
    }
}
