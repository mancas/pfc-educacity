package com.mancas.database;

import com.mancas.database.Image.ImageEntry;

import android.provider.BaseColumns;

/**
 * Class that define a contract for Site Table
 * @author Manuel Casas Barrado
 * @version 1.0
 */
public final class Site
{
    /**
     * Empty constructor to prevent someone from accidentally instantiating the contract class
     */
    public Site() {}

    public static abstract class SiteEntry implements BaseColumns
    {
        public static final String TABLE_NAME = "sites";
        public static final String COLUMN_NAME = "name";
        public static final String TABLE_NAME_WITH_PREFIX = DBHelper.DB_PREFIX + SiteEntry.TABLE_NAME;
        public static final String[] TABLE_PROJECTION = {
            SiteEntry._ID,
            SiteEntry.COLUMN_NAME,
        };
        public static final String DEFUALT_TABLE_ORDER = SiteEntry.COLUMN_NAME + " DESC";
        public static final String DEFAULT_TABLE_SELECTION = SiteEntry._ID + "=?";
    }
}
