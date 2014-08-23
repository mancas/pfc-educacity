package com.mancas.database;

import com.mancas.database.Image.ImageEntry;

import android.provider.BaseColumns;

/**
 * Class that define a contract for Account Table
 * @author Manuel Casas Barrado
 * @version 1.0
 */
public final class Account
{
    /**
     * Empty constructor to prevent someone from accidentally instantiating the contract class
     */
    public Account() {}

    public static abstract class AccountEntry implements BaseColumns
    {
        public static final String TABLE_NAME = "account";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_IMAGE = "image_id";
        public static final String COLUMN_SYNC = "sync";
        public static final String COLUMN_TOKEN = "token";
        public static final String TABLE_NAME_WITH_PREFIX = DBHelper.DB_PREFIX + AccountEntry.TABLE_NAME;
        public static final String[] TABLE_PROJECTION = {
            AccountEntry._ID,
            AccountEntry.COLUMN_EMAIL,
            AccountEntry.COLUMN_NAME,
            AccountEntry.COLUMN_IMAGE,
            AccountEntry.COLUMN_SYNC
        };
        public static final String DEFUALT_TABLE_ORDER = AccountEntry.COLUMN_NAME + " DESC";
        public static final String DEFAULT_TABLE_SELECTION = AccountEntry._ID + "=?";
        public static final String TABLE_JOIN_ACCOUNT_IMAGE_SQL = "SELECT * FROM " + AccountEntry.TABLE_NAME_WITH_PREFIX +
                " " + AccountEntry.TABLE_NAME + " INNER JOIN " + ImageEntry.TABLE_NAME_WITH_PREFIX + " " + ImageEntry.TABLE_NAME +
                " ON " + AccountEntry.TABLE_NAME + "." + AccountEntry.COLUMN_IMAGE + "=" + ImageEntry.TABLE_NAME +
                "." + ImageEntry._ID + " WHERE " + AccountEntry.TABLE_NAME + "." + AccountEntry._ID + "=?";
    }
}
