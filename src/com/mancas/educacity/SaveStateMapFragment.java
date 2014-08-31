package com.mancas.educacity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mancas.dialogs.EnableGPSDialog;
import com.mancas.models.Site;
import com.mancas.utils.AppUtils;
import com.mancas.utils.HTTPRequestHelper;
import com.mancas.utils.HTTPRequestHelper.HTTPResponseCallback;
import com.mancas.utils.ParseJSONSites;

public class SaveStateMapFragment extends SupportMapFragment implements
  OnMarkerClickListener,
  OnConnectionFailedListener,
  android.location.LocationListener,
  ConnectionCallbacks
{
    private GoogleMap mMap;
    /**
     * URL to gets sites from REST server
     */
    private static final String GET_SITES_URL = "http://rest.educacity-sevilla.com/sites";
    /**
     * List of markers to display in the map
     */
    private List<Site> mSites;
    /**
     * Map to handle marker click events. The marker ID will be store together with the site ID
     */
    private Map<String, Integer> mMarkers = new HashMap<String, Integer>();
    /**
     * Tag for identify extras in an Intent
     */
    public static final String SITE_CLICKED = "SITE_CLICKED";
    /**
     * Instance of {@link LocationServices}
     */
    private LocationServices mLocationServices;
    /**
     * Instance of {@link LocationManager}
     */
    private LocationManager mLocationManager;
    public static final String PROXIMITY_ALERT_INTENT = "com.mancas.educacity.proximityAlert";
    public static final String PROXIMITY_ALERT_TITLE = "PROXIMITY_ALERT_TITLE";
    public static final int PROXIMITY_ALERT_EXPIRATION = -1;
    public static final int PROXIMITY_ALERT_RADIUS = 20;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setUpMapIfNeeded();
        setUpLocationManagerIfNeeded();
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
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
        mMap.setOnMarkerClickListener(this);
        mMap.setMyLocationEnabled(true);
    }

    public void setMap(GoogleMap map)
    {
        mMap = map;
    }

    public class GetSitesTask extends AsyncTask<Void, Void, List<Site>> implements HTTPResponseCallback
    {
        private List<Site> mSites;

        public GetSitesTask()
        {
        }

        @Override
        protected void onPreExecute()
        {
        }

        @Override
        protected List<Site> doInBackground(Void... params) {
            HTTPRequestHelper helper = new HTTPRequestHelper(null, this);
            helper.performGet(GET_SITES_URL);

            return mSites;
        }

        @Override
        protected void onPostExecute(final List<Site> sites) {
            setUpMarkers(sites);
        }

        @Override
        public void onResponseReady(String response) {
            if (!response.isEmpty()) {
                mSites = ParseJSONSites.parseMultipleSitesResponse(response);
            }
        }
    }

    public void setUpMarkers(List<Site> sites) {
        mSites = sites;
        if (mSites.size() != 0 && this.mMap != null) {
            Marker siteMarker;
            Site site;

            for (int i = 0; i < mSites.size(); i++) {
                site = sites.get(i);
                //Create a new marker
                siteMarker = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(site.getLatitude(), site.getLongitude()))
                            .title(site.getTitle()));

                //Add the marker to the map
                mMarkers.put(siteMarker.getId(), site.getId());
                setUpProximityAlert(siteMarker);
            }
        }
    }

    private void setUpProximityAlert(Marker siteMarker) {
        Intent intent = new Intent(PROXIMITY_ALERT_INTENT);
        intent.putExtra(PROXIMITY_ALERT_TITLE, siteMarker.getTitle());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity().getApplicationContext(), 0, intent, 0);
        LatLng position = siteMarker.getPosition();
        mLocationManager.addProximityAlert(position.latitude, position.longitude, PROXIMITY_ALERT_RADIUS, PROXIMITY_ALERT_EXPIRATION, pendingIntent);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Intent intent = new Intent(getActivity(), InfoActivity.class);
        intent.putExtra(SITE_CLICKED, mMarkers.get(marker.getId()));
        startActivity(intent);

        return true;
    }

    /* LOCATION */

    /**
     * Initialized Locaiton Services
     */
    private void setUpLocationManagerIfNeeded() {
        if (mLocationManager == null) {
            mLocationManager =
              (LocationManager) getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            setUpRequestUpdates();
            checkIfGPSIsEnabled();
            getSites();
        }
    }

    /**
     * Method thar set up the request location updates
     */
    private void setUpRequestUpdates() {
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (android.location.LocationListener) this);
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (android.location.LocationListener) this);
    }

    /**
     * Method that check if the user has gps enabled, if not an alert will be shown
     */
    private void checkIfGPSIsEnabled()
    {
        boolean isGPSEnabled = false;

        if (mLocationManager != null) {
            isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (!isGPSEnabled) {
                EnableGPSDialog enableGPSDialog = new EnableGPSDialog();
                enableGPSDialog.show(getActivity().getFragmentManager(), "ENABLE_GPS");
            }
        }
    }

    /**
     * Method that query server to get the list of available sites
     */
    private void getSites()
    {
        GetSitesTask task = new GetSitesTask();
        task.execute((Void) null);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
    }

    @Override
    public void onConnectionSuspended(int cause) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
}
