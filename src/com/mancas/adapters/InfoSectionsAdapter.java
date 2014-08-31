package com.mancas.adapters;

import java.util.Locale;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mancas.educacity.R;
import com.mancas.educacity.SiteInfoFragment;
import com.mancas.educacity.SiteInfoFragment.SiteInfoCallback;

/**
 * Adapter to load info fragments into a ViewPager
 * @author Manuel Casas Barrado
 * @version 1.0
 */
public class InfoSectionsAdapter extends FragmentPagerAdapter
{
    /**
     * A reference to the activity
     */
    private Activity mActivity;

    /**
     * Adapter for loading pages
     * This adapter is used to display the three tabs in InfoActivity. These
     * tabs contains the information about the current site, user photos and
     * the site timeline
     * @param activity the context of the application
     * @param fragmentManager FragmentManager to interact with other fragments
     */
    public InfoSectionsAdapter(Activity activity, FragmentManager fragmentManager) {
        super(fragmentManager);
        mActivity = activity;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a SiteInfoFramgent
        return SiteInfoFragment.newInstance(position, (SiteInfoCallback) mActivity);
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        switch (position) {
        case 0:
            return mActivity.getResources().getString(R.string.info_tab_information).toUpperCase(l);
        case 1:
            return mActivity.getResources().getString(R.string.info_tab_photos).toUpperCase(l);
        case 2:
            return mActivity.getResources().getString(R.string.info_tab_timeline).toUpperCase(l);
        }
        return null;
    }
}
