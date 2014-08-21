package com.mancas.database;

/**
 * 
 * @author Manuel Casas Barrado
 * @version 1.0
 */
public class DBTaskQuery extends DBTask
{
    private String[] mProjection;
    private String mSelect;
    private String[] mSelectArgs;
    private String mGroupBy;
    private String mHaving;
    private String mOrderBy;
    
    public DBTaskQuery(String table, String[] projection, String select,
            String[] selectArgs, String groupBy, String having, String orderBy)
    {
        super(table);
        mProjection = projection;
        mSelect = select;
        mSelectArgs = selectArgs;
        mGroupBy = groupBy;
        mHaving = having;
        mOrderBy = orderBy;
    }

    public String[] getProjection()
    {
        return mProjection;
    }

    public void setProjection(String[] projection)
    {
        this.mProjection = projection;
    }

    public String getSelect()
    {
        return mSelect;
    }

    public void setSelect(String select)
    {
        this.mSelect = select;
    }

    public String[] getSelectArgs()
    {
        return mSelectArgs;
    }

    public void setSelectArgs(String[] selectArgs)
    {
        this.mSelectArgs = selectArgs;
    }

    public String getmGroupBy()
    {
        return mGroupBy;
    }

    public void setGroupBy(String groupBy)
    {
        this.mGroupBy = groupBy;
    }

    public String getHaving()
    {
        return mHaving;
    }

    public void setmHaving(String having)
    {
        this.mHaving = having;
    }

    public String getOrderBy()
    {
        return mOrderBy;
    }

    public void setOrderBy(String orderBy)
    {
        this.mOrderBy = orderBy;
    }
}
