package com.mancas.educacity;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends FragmentActivity implements
  NavigationDrawerFragment.NavigationDrawerCallbacks,
  EducacityMapFragment.EducacityMapCallbacks,
  MyAccountFragment.MyAccountCallbacks{
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Fragment managing the behaviors, interactions and presentation of the container.
     */
    private EducacityMapFragment mMapFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        mMapFragment = new EducacityMapFragment();
Log.d("MAIN", "On create");
        // Set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
          (DrawerLayout) findViewById(R.id.drawer_layout));

        PreferenceManager.setDefaultValues(this, R.xml.educacity_preferences, false);

        loadFragment(mMapFragment);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position, int lastPostion) {
        // update the main content by replacing fragments
        //FragmentManager fragmentManager = getFragmentManager();
        //fragmentManager.beginTransaction()
         //       .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
         //       .commit();
        switch (position) {
        case 0:
            //Educacity Sevilla
            if (lastPostion != 0) {
                loadFragment(new EducacityMapFragment());
            }
            break;
        case 1:
            //Mis sitios
        	loadFragment(new MySitesFragment());
            break;
        case 2:
            //Mi cuenta
            loadFragment(new MyAccountFragment());
            break;
        case 3:
            //Sincronizar
            break;
        case 4:
            //Ajustes
            Intent mIntent = new Intent(this, SettingsActivity.class);
            startActivity(mIntent);
            break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void loadFragment(Fragment fragment)
    {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onMarkerSelected() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
       super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void updateAccountName(String name) {
        // TODO Auto-generated method stub
        Log.d("MAIN", name);
    }

    @Override
    public void updateProfileImage(String path) {
        // TODO Auto-generated method stub
        
    }
}
