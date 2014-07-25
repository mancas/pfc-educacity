package com.mancas.adapters;

import com.mancas.educacity.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NavigationDrawerAdapter extends BaseAdapter {

    private Context mContext;
    private String[] mItems;
    private String[] mIcons;
    private LayoutInflater inflater;

    public NavigationDrawerAdapter(Context context)
    {
        this.mContext = context;
        this.mItems = context.getResources().getStringArray(R.array.drawerMenu);
        this.mIcons = context.getResources().getStringArray(R.array.drawerMenuIcons);
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /** 
     * ViewHolder use to cache the view in order to save processor cycles
     **/
    static class ListImageViewHolder {
        TextView textView;
        ImageView imageView;
        int position;
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
    	ListImageViewHolder viewHolder = new ListImageViewHolder();

    	if (convertView == null) {
    		convertView = inflater.inflate(R.layout.educacity_list_with_icon, parent, false);
    		viewHolder.textView = (TextView) convertView.findViewById(R.id.item_text);
    		viewHolder.imageView = (ImageView) convertView.findViewById(R.id.item_image);
    		
    		convertView.setTag(viewHolder);
    	} else {
    		viewHolder = (ListImageViewHolder) convertView.getTag();
    	}
    	
    	String text = mItems[position];
    	String image = mIcons[position];
    	
    	if (text != null && image != null) {
    		viewHolder.textView.setText(text);
    		int idDrawable = mContext.getResources().getIdentifier(image, "drawable", null);
    		viewHolder.imageView.setImageResource(idDrawable);
    	}

        return convertView;
    }
}
