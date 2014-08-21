package com.mancas.database;

import com.mancas.database.Account.AccountEntry;

import android.provider.BaseColumns;

/**
 * Class that define a contract for Image Table
 * @author Manuel Casas Barrado
 * @version 1.0
 */
public final class Image
{
    /**
     * Empty constructor to prevent someone from accidentally instantiating the contract class
     */
    public Image() {}

    public static abstract class ImageEntry implements BaseColumns
    {
        public static final String TABLE_NAME = "images";
        public static final String COLUMN_SITE_ID = "site_id";
        public static final String COLUMN_PATH = "path";
        public static final String COLUMN_SYNC = "sync";
        public static final String TABLE_NAME_WITH_PREFIX = DBHelper.DB_PREFIX + ImageEntry.TABLE_NAME;
        public static final String[] TABLE_PROJECTION = {
            ImageEntry._ID,
            ImageEntry.COLUMN_SITE_ID,
            ImageEntry.COLUMN_PATH,
            ImageEntry.COLUMN_SYNC
        };
        public static final String DEFUALT_TABLE_ORDER = ImageEntry._ID + " ASC";
        public static final String DEFAULT_TABLE_SELECTION = ImageEntry._ID + "=?";
    }
}
