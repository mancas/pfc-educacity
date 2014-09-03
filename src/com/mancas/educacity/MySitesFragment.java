package com.mancas.educacity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mancas.adapters.MySitesAdapter;
import com.mancas.database.DBHelper;
import com.mancas.database.DBHelper.DBHelperCallback;
import com.mancas.database.Site.SiteEntry;
import com.mancas.database.DBTaskQuery;
import com.mancas.models.Site;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

public class MySitesFragment extends Fragment implements DBHelperCallback
{
    public static final String TAG = "My Sites Fragment";
    /**
     * An instance of {@link DBHelper} used to manage changes in user data
     */
    private DBHelper mDatabaseManager;
    /**
     * ListView where the user sites will be displayed
     */
    private ListView mSitesListView;
    /**
     * LinearLayout to display a warning message when the user has no sites
     */
    private LinearLayout mNoSites;
    /**
     * List of sites where the user has taken photos
     */
    private static List<Site> mItems = new ArrayList<Site>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabaseManager = DBHelper.getInstance(getActivity().getApplicationContext(), this);
        DBTaskQuery task = new DBTaskQuery(SiteEntry.TABLE_NAME_WITH_PREFIX,
                SiteEntry.TABLE_PROJECTION, null, null, null, null, SiteEntry.DEFUALT_TABLE_ORDER);
        mDatabaseManager.new AsyncSelect().execute(task);
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
        View rootView = inflater.inflate(R.layout.educacity_my_sites, container, false);
        mSitesListView = (ListView) rootView.findViewById(R.id.sites_list);
        mSitesListView.setAdapter(
          new MySitesAdapter(getActivity().getApplicationContext(), mItems));

        mSitesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });
        mNoSites = (LinearLayout) rootView.findViewById(R.id.no_sites);

        return rootView;
    }

    public void selectItem(int position)
    {
        // We must retrieve the ID from the selected item and start a new activity
        MySitesAdapter adapter = (MySitesAdapter) mSitesListView.getAdapter();
        Site site = adapter.getItem(position);
        Intent intent = new Intent(getActivity(), InfoActivity.class);
        intent.putExtra(SaveStateMapFragment.SITE_CLICKED, site.getId());
        intent.putExtra(SaveStateMapFragment.SITE_TITLE, site.getTitle());
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDatabaseOpen(SQLiteDatabase database) {
    }

    @Override
    public void onSelectReady(Cursor data) {
        if (data != null) {
            if (data.getCount() > 0 && mItems.size() > 0)
                mItems = new ArrayList<Site>();
            while (data.moveToNext()) {
                Site site = new Site();
                site.setId(data.getInt(data.getColumnIndex(SiteEntry._ID)));
                site.setTitle(data.getString(data.getColumnIndex(SiteEntry.COLUMN_NAME)));
                mItems.add(site);
            }
            if (mItems.size() > 0) {
                mNoSites.setVisibility(View.GONE);
                mSitesListView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onInsertReady(long id) {
    }

    @Override
    public void onUpdateReady(int rows) {
    }
}
