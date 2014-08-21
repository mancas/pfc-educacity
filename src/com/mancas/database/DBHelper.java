package com.mancas.database;

import com.mancas.database.Account.AccountEntry;
import com.mancas.database.Image.ImageEntry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Helper class that contains useful methods to manage Educacity SQLite database
 * @author Manuel Casas Barrado
 * @version 1.0
 */

public class DBHelper
{
    /**
     * Tag for naming database
     */
    public static final String DB_NAME = "educacity.db";
    /**
     * Tag used to define the prefix of the tables
     */
    public static final String DB_PREFIX = "ecity_";
    /**
     * Tag that define database version
     */
    public static final int DB_VERSION = 5;
    /**
     * An instance of the open database
     */
    private SQLiteDatabase db;
    /**
     * An instance of {@link DBOpenHelper}
     */
    private DBOpenHelper dbOpenHelper;
    /**
     * Database helper who implements the necessary callbacks
     */
    private DBHelperCallback callbacks;

    /**
     * SQL sentence to delete all account entries
     */
    private static final String SQL_DELETE_ACCOUNT_ENTRIES =
            "DROP TABLE IF EXISTS " + AccountEntry.TABLE_NAME;
    /**
     * SQL sentence to delete all images entries
     */
    private static final String SQL_DELETE_IMAGES_ENTRIES =
            "DROP TABLE IF EXISTS " + ImageEntry.TABLE_NAME;

    /**
     * Debug Tag for use logging debug output to LogCat
     */
    public static final String TAG = "DBHelper";

    /**
     * Helper class to manage database creation and upgrade
     * @author Manuel Casas Barrado
     * @version 1.0
     */
    private static class DBOpenHelper extends SQLiteOpenHelper
    {
        private static final String DB_CREATE =
                "CREATE TABLE " + DBHelper.DB_PREFIX + AccountEntry.TABLE_NAME +
                " (" + AccountEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + AccountEntry.COLUMN_NAME + " TEXT NOT NULL," +
                AccountEntry.COLUMN_IMAGE + " TEXT," + AccountEntry.COLUMN_SYNC + " INTEGER DEFAULT 0);" +
                "CREATE TABLE " + DBHelper.DB_PREFIX + ImageEntry.TABLE_NAME +
                " (" + ImageEntry._ID + " INTEGER PRIMARY KEY," + ImageEntry.COLUMN_PATH + " TEXT NOT NULL," +
                ImageEntry.COLUMN_SITE_ID + " INTEGER," + ImageEntry.COLUMN_SYNC + " INTEGER DEFAULT 0);";

        /**
         * Create a helper object to create, open, and/or manage a database.
         * @param context the context of the application
         * @param dbName the name of the database
         * @param version the version of the database
         */
        public DBOpenHelper(Context context, String dbName, int version) {
            super(context, dbName, null, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d("DBHELPER", "onCreate");
            try {
                db.execSQL(DB_CREATE);
            } catch (SQLException e) {
                Log.e(DBHelper.TAG, e.getMessage());
            }
        }

        @Override
        public void onOpen(SQLiteDatabase db) {
            super.onOpen(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO modify this method to preserve data
            db.execSQL("DROP TABLE IF EXISTS " + DBHelper.DB_PREFIX + AccountEntry.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + DBHelper.DB_PREFIX + ImageEntry.TABLE_NAME);
            onCreate(db);
        }
    }

    /**
     * Private class that provide an asynchronous task to get a writable database
     * @author Manuel Casas Barrado
     * @version 1.0
     */
    private class AsyncGetDatabase extends AsyncTask<DBOpenHelper, Void, SQLiteDatabase>
    {
        @Override
        protected SQLiteDatabase doInBackground(DBOpenHelper... params)
        {
            DBOpenHelper helper = params[0];
            return helper.getWritableDatabase();
        }

        @Override
        protected void onPostExecute(SQLiteDatabase database) {
            db = database;
            callbacks.onDatabaseOpen(database);
        }
    }

    /**
     * Creates a helper to manage transitions to database
     * @param context the context of the application
     * @param an instance of the class which is going to handle events
     */
    public DBHelper(Context context, DBHelperCallback callbacks)
    {
        this.dbOpenHelper = new DBOpenHelper(context, DBHelper.DB_NAME, DBHelper.DB_VERSION);
        Log.d("DBHELPER", "new Object");
        this.callbacks = callbacks;
        establishDB();
    }

    /**
     * Establish a writable database
     */
    public void establishDB()
    {
        if (this.db == null)
            new AsyncGetDatabase().execute(dbOpenHelper);
        else
            callbacks.onDatabaseOpen(db);
    }

    /**
     * Close the currently open database
     */
    public void cleanUp()
    {
        if (this.db != null) {
            db.close();
            db = null;
        }
    }

    /**
     * Returns the currently open database instance
     * @return an instance of {@link SQLiteDatabase}
     */
    public SQLiteDatabase getDataBase()
    {
        return this.db;
    }

    /**
     * Callback interface used to listen for database events from asynchronous tasks
     * @author Manuel Casas Barrado
     * @version 1.0
     */
    public static interface DBHelperCallback {
        public void onDatabaseOpen(SQLiteDatabase database);
        //public void onDataReady(Cursor data);
    }

    public Cursor select(String table, String[] projection, String select,
            String[] selectArgs, String groupBy, String having, String orderBy)
    {
        if (db == null) {
            return null;
        }
        Cursor data = db.query(table, projection, select, selectArgs, groupBy, having, orderBy);

        return data;
    }

    public long insert(String table, String nullColumnHack, ContentValues values)
    {
        if (db == null) {
            return -1;
        }

        return db.insert(table, nullColumnHack, values);
    }

    public int update(String table, ContentValues values, String whereClause, String[] whereArgs)
    {
        if (db == null) {
            return -1;
        }

        return db.update(table, values, whereClause, whereArgs);
    }

    public Cursor query(String sql, String[] selectionArgs)
    {
        if (db == null) {
            return null;
        }
        Cursor data = db.rawQuery(sql, selectionArgs);

        return data;
    }
}

