package com.mancas.educacity;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SiteInfoFragment extends Fragment
{
    public static final String FRAGMENT_BUNDLE_KEY = "fragment_position";
    /**
     * Returns a new instance of this fragment for the given section number.
     */
    public static SiteInfoFragment newInstance(int position) {
        SiteInfoFragment fragment = new SiteInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(FRAGMENT_BUNDLE_KEY, position);
        fragment.setArguments(bundle);

        return fragment;
    }

    public SiteInfoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        int position = getArguments().getInt(FRAGMENT_BUNDLE_KEY);
        View rootView = null;

        switch(position) {
        case 0:
            rootView =
              inflater.inflate(R.layout.educacity_tab_information, container, false);
            break;
        case 1:
            rootView =
              inflater.inflate(R.layout.educacity_tab_photos, container, false);
            break;
        case 2:
            break;
        }

        return rootView;
    }
}
