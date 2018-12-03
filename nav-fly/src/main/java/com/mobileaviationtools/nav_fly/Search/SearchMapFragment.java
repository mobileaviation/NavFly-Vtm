package com.mobileaviationtools.nav_fly.Search;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobileaviationtools.nav_fly.GlobalVars;
import com.mobileaviationtools.nav_fly.R;

import org.oscim.android.MapView;
import org.oscim.core.GeoPoint;
import org.oscim.core.MapPosition;
import org.oscim.event.Event;
import org.oscim.event.EventDispatcher;
import org.oscim.event.MotionEvent;
import org.oscim.layers.tile.bitmap.BitmapTileLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.map.Map;
import org.oscim.theme.VtmThemes;
import org.oscim.tiling.TileSource;
import org.oscim.tiling.source.OkHttpEngine;
import org.oscim.tiling.source.bitmap.BitmapTileSource;
import org.oscim.tiling.source.bitmap.DefaultSources;
import org.oscim.tiling.source.oscimap4.OSciMap4TileSource;


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

        setupMap();

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


}

//[X:0.5337957775766722, Y:0.43120260749218736, Z:2] lat:24.029938353709085, lon:12.166479927601985
//scale = 4.0
//zoomLevel = 2
//bearing = 0.0
//roll = 0.0
//tilt = 0.0