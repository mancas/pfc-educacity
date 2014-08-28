package com.mancas.educacity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.Toast;

import com.mancas.adapters.GridViewImageAdapter;
import com.mancas.adapters.InfoSectionsAdapter;
import com.mancas.album.storage.AlbumStorageDirFactory;
import com.mancas.album.storage.BaseAlbumDirFactory;
import com.mancas.album.storage.FroyoAlbumDirFactory;
import com.mancas.database.DBHelper;
import com.mancas.database.DBHelper.AsyncInsert;
import com.mancas.database.DBHelper.DBHelperCallback;
import com.mancas.database.DBTaskInsert;
import com.mancas.database.DBTaskQuery;
import com.mancas.database.Image.ImageEntry;
import com.mancas.dialogs.PickPictureDialog;
import com.mancas.dialogs.PickPictureDialog.PickPictureCallbacks;
import com.mancas.educacity.SiteInfoFragment.SiteInfoCallback;
import com.mancas.utils.AppUtils;
import com.mancas.utils.Utils;

public class InfoActivity extends FragmentActivity implements ActionBar.TabListener,
    PickPictureCallbacks, DBHelperCallback, SiteInfoCallback {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
     * derivative, which will keep every loaded fragment in memory. If this
     * becomes too memory intensive, it may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    InfoSectionsAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    /**
     * Tag for identify information tab
     */
    private static final int INFO_TAB = 0;
    /**
     * Tag for identify photos tab
     */
    private static final int PHOTOS_TAB = 1;
    /**
     * Tag for identify timeline tab
     */
    private static final int TIMELINE_TAB = 2;
    /**
     * Debug Tag for use logging debug output to LogCat
     */
    private static final String TAG = "InfoActivity";
    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    /**
     * Paths list of all images the user has taken from the current site
     */
    private ArrayList<String> mImagesPath = new ArrayList<String>();
    /**
     * Path of the image that is being processed
     */
    private String mCurrentImagePath = null;
    /**
     * An instance of {@link DBHelper} used to manage changes in user data
     */
    private DBHelper mDatabaseManager;
    /**
     * An instance of {@link GridViewImageAdapter}
     */
    private GridViewImageAdapter mAdpater;
    /**
     * Grid view where the images of the user are going to be displayed
     */
    private GridView mGrid;
    /**
     * The column width of each item displayed in the grid view
     */
    private int mColumnWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new InfoSectionsAdapter(this, getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager
                .setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        actionBar.setSelectedNavigationItem(position);
                    }
                });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(actionBar.newTab()
                    .setText(mSectionsPagerAdapter.getPageTitle(i))
                    .setTabListener(this));
        }

        mDatabaseManager = DBHelper.getInstance(getApplicationContext(), this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }
    }

    /**
     * Method that retrieve all user images from teh current site
     */
    private void getUserImages() {
        AsyncGetImages asyncTask = new AsyncGetImages();
        String[] selectArgs = {String.valueOf(1)};
        DBTaskQuery task = new DBTaskQuery(ImageEntry.TABLE_NAME_WITH_PREFIX,
                ImageEntry.TABLE_PROJECTION, ImageEntry.SELECT_BY_SITE,
                selectArgs, null, null, ImageEntry.DEFUALT_TABLE_ORDER);
        asyncTask.execute(task);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
        case R.id.action_sync:
            mViewPager.setCurrentItem(PHOTOS_TAB);
            break;
        case R.id.action_take_photo:
            takePhoto();
            mViewPager.setCurrentItem(PHOTOS_TAB);
            break;
        case R.id.action_settings:
            break;
        case android.R.id.home:
            //NavUtils.navigateUpFromSameTask(this);
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab,
            FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab,
            FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab,
            FragmentTransaction fragmentTransaction) {
    }

    /* Photo album for this application */
    private String getAlbumName() {
        return getString(R.string.album_name);
    }

    @Override
    public void onBackPressed() {
        if (mViewPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
        }
    }

    /**
     * Method used for display a dialog to let user chose with which application
     * the image will be taken
     */
    private void takePhoto()
    {
        PickPictureDialog dialog = PickPictureDialog.newInstance(this);
        dialog.show(getFragmentManager(), TAG);
    }

    @Override
    public void onCameraBtnClick(DialogFragment dialog) {
        dialog.dismiss();
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = Utils.createImageFile(mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName()));
                mCurrentImagePath = photoFile.getAbsolutePath();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e(TAG, ex.getMessage());
                final Toast toast = new Toast(getApplicationContext());
                toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.BOTTOM, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setText(R.string.error_file_create);
                toast.show();
                return;
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                final ResolveInfo app =
                  AppUtils.getPreferredAppIfAvailable(takePictureIntent, getPackageManager());

                if (app != null) {
                    takePictureIntent.setClassName(app.activityInfo.packageName, app.activityInfo.name);
                }

                startActivityForResult(takePictureIntent, AppUtils.TAKE_IMAGE_FROM_CAMERA);
            }
        }
    }

    @Override
    public void onGalleryBtnClick(DialogFragment dialog) {
        dialog.dismiss();
        Intent takePictureIntent = new Intent(Intent.ACTION_PICK);
        takePictureIntent.setType("image/*");
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = Utils.createImageFile(mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName()));
                mCurrentImagePath = photoFile.getAbsolutePath();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e(TAG, ex.getMessage());
                final Toast toast = new Toast(getApplicationContext());
                toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.BOTTOM, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setText(R.string.error_file_create);
                toast.show();
                return;
            }
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(photoFile));
            final ResolveInfo app =
              AppUtils.getPreferredAppIfAvailable(takePictureIntent, getPackageManager());
    
            if (app != null) {
                takePictureIntent.setClassName(app.activityInfo.packageName, app.activityInfo.name);
            }

            startActivityForResult(takePictureIntent, AppUtils.TAKE_IMAGE_FROM_GALLERY);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
            case AppUtils.TAKE_IMAGE_FROM_CAMERA:
            case AppUtils.TAKE_IMAGE_FROM_GALLERY:
                saveImage(mCurrentImagePath);
                AppUtils.galleryAddPicture(this, mCurrentImagePath);
                mImagesPath.add(mCurrentImagePath);
                break;
            }
        }
    }

    /**
     * Stores a new image into the database, and mark it as not synchronized
     * @param path the absolute path of the image
     */
    private void saveImage(String path) {
        ContentValues image = new ContentValues();
        image.put(ImageEntry.COLUMN_PATH, path);
        image.put(ImageEntry.COLUMN_SITE_ID, 1);
        image.put(ImageEntry.COLUMN_SYNC, false);
        String publicProfile = AppUtils.readPreference(getApplicationContext(), SettingsActivity.PUBLIC_PROFILE_KEY,
                SettingsActivity.BOOL_TYPE);
        image.put(ImageEntry.COLUMN_PUBLIC, Boolean.valueOf(publicProfile));
        DBTaskInsert task = new DBTaskInsert(ImageEntry.TABLE_NAME_WITH_PREFIX, null, image);
        AsyncInsert insertTask = mDatabaseManager.new AsyncInsert();
        insertTask.execute(task);
    }

    @Override
    public void onDatabaseOpen(SQLiteDatabase database) {
    }

    @Override
    public void onSelectReady(Cursor data) {
    }

    @Override
    public void onInsertReady(long id) {
        mAdpater.notifyDataSetChanged();
    }

    @Override
    public void onUpdateReady(int rows) {
    }

    /**
     * Asynchronous task to perform a database query in order to retrieve all images stored in the database
     * @author Manuel Casas Barrado
     * @version 1.0
     */
    private class AsyncGetImages extends AsyncTask<DBTaskQuery, Void, Cursor>
    {
        @Override
        protected Cursor doInBackground(DBTaskQuery... params) {
            DBTaskQuery task = params[0];
            SQLiteDatabase db = mDatabaseManager.getDataBase();
            if (db == null) {
                db = mDatabaseManager.getDBOpenHelper().getWritableDatabase();
            }

            return db.query(task.getTableName(), task.getProjection(), task.getSelect(),
                    task.getSelectArgs(), task.getGroupBy(), task.getHaving(), task.getOrderBy());
        }

        @Override
        protected void onPostExecute(Cursor data) {
            Log.d(TAG, "" + data.getCount());
            if (data != null && data.getCount() > 0) {
                while (data.moveToNext()) {
                    String path = data.getString(data.getColumnIndex(ImageEntry.COLUMN_PATH));
                    File f = new File(path);
                    if (f.exists()) {
                        mImagesPath.add(path);
                    }
                }
                setUpGrid();
            }
        }
    }

    /**
     * Once the images are retrieved form database, they must be displayed over the grid view.
     * This method sets the width of each column based on the total space available in the screen.
     */
    public void setUpGrid()
    {
        int screenWidth = Utils.getScreenWidth(getApplicationContext());
        mGrid.setNumColumns(AppUtils.GRID_NUM_COLUMNS);
        float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                AppUtils.GRID_PADDING, getResources().getDisplayMetrics());
        float spacing = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                AppUtils.GRID_SPACING, getResources().getDisplayMetrics());
        mColumnWidth = (int) ((screenWidth - (2 * padding) - ((AppUtils.GRID_NUM_COLUMNS - 1) * spacing)) / AppUtils.GRID_NUM_COLUMNS);
        mAdpater = new GridViewImageAdapter(this, mImagesPath, mColumnWidth);

        mGrid.setColumnWidth(mColumnWidth);
        mGrid.setPadding((int) padding, (int) padding, (int) padding, (int) padding);
        mGrid.setHorizontalSpacing((int) spacing);
        mGrid.setVerticalSpacing((int) spacing);
        mGrid.setAdapter(mAdpater);
        //mGrid.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
        //mGrid.setMultiChoiceModeListener(this);
    }

    @Override
    public void onGridReady(GridView grid) {
        mGrid = grid;
        //Retrieve photos when grid view is ready
        getUserImages();
    }

    /* MULTICHOICE MODE LISTENER */

    /*@Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        // TODO Delete images from DB and from adapter
        switch (item.getItemId()) {
        case R.id.action_delete:
            SparseBooleanArray selectedItems = mAdpater.getSelectedIds();
            for(int i = 0; i < selectedItems.size(); i++) {
                if (selectedItems.valueAt(i)) {
                    mImagesPath.remove(selectedItems.keyAt(i));
                }
            }
            mAdpater.notifyDataSetChanged();
            mode.finish();
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.multichoice_delete, menu);
        mode.setTitle(R.string.multichoice_images_title);
        mode.setSubtitle("1 " + getResources().getString(R.string.one_item));
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        mAdpater.removeSelection();
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position,
            long id, boolean checked) {
        int checkedSum = mGrid.getCheckedItemCount();
        Log.d(TAG, "" + checkedSum);
        switch (checkedSum) {
        case 1:
            mode.setSubtitle(checkedSum + " " + getResources().getString(R.string.one_item));
            break;
        default:
            mode.setSubtitle(checkedSum + " " + getResources().getString(R.string.more_items));
            break;
        }

        mAdpater.toggleSelection(position);
    }*/
}
