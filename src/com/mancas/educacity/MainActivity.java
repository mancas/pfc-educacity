package com.mancas.educacity;

import com.mancas.database.DBHelper;
import com.mancas.database.Account.AccountEntry;
import com.mancas.database.Image.ImageEntry;

import android.app.ActionBar;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


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
        mDatabaseManager = new DBHelper(getApplicationContext(), this);

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
        if (mDatabaseManager != null)
            mDatabaseManager.cleanUp();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (mDatabaseManager != null)
            mDatabaseManager.establishDB();
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
        if (mDatabaseManager != null) {
            Cursor data = mDatabaseManager.select(AccountEntry.TABLE_NAME_WITH_PREFIX,
                    AccountEntry.TABLE_PROJECTION, null,
                    null, null, null, AccountEntry.DEFUALT_TABLE_ORDER);
            ContentValues values = new ContentValues();
            values.put(AccountEntry.COLUMN_NAME, name);
            values.put(AccountEntry.COLUMN_SYNC, false);
            if (data.getCount() == 0) {
                //No Account created we need to set up a new account
                mDatabaseManager.insert(AccountEntry.TABLE_NAME_WITH_PREFIX, null, values);
            } else {
                //We must update the current account
                data.moveToFirst();
                int id = data.getInt(data.getColumnIndex(AccountEntry._ID));
                String[] whereArgs = {String.valueOf(id)};
                mDatabaseManager.update(AccountEntry.TABLE_NAME_WITH_PREFIX, values,
                        AccountEntry.DEFAULT_TABLE_SELECTION, whereArgs);
            }
        }
    }

    @Override
    public void updateProfileImage(String path) {
        if (mDatabaseManager.getDataBase() != null) {
            Cursor data = mDatabaseManager.select(AccountEntry.TABLE_NAME_WITH_PREFIX,
                    AccountEntry.TABLE_PROJECTION, null,
                    null, null, null, AccountEntry.DEFUALT_TABLE_ORDER);
            if (data == null) {
                Toast toast = Toast.makeText(getApplicationContext(), R.string.db_insert_error, Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
            //First we need to create the image if not exist
            ContentValues values = new ContentValues();
            values.put(AccountEntry.COLUMN_SYNC, false);

            ContentValues imageValues = new ContentValues();
            values.put(AccountEntry.COLUMN_IMAGE, path);
            values.put(AccountEntry.COLUMN_SYNC, false);
            if (data.getCount() == 0) {
                //No Account created we need to set up a new account
                long image_id = mDatabaseManager.insert(ImageEntry.TABLE_NAME_WITH_PREFIX, null, imageValues);
                if (image_id == -1) {
                    Toast toast = Toast.makeText(getApplicationContext(), R.string.db_insert_error, Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                values.put(AccountEntry.COLUMN_IMAGE, image_id);
                mDatabaseManager.insert(AccountEntry.TABLE_NAME_WITH_PREFIX, null, values);
            } else {
                //We must update the current account
                data.moveToFirst();
                int image_id = data.getInt(data.getColumnIndex(AccountEntry.COLUMN_IMAGE));
                String[] whereArgs = {String.valueOf(image_id)};
                mDatabaseManager.update(ImageEntry.TABLE_NAME_WITH_PREFIX, imageValues,
                        ImageEntry.DEFAULT_TABLE_SELECTION, whereArgs);
            }
        }
    }

    @Override
    public String getCurrentProfileName()
    {
        Cursor data = mDatabaseManager.select(AccountEntry.TABLE_NAME_WITH_PREFIX,
                AccountEntry.TABLE_PROJECTION, null,
                null, null, null, AccountEntry.DEFUALT_TABLE_ORDER);

        if (data == null) {
            return getResources().getString(R.string.profile_name_default);
        }

        if (data.getCount() == 0) {
            return getResources().getString(R.string.profile_name_default);
        }
        data.moveToFirst();

        return data.getString(data.getColumnIndex(AccountEntry.COLUMN_NAME));
    }

    @Override
    public String getCurrentProfileImage()
    {
        Cursor data = mDatabaseManager.select(AccountEntry.TABLE_NAME_WITH_PREFIX,
                AccountEntry.TABLE_PROJECTION, null,
                null, null, null, AccountEntry.DEFUALT_TABLE_ORDER);

        if (data == null) {
            return getResources().getString(R.string.profile_image_default);
        }

        if (data.getCount() == 0) {
            return getResources().getString(R.string.profile_image_default);
        }
        data.moveToFirst();

        long id = data.getLong(data.getColumnIndex(AccountEntry._ID));

        data = mDatabaseManager.query(AccountEntry.TABLE_JOIN_ACCOUNT_IMAGE_SQL, new String[]{String.valueOf(id)});

        if (data.getCount() == 0) {
            return getResources().getString(R.string.profile_image_default);
        }

        return data.getString(data.getColumnIndex(ImageEntry.TABLE_NAME + "." + ImageEntry.COLUMN_PATH));
    }

    @Override
    public void onDatabaseOpen(SQLiteDatabase db)
    {
    }
}
