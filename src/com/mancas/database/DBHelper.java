package com.mancas.database;

import java.util.concurrent.ExecutionException;

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
    public static final int DB_VERSION = 10;
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
     * Keep track of the asynchronous task to ensure we can handle it if requested
     */
    private AsyncGetDatabase mGetDatabase = null;

    /**
     * Debug Tag for use logging debug output to LogCat
     */
    public static final String TAG = "DBHelper";
    /**
     * Flag for keep track of the asynchronous task status
     */
    private static boolean mWorking = false;
    private static volatile DBHelper instance = null;

    /**
     * Returns or create a new instance of DBHelper
     * @param context the context of the application
     * @param an instance of the class which is going to handle events
     */
    public static DBHelper getInstance(Context context, DBHelperCallback callbacks)
    {
        if (instance == null) {
            instance = new DBHelper(context, callbacks);
        } else {
            instance.configure(context, callbacks);
        }

        return instance;
        //establishDB();
    }
    
    /**
     * Method used for configure the current singleton instance
     * @param context the context of the application
     * @param callbacks the lister which will handle the events
     */
    private void configure(Context context, DBHelperCallback callbacks) {
        this.callbacks = callbacks;
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
    }

    /**
     * Helper class to manage database creation and upgrade
     * @author Manuel Casas Barrado
     * @version 1.0
     */
    public static class DBOpenHelper extends SQLiteOpenHelper
    {
        private static final String DB_TABLE_ACCOUNT =
                "CREATE TABLE " + DBHelper.DB_PREFIX + AccountEntry.TABLE_NAME +
                " (" + AccountEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + AccountEntry.COLUMN_NAME + " TEXT," +
                AccountEntry.COLUMN_EMAIL + " TEXT NOT NULL," +
                AccountEntry.COLUMN_IMAGE + " TEXT," + AccountEntry.COLUMN_SYNC + " INTEGER DEFAULT 0);";
        private static final String DB_TABLE_IMAGES = 
                "CREATE TABLE " + DBHelper.DB_PREFIX + ImageEntry.TABLE_NAME +
                " (" + ImageEntry._ID + " INTEGER PRIMARY KEY," + ImageEntry.COLUMN_PATH + " TEXT NOT NULL," +
                ImageEntry.COLUMN_SITE_ID + " INTEGER," + ImageEntry.COLUMN_PUBLIC + " INTEGER DEFAULT 1," +
                ImageEntry.COLUMN_SYNC + " INTEGER DEFAULT 0);";

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
            try {
                db.execSQL(DB_TABLE_ACCOUNT);
                db.execSQL(DB_TABLE_IMAGES);
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
        protected void onPreExecute() {
            super.onPreExecute();
            mWorking = true;
        }

        @Override
        protected SQLiteDatabase doInBackground(DBOpenHelper... params)
        {
            DBOpenHelper helper = params[0];
            if (db != null) {
                return db;
            }
            return helper.getWritableDatabase();
        }

        @Override
        protected void onPostExecute(SQLiteDatabase database) {
            db = database;
            mWorking = false;
            callbacks.onDatabaseOpen(database);
        }
    }

    /**
     * Establish a writable database
     */
    public void establishDB()
    {
        if (this.db == null) {
            mGetDatabase = new AsyncGetDatabase();
            try {
                mGetDatabase.execute(dbOpenHelper).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.d("DBHELPER", e.getMessage());
            } catch (ExecutionException e) {
                e.printStackTrace();
                Log.d("DBHELPER", e.getMessage());
            }
        } else {
            callbacks.onDatabaseOpen(db);
        }
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
     * Returns a helper to perform actions over the database
     * @return an instance of {@link DBOpenHelper}
     */
    public DBOpenHelper getDBOpenHelper()
    {
        return this.dbOpenHelper;
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
     * Set a new instance of the open database
     * @param database the current openly database
     */
    public void setDataBase(SQLiteDatabase database)
    {
        this.db = database;
    }

    /**
     * Callback interface used to listen for database events from asynchronous tasks
     * @author Manuel Casas Barrado
     * @version 1.0
     */
    public static interface DBHelperCallback {
        public void onDatabaseOpen(SQLiteDatabase database);
        public void onSelectReady(Cursor data);
        public void onInsertReady(long id);
        public void onUpdateReady(int rows);
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

    /**
     * Creates a new Educacity account
     * @param email the email of the user has used to register a new Educacity account
     * @param name the name of the user
     * @param profile_image path to the image file of the account
     * @return the new account ID
     * @throws InterruptedException if the asynchronous task that perform the database opening work has been interrupted
     * @throws ExecutionException if the asynchronous task that perform the database opening work has crashed
     */
    public long createNewAccount(String email, String name, String profile_image)
            throws InterruptedException, ExecutionException
    {
        ContentValues profileImage = new ContentValues();
        profileImage.put(ImageEntry.COLUMN_PATH, profile_image);
        profileImage.put(ImageEntry.COLUMN_SYNC, false);
        long image = insert(ImageEntry.TABLE_NAME_WITH_PREFIX, null, profileImage);
        if (image == -1) {
            return -1;
        }
        ContentValues account = new ContentValues();
        account.put(AccountEntry.COLUMN_EMAIL, email);
        account.put(AccountEntry.COLUMN_IMAGE, image);
        account.put(AccountEntry.COLUMN_SYNC, false);
        long id = insert(AccountEntry.TABLE_NAME_WITH_PREFIX, null, account);
        if (id != -1) {
            return id;
        }

        return -1;
    }

    public long getProfileImageId()
    {
        Cursor data = select(AccountEntry.TABLE_NAME_WITH_PREFIX, AccountEntry.TABLE_PROJECTION,
                null, null, null, null,  AccountEntry.DEFUALT_TABLE_ORDER);
        if (data != null && data.getCount() > 0) {
            data.moveToFirst();
            return data.getLong(data.getColumnIndex(AccountEntry.COLUMN_IMAGE));
        }

        return -1;
    }

    public String getProfileImagePath(DBTaskQuery task)
    {
        AsyncSelect selectTask = new AsyncSelect();
        try {
            Cursor data = selectTask.execute(task).get();
            if (data != null && data.getCount() > 0) {
                data.moveToFirst();
                return data.getString(data.getColumnIndex(ImageEntry.COLUMN_PATH));
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return "";
    }

    public String getProfileName(DBTaskQuery task)
    {
        AsyncSelect selectTask = new AsyncSelect();
        try {
            Cursor data = selectTask.execute(task).get();
            if (data != null && data.getCount() > 0) {
                data.moveToFirst();
                return data.getString(data.getColumnIndex(AccountEntry.COLUMN_NAME));
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return "";
    }

    /* ASYNCHRONOUS TASKS */
    /**
     * Private class that provide an asynchronous task to insert entries into database
     * @author Manuel Casas Barrado
     * @version 1.0
     */
    public class AsyncInsert extends AsyncTask<DBTaskInsert, Void, Long>
    {
        @Override
        protected Long doInBackground(DBTaskInsert... params)
        {
            DBTaskInsert insert = params[0];
            if (db == null) {
                db = getDBOpenHelper().getWritableDatabase();
            }

            return db.insert(insert.getTableName(), insert.getNullColumnHack(), insert.getValues());
        }

        @Override
        protected void onPostExecute(Long id) {
            callbacks.onInsertReady(id);
        }
    }

    /**
     * Private class that provide an asynchronous task to insert entries into database
     * @author Manuel Casas Barrado
     * @version 1.0
     */
    public class AsyncSelect extends AsyncTask<DBTaskQuery, Void, Cursor>
    {
        @Override
        protected Cursor doInBackground(DBTaskQuery... params)
        {
            DBTaskQuery select = params[0];
            if (db == null) {
                db = getDBOpenHelper().getWritableDatabase();
            }

            return db.query(select.getTableName(), select.getProjection(), select.getSelect(), select.getSelectArgs(),
                    select.getGroupBy(), select.getHaving(), select.getOrderBy());
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            callbacks.onSelectReady(cursor);
        }
    }

    /**
     * Private class that provide an asynchronous task to update entries
     * @author Manuel Casas Barrado
     * @version 1.0
     */
    public class AsyncUpdate extends AsyncTask<DBTaskUpdate, Void, Integer>
    {
        @Override
        protected Integer doInBackground(DBTaskUpdate... params)
        {
            DBTaskUpdate update = params[0];
            if (db == null) {
                db = getDBOpenHelper().getWritableDatabase();
            }

            return db.update(update.getTableName(), update.getValues(),
                    update.getWhereClause(), update.getWhereArgs());
        }

        @Override
        protected void onPostExecute(Integer rowsAffected) {
            callbacks.onUpdateReady(rowsAffected);
        }
    }
}

