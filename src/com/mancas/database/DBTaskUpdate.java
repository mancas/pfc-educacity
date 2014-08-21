package com.mancas.database;

import android.content.ContentValues;

/**
 * {@inheritDoc}
 * @author Manuel Casas Barrado
 * @version 1.0
 */
public class DBTaskUpdate extends DBTask
{
    private ContentValues mValues;
    private String mWhereClause;
    private String[] mWhereArgs;

    public DBTaskUpdate(String table, ContentValues values, String whereClause, String[] whereArgs)
    {
        super(table);
        mValues = values;
        mWhereClause = whereClause;
        mWhereArgs = whereArgs;
    }

    public ContentValues getValues()
    {
        return mValues;
    }

    public void setValues(ContentValues values)
    {
        this.mValues = values;
    }

    public String getWhereClause()
    {
        return mWhereClause;
    }

    public void setWhereClause(String whereClause)
    {
        this.mWhereClause = whereClause;
    }

    public String[] getWhereArgs()
    {
        return mWhereArgs;
    }

    public void setWhereArgs(String[] whereArgs)
    {
        this.mWhereArgs = whereArgs;
    }
}
