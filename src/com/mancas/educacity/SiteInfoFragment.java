package com.mancas.educacity;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;

public class SiteInfoFragment extends Fragment
{
    /**
     * Tag for share position between parent activity and a new instance of SiteInfoFragment
     */
    public static final String FRAGMENT_BUNDLE_KEY = "fragment_position";
    /**
     * Reference to the object which is going to handle SiteInfo events
     */
    private SiteInfoCallback mCallbacks;

    /**
     * Returns a new instance of this fragment for the given section number.
     */
    public static SiteInfoFragment newInstance(int position, SiteInfoCallback callbacks) {
        SiteInfoFragment fragment = new SiteInfoFragment();
        fragment.mCallbacks = callbacks;
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
            mCallbacks.onInformationLayoutReady((LinearLayout) rootView);
            break;
        case 1:
            rootView =
              inflater.inflate(R.layout.educacity_tab_photos, container, false);
              mCallbacks.onGridReady((GridView) rootView.findViewById(R.id.my_photos));
            break;
        case 2:
            break;
        }

        return rootView;
    }

    public interface SiteInfoCallback
    {
        public void onGridReady(GridView grid);
        public void onInformationLayoutReady(LinearLayout root);
    }
}
