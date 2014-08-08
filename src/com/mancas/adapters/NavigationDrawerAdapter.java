package com.mancas.adapters;

import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mancas.educacity.R;
import com.mancas.utils.DrawerItem;

public class NavigationDrawerAdapter extends BaseAdapter {

    private Context mContext;
    private List<DrawerItem> mItems;
    private LayoutInflater inflater;
    private int mSelectedItem = 0;

    private final String TAG = "ADAPTER DRAWER";

    public NavigationDrawerAdapter(Context context, List<DrawerItem> items)
    {
        super();
        this.mContext = context;
        this.mItems = items;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    
    public void setSelectedItem(int position)
    {
        this.mSelectedItem = position;
    }

    /** 
     * ViewHolder use to cache the view in order to save processor cycles
     **/
    static class ListImageViewHolder {
        TextView textView;
        ImageView imageView;
        View lastDivider;
        int position;
    }

    @Override
    public int getCount()
    {
        return mItems.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mItems.get(position);
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
        DrawerItem tCurrentItem = mItems.get(position);

        if (convertView == null) {
            convertView = inflater.inflate(tCurrentItem.getLayout(), parent, false);

            viewHolder.textView = (TextView) convertView.findViewById(R.id.item_text);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.item_image);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ListImageViewHolder) convertView.getTag();
        }

        viewHolder.textView.setText(tCurrentItem.getText());
        boolean isSelectedItem = (position == mSelectedItem);
        viewHolder.textView.setTypeface(null, isSelectedItem ? Typeface.BOLD : Typeface.NORMAL);
        //int idDrawable = mContext.getResources().getIdentifier(image, "drawable", mContext.getPackageName());
        viewHolder.imageView.setImageResource(tCurrentItem.getIcon());

        return convertView;
    }
}
