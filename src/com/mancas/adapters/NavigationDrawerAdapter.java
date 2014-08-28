package com.mancas.adapters;

import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mancas.educacity.R;
import com.mancas.utils.DrawerItem;

/**
 * Adapter used for load items in the navigation drawer menu.
 * It uses {@link DrawerItem} to decide the layout, image and label
 * is use for each item
 * @author Manuel Casas Barrado
 * @version 1.0
 */
public class NavigationDrawerAdapter extends BaseAdapter
{
    /**
     * Context of the application
     */
    private Context mContext;
    /**
     * Items to display in the navigation drawer
     */
    private List<DrawerItem> mItems;
    /**
     * LayoutInflater used to inflate the item layout
     */
    private LayoutInflater inflater;
    /**
     * Current selected position used to give styles to the selected item
     */
    private int mSelectedItem = 0;

    /**
     * Debug Tag for use logging debug output to LogCat
     */
    private final String TAG = "ADAPTER DRAWER";

    /**
     * Adapter for loading items in the navigation drawer
     * @param context the context of the application
     * @param items the list of items to display in the navigation drawer
     */
    public NavigationDrawerAdapter(Context context, List<DrawerItem> items)
    {
        super();
        this.mContext = context;
        this.mItems = items;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * Method that updates the current selected item
     * @param position the current position of the selected item
     */
    public void setSelectedItem(int position)
    {
        this.mSelectedItem = position;
    }

    /** 
     * ViewHolder used to cache the view in order to save processor cycles
     **/
    static class ListImageViewHolder {
        TextView textView;
        ImageView imageView;
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
