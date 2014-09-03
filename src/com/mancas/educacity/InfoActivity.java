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
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.mancas.adapters.GridViewImageAdapter;
import com.mancas.adapters.InfoSectionsAdapter;
import com.mancas.album.storage.AlbumStorageDirFactory;
import com.mancas.album.storage.BaseAlbumDirFactory;
import com.mancas.album.storage.FroyoAlbumDirFactory;
import com.mancas.database.DBHelper;
import com.mancas.database.DBHelper.AsyncInsert;
import com.mancas.database.DBHelper.AsyncInsertSiteIfNeeded;
import com.mancas.database.DBHelper.DBHelperCallback;
import com.mancas.database.DBTaskInsert;
import com.mancas.database.DBTaskQuery;
import com.mancas.database.Image.ImageEntry;
import com.mancas.dialogs.NoNetworkDialog;
import com.mancas.dialogs.PickPictureDialog;
import com.mancas.dialogs.PickPictureDialog.PickPictureCallbacks;
import com.mancas.educacity.SiteInfoFragment.SiteInfoCallback;
import com.mancas.models.RegisterModel;
import com.mancas.models.Site;
import com.mancas.synchronize.Synchronize;
import com.mancas.utils.AppUtils;
import com.mancas.utils.HTTPRequestHelper;
import com.mancas.utils.ParseJSONSites;
import com.mancas.utils.HTTPRequestHelper.HTTPResponseCallback;
import com.mancas.utils.Utils;

