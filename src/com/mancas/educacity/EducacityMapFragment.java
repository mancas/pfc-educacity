package com.mancas.educacity;

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
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.mancas.dialogs.EnableGPSDialog;

public class EducacityMapFragment extends MapFragment
        implements
        ConnectionCallbacks,
        OnConnectionFailedListener,
        LocationListener,
        OnMyLocationButtonClickListener,
        OnMarkerClickListener{

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private MapCallbacks mCallbacks;

    private FragmentActivity mContext;

    private GoogleMap mMap;
    private LocationClient mLocationClient;
    private LocationManager mLocationManager;
    private LocationRequest REQUEST = LocationRequest.create()
            .setInterval(10000)
            .setFastestInterval(32)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    private RelativeLayout infoLayout;
    
    public EducacityMapFragment()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*SupportMapFragment mapFragment = 
                (SupportMapFragment) mContext.getSupportFragmentManager().findFragmentById(R.id.map);

        if (savedInstanceState == null) {
            // First incarnation of this activity.
            mapFragment.setRetainInstance(true);
        } else {
            // Reincarnated activity. The obtained map is the same map instance in the previous
            // activity life cycle. There is no need to reinitialize it.
            mMap = mapFragment.getMap();
        }*/
        setUpLocationClientIfNeeded();
        setUpLocationManagerIfNeeded();
    }
    
    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        infoLayout = (RelativeLayout) view.findViewById(R.id.sites_info);
        setUpMap(R.id.map);

        return view;
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
        	Log.d("MAP", e.getMessage());
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = (FragmentActivity) activity;
        try {
            mCallbacks = (MapCallbacks) activity;
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
        setUpMap(R.id.map);
        setUpLocationClientIfNeeded();
        setUpLocationManagerIfNeeded();
        mLocationClient.connect();
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
        if (mLocationClient != null)
            mLocationClient.disconnect();
    }
    
    public void onStop()
    {
        super.onStop();
    }
    
    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     */
    public void setUpMap(int fragmentId) {
        if (mMap == null) {
            //mMap = ((SupportMapFragment) getFragmentManager().findFragmentById(fragmentId)).getMap();
            mMap = ((SupportMapFragment) mContext.getSupportFragmentManager().findFragmentById(fragmentId)).getMap();
            //Check if we were successful in obtaining the map
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
                mMap.setOnMyLocationButtonClickListener(this);   
                mMap.setOnMarkerClickListener(this);
            }
        }
    }

    
    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface MapCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onMarkerSelected();
    }
    
    /**
     * Connection Callbacks
     */
    @Override
    public void onConnectionFailed(ConnectionResult result)
    {
        //Do nothing        
    }

    @Override
    public void onConnected(Bundle connectionHint)
    {
        mLocationClient.requestLocationUpdates(REQUEST, this); 
    }

    @Override
    public void onDisconnected()
    {
        //Do nothing
    }
    
    /**
     * Set up functions
     */
    private void setUpLocationClientIfNeeded() {
        if (mLocationClient == null) {
            mLocationClient = new LocationClient(
                    mContext,
                    this,  // ConnectionCallbacks
                    this); // OnConnectionFailedListener
        }
    }
    
    private void setUpLocationManagerIfNeeded()
    {
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
            checkIfGPSIsEnabled();
        }
    }
    
    /**
     * Show gps not enabled dialog
     */
    private void notGPSEnabled()
    {
        EnableGPSDialog enableGPSDialog = new EnableGPSDialog();
        enableGPSDialog.setTargetFragment(this, EnableGPSDialog.GPS_ENABLED);
        enableGPSDialog.show(getFragmentManager(), "enabledGPS");
    }
    
    private void checkIfGPSIsEnabled()
    {
        boolean isGPSEnabled = false;

        if (mLocationManager != null) {
            isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            
            if (!isGPSEnabled) {
                notGPSEnabled();
            } else {
               // if (this.sites.size() == 0)
                 //   new RetrieveSitesTask(sitesHandler, this).execute(RetrieveSitesTask.URL_GET_SITES);
            }
        }
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // Check which request we're responding to
        if (requestCode == EnableGPSDialog.GPS_ENABLED) {
            checkIfGPSIsEnabled();
        }
    }
    
    /**
     * Marker click
     */
    @Override
    public boolean onMarkerClick(Marker marker)
    {
        //Intent intent = new Intent(MapActivity.this, InfoActivity.class);
        //intent.putExtra("SITE_CLICKED", this.markersMap.get(marker.getId()));
        //startActivityForResult(intent, 2);
        //overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right);
        //overridePendingTransition(android.R.anim.slide_out_right, android.R.anim.slide_in_left);
        mCallbacks.onMarkerSelected();
        return true;
    }
    
    /**
     * Location Button click
     */
    @Override
    public boolean onMyLocationButtonClick()
    {
        Toast.makeText(mContext, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        if (infoLayout.getVisibility() == View.VISIBLE) {
            infoLayout.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.educacity_slide_down));
            infoLayout.setVisibility(View.GONE);
        } else {
            infoLayout.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.educacity_slide_up));
            infoLayout.setVisibility(View.VISIBLE);
        }
        return false;
    }

    @Override
    public void onLocationChanged(Location location)
    {
                
    }
}