package com.mancas.adapters;

import com.mancas.educacity.R;

import android.content.Context;
import android.graphics.Typeface;
import android.renderscript.Type;
import android.util.Log;
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
    private int mSelectedItem = 0;

    private final String TAG = "ADAPTER DRAWER";

    public NavigationDrawerAdapter(Context context)
    {
        this.mContext = context;
        this.mItems = context.getResources().getStringArray(R.array.drawerMenu);
        this.mIcons = context.getResources().getStringArray(R.array.drawerMenuIcons);
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
            switch(position) {
            case 2:
            case 3:
                convertView = inflater.inflate(R.layout.drawer_list_item_small, parent, false);
                break;
            default:
                convertView = inflater.inflate(R.layout.educacity_list_with_icon, parent, false);
                break;
            }

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
            boolean isSelectedItem = (position == mSelectedItem);
            Log.d(TAG, mSelectedItem + "-" + isSelectedItem);
            viewHolder.textView.setTypeface(null, isSelectedItem ? Typeface.BOLD : Typeface.NORMAL);
            int idDrawable = mContext.getResources().getIdentifier(image, "drawable", mContext.getPackageName());
            viewHolder.imageView.setImageResource(idDrawable);
        }

        return convertView;
    }
}
