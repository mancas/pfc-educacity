package com.mancas.educacity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class SaveStateMapFragment extends SupportMapFragment implements OnMarkerDragListener
{
    private GoogleMap mMap;
    private LatLng mMarkerPosition;
    //private MarkerInfo mMarkerInfo;
    private boolean mMoveCameraToMarker;
    /** Default marker position when the activity is first created. */
    private static final LatLng DEFAULT_MARKER_POSITION = new LatLng(48.858179, 2.294576);

    //Bundle keys
    private static final String MARKER_POSITION = "markerPosition";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            // Activity created for the first time.
            mMarkerPosition = DEFAULT_MARKER_POSITION;
            //mMarkerInfo = new MarkerInfo(BitmapDescriptorFactory.HUE_RED);
            mMoveCameraToMarker = true;
            
        } else {
            // Extract the state of the MapFragment:
            // - Objects from the API (eg. LatLng, MarkerOptions, etc.) were stored directly in
            //   the savedInsanceState Bundle.
            // - Custom Parcelable objects were wrapped in another Bundle.

            mMarkerPosition = savedInstanceState.getParcelable(MARKER_POSITION);

            //Bundle bundle = savedInstanceState.getBundle(OTHER_OPTIONS);
            //mMarkerInfo = bundle.getParcelable(MARKER_INFO);

            mMoveCameraToMarker = false;
        }

        setUpMapIfNeeded();
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // All Parcelable objects of the API  (eg. LatLng, MarkerOptions, etc.) can be set
        // directly in the given Bundle.
        outState.putParcelable(MARKER_POSITION, mMarkerPosition);

        // All other custom Parcelable objects must be wrapped in another Bundle. Indeed,
        // failing to do so would throw a ClassNotFoundException. This is due to the fact that
        // this Bundle is being parceled (losing its ClassLoader at this time) and unparceled
        // later in a different ClassLoader.
        //Bundle bundle = new Bundle();
        //bundle.putParcelable(MARKER_INFO, mMarkerInfo);
        //outState.putBundle(OTHER_OPTIONS, bundle);
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        MarkerOptions markerOptions = new MarkerOptions()
            .position(mMarkerPosition)
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            .draggable(true);
        mMap.addMarker(markerOptions);
        mMap.setOnMarkerDragListener(this);
        //mMap.setOnMarkerClickListener(this);

        if (mMoveCameraToMarker) {
            mMap.animateCamera(CameraUpdateFactory.newLatLng(mMarkerPosition));
        }
    }

    @Override
    public void onMarkerDrag(Marker marker)
    {
    }

    @Override
    public void onMarkerDragEnd(Marker marker)
    {
        mMarkerPosition = marker.getPosition();
    }

    @Override
    public void onMarkerDragStart(Marker marker)
    {
    }
    
    public void setMap(GoogleMap map)
    {
        mMap = map;
    }
}
