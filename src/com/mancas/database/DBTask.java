package com.mancas.database;

/**
 * Abstract class used to define a database task (insert/update/query)
 * @author Manuel Casas Barrado
 * @version 1.0
 */
public abstract class DBTask
{
    private String mTableName;

    public DBTask(String table)
    {
        mTableName = table;
    }

    /**
     * Returns the table name on which the task will be performed
     * @return the name of the table
     */
    public String getTableName()
    {
        return mTableName;
    }

    /**
     * Set the table name on which the task will be performed
     * @param table the name of the table
     */
    public void setTableName(String table)
    {
        mTableName = table;
    }
}
