package com.mancas.database;

import android.content.ContentValues;

/**
 * {@inheritDoc}
 * @author Manuel Casas Barrado
 * @version 1.0
 */
public class DBTaskInsert extends DBTask
{
    private String mNullColumnHack;
    private ContentValues mValues;

    public DBTaskInsert(String table, String nullColumnHack, ContentValues values)
    {
        super(table);
        mNullColumnHack = nullColumnHack;
        mValues = values;
    }

    public String getNullColumnHack()
    {
        return mNullColumnHack;
    }

    public void setNullColumnHack(String nullColumnHack)
    {
        this.mNullColumnHack = nullColumnHack;
    }

    public ContentValues getValues()
    {
        return mValues;
    }

    public void setValues(ContentValues values)
    {
        this.mValues = values;
    }
}
