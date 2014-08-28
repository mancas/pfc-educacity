package com.mancas.adapters;

import java.util.List;

import com.mancas.educacity.R;
import com.mancas.utils.Utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Adapter to show items in a view pager in full screen mode
 * @author Manuel Casas Barrado
 * @version 1.0
 */
public class FullScreenAdapter extends PagerAdapter
{
    /**
     * The activity host
     */
    private Activity mActivity;
    /**
     * A list with all image paths to display
     */
    private List<String> mImagePaths;
    /**
     * Layout inflate used for inflate a layout for each item
     */
    private LayoutInflater inflater;

    // constructor
    public FullScreenAdapter(Activity activity, List<String> imagePaths) {
        mActivity = activity;
        mImagePaths = imagePaths;
    }

    @Override
    public int getCount() {
        return mImagePaths.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imgDisplay;
 
        inflater = (LayoutInflater) mActivity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.educacity_gallery_full_screen, container,
                false);
 
        imgDisplay = (ImageView) viewLayout.findViewById(R.id.display);

        Bitmap bitmap = Utils.decodeFile(mImagePaths.get(position), Utils.getScreenWidth(mActivity), Utils.getScreenHeight(mActivity));
        imgDisplay.setImageBitmap(bitmap);
        ((ViewPager) container).addView(viewLayout);
 
        return viewLayout;
    }
    
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
 
    }

    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view.equals(obj);
    }
}
