package com.mancas.educacity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.mancas.dialogs.EnableGPSDialog;

public class EducacityMapFragment extends Fragment {

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private EducacityMapCallbacks mCallbacks;
    /**
     * Context of the application where the fragment will be placed
     */
    private FragmentActivity mContext;

    private RelativeLayout infoLayout;
    private SaveStateMapFragment mMapFragment;
    
    public EducacityMapFragment()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @SuppressLint("NewApi") @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        infoLayout = (RelativeLayout) view.findViewById(R.id.sites_info);
        mMapFragment = new SaveStateMapFragment();

        FragmentTransaction ft = mContext.getSupportFragmentManager().beginTransaction();
        ft.add(R.id.map, mMapFragment);
        ft.commit();

        if (savedInstanceState == null) {
            mMapFragment.setRetainInstance(true);
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        /*try {
            Fragment fragment =  getActivity().getFragmentManager().findFragmentById(R.id.container);
            if (fragment != null) getFragmentManager().beginTransaction().remove(fragment).commit();

        } catch (IllegalStateException e) {
            //handle this situation because you are necessary will get 
            //an exception here :-(
        	Log.d("MAP", e.getMessage());
        }*/
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = (FragmentActivity) activity;
        try {
            mCallbacks = (EducacityMapCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement MapCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
    
    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }
    
    @Override
    public void onPause()
    {
        super.onPause();
    }
    
    public void onStop()
    {
        super.onStop();
    }
    

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface EducacityMapCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onMarkerSelected();
    }
}