public class InfoActivity extends FragmentActivity implements ActionBar.TabListener,
    PickPictureCallbacks, DBHelperCallback, SiteInfoCallback, OnClickListener {

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
    /**
     * View where the information about the place will be displayed
     */
    private ScrollView mInformationView;
    /**
     * Current site information
     */
    private Site mSite;
    /**
     * LinewarLayoyt where the progress will be displayed
     */
    private LinearLayout mProgress;
    /**
     * Progress bar
     */
    private ProgressBar mProgressBar;
    /**
     * Button to retry if something went wrong
     */
    private Button mRetry;
    /**
     * TextView where to display the current status
     */
    private TextView mStatus;
    /**
     * ID of the current site
     */
    private Integer mSiteId;
    /**
     * Title of the current site
     */
    private String mSiteTitle;
    private WebView mWikipedia;
    public static String WIKIPEDIA_PREFIX = "http://es.wikipedia.org/wiki/";
    public static String GET_SITE_URL = "http://rest.educacity-sevilla.com/site/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle extras = getIntent().getExtras();
        mSiteId = extras.getInt(SaveStateMapFragment.SITE_CLICKED, 1);
        mSiteTitle = extras.getString(SaveStateMapFragment.SITE_TITLE);

        getActionBar().setTitle(mSiteTitle);
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
        String[] selectArgs = {String.valueOf(mSiteId)};
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
            synchronize();
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

    /**
     * Method that starts the synchronize
     */
    private void synchronize() {
        boolean connection = AppUtils.checkNetworkConnection(getApplicationContext());
        if (!connection) {
            NoNetworkDialog dialog = new NoNetworkDialog();
            dialog.show(getFragmentManager(), "Info activity");
            return;
        }
        Synchronize sync = Synchronize.getInstance(this);
        sync.synchronizeData();
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
        PickPictureDialog dialog = PickPictureDialog.newInstance(this, getResources().getString(R.string.add_photo));
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
        image.put(ImageEntry.COLUMN_SITE_ID, mSiteId);
        image.put(ImageEntry.COLUMN_SYNC, false);
        String publicProfile = AppUtils.readPreference(getApplicationContext(), SettingsActivity.PUBLIC_PROFILE_KEY,
                SettingsActivity.BOOL_TYPE);
        image.put(ImageEntry.COLUMN_PUBLIC, Boolean.valueOf(publicProfile));
        DBTaskInsert task = new DBTaskInsert(ImageEntry.TABLE_NAME_WITH_PREFIX, null, image);
        AsyncInsert insertTask = mDatabaseManager.new AsyncInsert();
        insertTask.execute(task);
        AsyncInsertSiteIfNeeded siteTask = mDatabaseManager.new AsyncInsertSiteIfNeeded();
        Site site = new Site();
        site.setId(mSiteId);
        site.setTitle(mSiteTitle);
        siteTask.execute(site);
    }

    @Override
    public void onDatabaseOpen(SQLiteDatabase database) {
    }

    @Override
    public void onSelectReady(Cursor data) {
    }

    @Override
    public void onInsertReady(long id) {
        if (mAdpater != null) {
            mAdpater.notifyDataSetChanged();
            return;
        }

        if (mGrid != null) {
            setUpGrid();
        }
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
            Log.d("COUNT", "" + data.getCount());
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

    @Override
    public void onInformationLayoutReady(LinearLayout root) {
        mInformationView = (ScrollView) root.findViewById(R.id.scroll);
        mProgress = (LinearLayout) root.findViewById(R.id.progress);
        mRetry = (Button) mProgress.findViewById(R.id.retry_button);
        mRetry.setOnClickListener(this);
        mProgressBar = (ProgressBar) mProgress.findViewById(R.id.progress_bar);
        mStatus = (TextView) mProgress.findViewById(R.id.progress_message);
        mWikipedia = (WebView) root.findViewById(R.id.wikipedia_frame);
        getSite();
    }

    /**
     * Asynchronous task to retrieve the information about the current site
     * @author Manuel Casas Barrado
     * @version 1.0
     */
    public class GetSiteTask extends AsyncTask<Void, Void, Site>
        implements HTTPResponseCallback{
        private Site mSite;

        @Override
        protected Site doInBackground(Void... params) {
            HTTPRequestHelper helper = new HTTPRequestHelper(null, this);
            helper.performGet(GET_SITE_URL + mSiteId);

            return mSite;
        }

        @Override
        protected void onPostExecute(final Site model) {
            if (model != null) {
                if (!model.hasErrors()) {
                    AppUtils.showProgress(getApplicationContext(), mProgress, mWikipedia, false);
                    displayModelInView(model);
                    return;
                }
                displayServerError();
            }
        }

        @Override
        protected void onCancelled() {
            displayServerError();
        }

        @Override
        public void onResponseReady(String response) {
            Log.d(TAG, response);
            if (!response.isEmpty()) {
                mSite = ParseJSONSites.parseSingleSiteResponse(response);
            } else {
                this.cancel(true);
            }
        }
    }

    @Override
    public void onClick(View v) {
        retryGetSite();
    }

    /**
     * Method that retry to get the information from server
     */
    public void retryGetSite()
    {
        mProgressBar.setVisibility(View.VISIBLE);
        mStatus.setText(R.string.getting_site);
        mRetry.setVisibility(View.GONE);
        getSite();
    }

    /**
     * Method that get a site information form the server
     */
    public void getSite()
    {
        boolean connection = AppUtils.checkNetworkConnection(getApplicationContext());
        if (!connection) {
            displayConnectionError();
            return;
        }
        GetSiteTask task = new GetSiteTask();
        task.execute((Void) null);
    }

    /**
     * Method that display a network connection error
     */
    public void displayConnectionError() {
        mProgressBar.setVisibility(View.GONE);
        mStatus.setText(R.string.no_network_available_retry);
        mRetry.setVisibility(View.VISIBLE);
    }

    /**
     * Method that display a server connection error
     */
    public void displayServerError() {
        mProgressBar.setVisibility(View.GONE);
        mStatus.setText(R.string.error_network_response);
        mRetry.setVisibility(View.VISIBLE);
    }

    /**
     * Method that display the site information in the screen
     * @param model the site information retrieved from the server
     */
    public void displayModelInView(Site model)
    {
        /*mSite = model;
        TextView name = (TextView) mInformationView.findViewById(R.id.site_name);
        TextView info = (TextView) mInformationView.findViewById(R.id.sites_info);
        ImageView image = (ImageView) mInformationView.findViewById(R.id.site_image);

        mProgress.setVisibility(View.GONE);
        mInformationView.setVisibility(View.VISIBLE);
        name.setText(mSite.getTitle());
        info.setText(mSite.getInformation());*/
        // TODO build a decent parser for wikipedia content
        mWikipedia.getSettings().setJavaScriptEnabled(true);
        mWikipedia.setWebViewClient(new WebViewClient());
        mWikipedia.loadUrl(WIKIPEDIA_PREFIX + model.getInformation());
        mProgress.setVisibility(View.GONE);
        mWikipedia.setVisibility(View.VISIBLE);
    }
}
