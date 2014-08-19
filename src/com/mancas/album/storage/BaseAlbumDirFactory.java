package com.mancas.album.storage;

import java.io.File;

import android.os.Environment;

/**
 * Class to retrieve the File object associated with an album folder
 * This class is only used when API level is greater than 8 (Froyo 2.2)
 * @author Manuel Casas Barrado
 * @version 1.0
 */
public final class BaseAlbumDirFactory extends AlbumStorageDirFactory {

    /**
     * Standard storage location for digital camera files
     */
    private static final String CAMERA_DIR = "/dcim/";

    @Override
    public File getAlbumStorageDir(String albumName) {
        return new File (
                Environment.getExternalStorageDirectory()
                + CAMERA_DIR
                + albumName
        );
    }
}

