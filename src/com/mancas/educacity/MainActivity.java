package com.mancas.educacity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.mancas.database.Account.AccountEntry;
import com.mancas.database.DBHelper;
import com.mancas.database.DBHelper.AsyncUpdate;
import com.mancas.database.DBTaskUpdate;
import com.mancas.database.Image.ImageEntry;
import com.mancas.utils.AppUtils;


public class MainActivity extends FragmentActivity implements
  NavigationDrawerFragment.NavigationDrawerCallbacks,
  EducacityMapFragment.EducacityMapCallbacks,
  MyAccountFragment.MyAccountCallbacks,
  DBHelper.DBHelperCallback {
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
    /**
     * An instance of {@link DBHelper} used to manage changes in user data
     */
    private DBHelper mDatabaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        mDatabaseManager = DBHelper.getInstance(getApplicationContext(), this);

        mMapFragment = new EducacityMapFragment();
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
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
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
            checkAccount();
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

    /**
     * Initializes the action bar with the application title and the standar navigation mode
     */
    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    /**
     * Check if the current user is logged into the application if not,
     * he must be enter login data or register in order to access to his account
     */
    private void checkAccount() {
        //TODO MAKE LOGIN WITH REST
        long accountId = AppUtils.getAccountID(getApplicationContext());
        if (accountId == -1) {
            //No accounts registered
            Intent login = new Intent(getApplicationContext(), LoginActivity.class);
            startActivityForResult(login, LoginActivity.LOGIN_REQUEST);
        } else {
            loadFragment(new MyAccountFragment());
        }
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

    /**
     * Place a new fragment in the screen and replaces the older one
     * @param fragment the new fragment to be displayed
     */
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
       if (resultCode == Activity.RESULT_OK) {
           switch (requestCode) {
           case LoginActivity.LOGIN_REQUEST:
               loadFragment(new MyAccountFragment());
               break;
           }
       }
    }

    /**
     * Updates the account name and mark it as not synchronized.
     * @param name the new name choosen by the user
     */
    @Override
    public void updateAccountName(final String name) {
        if (mDatabaseManager != null) {
            ContentValues values = new ContentValues();
            values.put(AccountEntry.COLUMN_NAME, name);
            values.put(AccountEntry.COLUMN_SYNC, false);
            long id = AppUtils.getAccountID(getApplicationContext());
            String[] whereArgs = {String.valueOf(id)};
            DBTaskUpdate task = new DBTaskUpdate(AccountEntry.TABLE_NAME_WITH_PREFIX, values,
                    AccountEntry.DEFAULT_TABLE_SELECTION, whereArgs);
            AsyncUpdate updateTask = mDatabaseManager.new AsyncUpdate();
            updateTask.execute(task);
        } else {
            final Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.BOTTOM, 0, 0);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setText(R.string.db_insert_error);
            toast.show();
        }
    }

    /**
     * Stores in database the new profile image of the user
     * @param path the path of the new profile image
     */
    @Override
    public void updateProfileImage(final String path) {
        if (mDatabaseManager != null) {
            mDatabaseManager.establishDB();
            long image_id = AppUtils.getAccountImageID(getApplicationContext());
            String where = ImageEntry.DEFAULT_TABLE_SELECTION;
            String[] whereArgs = {String.valueOf(image_id)};
            ContentValues values = new ContentValues();
            values.put(ImageEntry.COLUMN_PATH, path);
            values.put(ImageEntry.COLUMN_SYNC, false);
            DBTaskUpdate task = new DBTaskUpdate(ImageEntry.TABLE_NAME_WITH_PREFIX, values, where, whereArgs);
            AsyncUpdate updateTask = mDatabaseManager.new AsyncUpdate();
            updateTask.execute(task);
        } else {
            final Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.BOTTOM, 0, 0);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setText(R.string.db_insert_error);
            toast.show();
        }
    }

    @Override
    public void onDatabaseOpen(SQLiteDatabase db)
    {
    }

    @Override
    public void onSelectReady(Cursor data) {
    }

    @Override
    public void onInsertReady(long id) {
    }

    @Override
    public void onUpdateReady(int rows) {
    }
}
