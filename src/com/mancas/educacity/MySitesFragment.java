package com.mancas.educacity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MySitesFragment extends Fragment
{
    public static final String TAG = "My Sites Fragment";
    private ListView mSitesListView;
    private static List<String> mItems = new ArrayList<String>();
    static {
        mItems.add("Giralda");
        mItems.add("Torre del oro");
        mItems.add("Acueducto Luis montoto");
        mItems.add("Giraldillo");
        mItems.add("Rampa");
        mItems.add("Parque Maria Luisa");
        mItems.add("Sevilla Este");
        mItems.add("Los remedios");
        mItems.add("Aeropuerto");
        mItems.add("Archivo de Indias");
        mItems.add("Tienda Guiri");
        mItems.add("Reina Mercedes");
        mItems.add("El Salvador");
        mItems.add("Nervión");
        mItems.add("Los Arcos");
        mItems.add("Zona Este");
        mItems.add("Palacio de Congresos");
        mItems.add("La conchinchina");
        mItems.add("Aquapark");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        Collections.sort(mItems);
        mSitesListView.setAdapter(
          new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, mItems));

        mSitesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });

        return rootView;
    }
    
    public void selectItem(int position)
    {
        // We must retrieve the ID from the selected item and start a new activity
        Intent intent = new Intent(getActivity(), InfoActivity.class);
        startActivity(intent);
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
        	Log.d(TAG, e.getMessage());
        }*/
    }
}
