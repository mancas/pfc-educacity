package com.mancas.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mancas.educacity.R;
import com.mancas.models.Site;

/**
 * Adapter used for load items in My Sites section.
 * @author Manuel Casas Barrado
 * @version 1.0
 */
public class MySitesAdapter extends BaseAdapter
{
    /**
     * A list with all sites to be displayed
     */
    private List<Site> mSites = new ArrayList<Site>();
    /**
     * LayoutInflater used to inflate the item layout
     */
    private LayoutInflater inflater;

    public MySitesAdapter(Context context, List<Site> sites)
    {
        mSites = sites;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mSites.size();
    }

    @Override
    public Site getItem(int position) {
        return mSites.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
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

        viewHolder.textView.setText(mSites.get(position).getTitle());

        return convertView;
    }

}
