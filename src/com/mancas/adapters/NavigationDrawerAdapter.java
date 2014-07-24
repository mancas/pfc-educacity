package com.mancas.adapters;

import com.mancas.educacity.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class NavigationDrawerAdapter extends BaseAdapter {

    private Context mContext;
    private String[] mItems;
    private LayoutInflater inflater;
    
    public NavigationDrawerAdapter(Context context)
    {
        this.mContext = context;
        this.mItems = context.getResources().getStringArray(R.array.drawerMenu);
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount()
    {
        return mItems.length;
    }

    @Override
    public Object getItem(int position)
    {
        return mItems[position];
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        //View rowView = inflater.inflate();
        // TODO Auto-generated method stub
        return null;
    }
}
