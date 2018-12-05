package com.mobileaviationtools.nav_fly.Search;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mobileaviationtools.airnavdata.Entities.Airport;
import com.mobileaviationtools.airnavdata.Entities.Fix;
import com.mobileaviationtools.airnavdata.Entities.Navaid;
import com.mobileaviationtools.nav_fly.GlobalVars;
import com.mobileaviationtools.nav_fly.R;
import com.mobileaviationtools.nav_fly.Route.Info.InfoItemAdapter;
import com.mobileaviationtools.nav_fly.Route.Weather.Station;


import org.oscim.core.GeoPoint;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapView;

import java.util.List;


public class SearchMapFragment extends Fragment {
    public SearchMapFragment() {
        // Required empty public constructor
    }

    private static SearchMapFragment instance;
    private View view;
    private MapView mapView;
    private InfoItemAdapter infoItemAdapter;
    private SearchService service;
    private ListView searchMapItemsList;
    private SearchDialog.OnSearch onSearch;

    private String TAG = "SearchMapFragment";

    public static SearchMapFragment getInstance(GlobalVars vars, SearchDialog.OnSearch onSearch)
    {
        if (instance == null) {
            SearchMapFragment instance = new SearchMapFragment();
            instance.vars = vars;
            instance.onSearch = onSearch;
            return instance;
        }
        else
            return instance;
    }

    private GlobalVars vars;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_search_map, container, false);

        mapView = (MapView) view.findViewById(R.id.searchMapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.getController().setZoom(2d);
        mapView.setTilesScaledToDpi(true);

        searchMapItemsList = (ListView) view.findViewById(R.id.searchMapItemsList);
        searchMapItemsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Object station = adapterView.getAdapter().getItem(i);
                Station s = new Station(vars.context);
                if (station instanceof Airport)  s.airport = (Airport)station;
                if (station instanceof Navaid)  s.navaid = (Navaid)station;
                if (station instanceof Fix)  s.fix = (Fix)station;

                if(onSearch != null) onSearch.FoundStation(s);
            }
        });

        setupMap();
        return view;
    }

    private void setupMap()
    {
        mapView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    IGeoPoint g = mapView.getProjection().fromPixels((int) motionEvent.getX(), (int) motionEvent.getY());
                    Log.i(TAG, motionEvent.toString() + " " + g.toString());
                    GeoPoint geoPoint = new GeoPoint(g.getLatitude(), g.getLongitude());
                    setListViewItemsInit(geoPoint);
                }
                return false;
            }
        });

    }

    private void setListViewItemsInit(GeoPoint point)
    {
        if (service == null) service = new SearchService(vars);
        if (infoItemAdapter == null) infoItemAdapter = new InfoItemAdapter(vars.context);
        infoItemAdapter.setAirports(service.getAirportsByLocationLimit(point, 50l));
        searchMapItemsList.setAdapter(infoItemAdapter);
        infoItemAdapter.notifyDataSetChanged();
    }




}

//[X:0.5337957775766722, Y:0.43120260749218736, Z:2] lat:24.029938353709085, lon:12.166479927601985
//scale = 4.0
//zoomLevel = 2
//bearing = 0.0
//roll = 0.0
//tilt = 0.0