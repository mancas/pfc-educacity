package com.mancas.album.storage;

import java.io.File;

/**
 * Class to retrieve the File object associated with an album folder
 * @author Manuel Casas Barrado
 * @version 1.0
 */
public abstract class AlbumStorageDirFactory
{
    /**
     * Method that returns the File object associated to a specific album name
     * @param albumName the desired album folder name
     * @return the File object relative to the albumName argument
     */
    public abstract File getAlbumStorageDir(String albumName);
}
