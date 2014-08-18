package com.mancas.adapters;

import java.util.Locale;

import com.mancas.educacity.R;
import com.mancas.educacity.SiteInfoFragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentPagerAdapter;

public class InfoSectionsAdapter extends FragmentPagerAdapter
{
    private Context mContext;

    public InfoSectionsAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class
        // below).
        return SiteInfoFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        switch (position) {
        case 0:
            return mContext.getResources().getString(R.string.info_tab_information).toUpperCase(l);
        case 1:
            return mContext.getResources().getString(R.string.info_tab_photos).toUpperCase(l);
        case 2:
            return mContext.getResources().getString(R.string.info_tab_timeline).toUpperCase(l);
        }
        return null;
    }
}
