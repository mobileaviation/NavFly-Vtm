package com.mobileaviationtools.nav_fly.Search;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.mobileaviationtools.airnavdata.Entities.Airport;
import com.mobileaviationtools.nav_fly.GlobalVars;
import com.mobileaviationtools.nav_fly.R;
import com.mobileaviationtools.nav_fly.Route.Info.InfoItemAdapter;

import org.oscim.android.MapView;
import org.oscim.core.GeoPoint;
import org.oscim.core.MapPosition;
import org.oscim.event.Event;
import org.oscim.event.MotionEvent;
import org.oscim.layers.tile.bitmap.BitmapTileLayer;
import org.oscim.map.Map;
import org.oscim.tiling.source.bitmap.BitmapTileSource;
import org.oscim.tiling.source.bitmap.DefaultSources;

import java.util.List;


public class SearchMapFragment extends Fragment {
    public SearchMapFragment() {
        // Required empty public constructor
    }

    private static SearchMapFragment instance;
    private View view;
    private MapView mapView;
    private Map map;
    private BitmapTileSource tileSource;
    protected BitmapTileLayer bitmapLayer;
    private InfoItemAdapter infoItemAdapter;
    private SearchService service;
    private ListView searchMapItemsList;

    private String TAG = "SearchMapFragment";

    public static SearchMapFragment getInstance(GlobalVars vars)
    {
        if (instance == null) {
            SearchMapFragment instance = new SearchMapFragment();
            instance.vars = vars;
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
        map = mapView.map();

        searchMapItemsList = (ListView) view.findViewById(R.id.searchMapItemsList);

        setupMap();
        setListViewItemsInit(vars.airplaneLocation.getGeopoint());

        return view;
    }

    private void setupMap()
    {
        tileSource = DefaultSources.OPENSTREETMAP.build();
        bitmapLayer = new BitmapTileLayer(map, tileSource);
        map.layers().add(bitmapLayer);

        MapPosition pos = new MapPosition();
//        map.getMapPosition(pos);
//        if (pos.x == 0.5 && pos.y == 0.5)
//            map.setMapPosition(52.4603, 5.5272, Math.pow(2, 14));
        pos.set(0.5337957775766722, 0.43120260749218736, 4.0, 0.0f, 0.0f, 0.0f);
        map.setMapPosition(pos);


        map.input.bind(new Map.InputListener() {
            @Override
            public void onInputEvent(Event e, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    GeoPoint point = map.viewport().fromScreenPoint(motionEvent.getX(), motionEvent.getY());
                    setListViewItemsInit(point);
                    Log.i(TAG, point.toString());
                }
            }
        });
        map.events.bind(new Map.UpdateListener() {
            @Override
            public void onMapEvent(Event e, MapPosition mapPosition) {
                MapPosition pos = new MapPosition();
                pos.set(mapPosition.x, mapPosition.y, 4.0, 0.0f,0.0f, 0.0f);
                map.setMapPosition(pos);
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