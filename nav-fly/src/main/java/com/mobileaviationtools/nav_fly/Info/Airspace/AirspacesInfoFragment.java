package com.mobileaviationtools.nav_fly.Info.Airspace;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mobileaviationtools.airnavdata.Entities.Airspace;
import com.mobileaviationtools.nav_fly.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AirspacesInfoFragment extends Fragment {
    public AirspacesInfoFragment() {
        // Required empty public constructor
    }

    private View view;
    private AirspaceAdapter airspaceAdapter;
    private ListView airspacesListView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.view = inflater.inflate(R.layout.fragment_airspaces_info, container, false);
        airspacesListView = (ListView) view.findViewById(R.id.airspacesList);
        setListViewOnClick();
        return view;
    }

    private void setListViewOnClick()
    {
        airspacesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                airspacesListView.setAdapter(null);
            }
        });
    }

    public void ShowAirspacesInfo(Airspace[] airspaces)
    {
        if (airspaceAdapter == null) 
            airspaceAdapter = new AirspaceAdapter(airspaces);
        else airspaceAdapter.setAirspaces(airspaces);

        airspacesListView.setAdapter(airspaceAdapter);
        airspaceAdapter.notifyDataSetChanged();
    }
}
