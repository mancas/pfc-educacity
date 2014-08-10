package com.mancas.educacity;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MySitesFragment extends Fragment
{
    public static final String TAG = "My Sites Fragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void onResume()
    {
        super.onResume();
    }

    public void onPause()
    {
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            Fragment fragment =  getActivity().getFragmentManager().findFragmentById(R.id.container);
            if (fragment != null) getFragmentManager().beginTransaction().remove(fragment).commit();
        } catch (IllegalStateException e) {
            //handle this situation because you are necessary will get 
            //an exception here :-(
        	Log.d(TAG, e.getMessage());
        }
    }
}
