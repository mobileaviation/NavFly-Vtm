package com.mobileaviationtools.nav_fly.Markers.Navaids;

import android.content.Context;
import android.util.Log;

import com.mobileaviationtools.airnavdata.AirnavDatabase;

import org.oscim.layers.marker.ItemizedLayer;
import org.oscim.layers.marker.MarkerItem;
import org.oscim.layers.marker.MarkerSymbol;
import org.oscim.map.Map;

import java.util.ArrayList;

public class NaviadMarkersLayer extends ItemizedLayer {
    private final String TAG = "AirportMarkersLayer";
    private Context context;
    private AirnavDatabase db;

    public NaviadMarkersLayer(Map map, MarkerSymbol defaultMarker) {
        super(map, defaultMarker);
    }

    public NaviadMarkersLayer(Map map, MarkerSymbol defaultMarker, Context context)
    {
        this(map, defaultMarker);
        this.context = context;
        //all_items = new ArrayList<>();
        setOnItemGestureListener();
        db = AirnavDatabase.getInstance(context);
    }

    private OnItemGestureListener<MarkerItem> onItemGestureListener;
    private void setOnItemGestureListener()
    {
        onItemGestureListener = new OnItemGestureListener<MarkerItem>() {
            @Override
            public boolean onItemSingleTapUp(int index, MarkerItem item) {
                Log.i(TAG,"Single Tab on : " + item.toString() + " : " + item.title );
                return false;
            }

            @Override
            public boolean onItemLongPress(int index, MarkerItem item) {
                return false;
            }
        };

        this.setOnItemGestureListener(onItemGestureListener);
    }
}
