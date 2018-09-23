package com.mobileaviationtools.nav_fly.Route;

import android.content.Context;

import com.mobileaviationtools.airnavdata.AirnavDatabase;
import com.mobileaviationtools.nav_fly.Markers.Airport.AirportMarkerItem;
import com.mobileaviationtools.nav_fly.Markers.Airport.AirportMarkersLayer;

import org.oscim.layers.marker.ItemizedLayer;
import org.oscim.layers.marker.MarkerItem;
import org.oscim.layers.marker.MarkerSymbol;
import org.oscim.map.Map;

import java.util.ArrayList;
import java.util.List;

public class WaypointLayer extends ItemizedLayer {
    private final String TAG = "WaypointLayer";
    private Context context;
    private List<WaypointMarkerItem> all_items;

    private WaypointLayer(Map map, MarkerSymbol defaultMarker) {
        super(map, defaultMarker);
    }
    public WaypointLayer(Map map, MarkerSymbol defaultMarker, Context context)
    {
        this(map, defaultMarker);
        this.context = context;
        all_items = new ArrayList<>();
        setOnItemGestureListener();

    }

    private OnItemGestureListener<MarkerItem> onItemGestureListener;
    private void setOnItemGestureListener()
    {
        onItemGestureListener = new OnItemGestureListener<MarkerItem>() {
            @Override
            public boolean onItemSingleTapUp(int index, MarkerItem item) {
                if (item instanceof WaypointMarkerItem)
                {

                }

                return false;
            }

            @Override
            public boolean onItemLongPress(int index, MarkerItem item) {
                return false;
            }
        };

        this.setOnItemGestureListener(onItemGestureListener);
    }

    public void PlaceMarker(Waypoint waypoint)
    {
        WaypointMarkerItem item = new WaypointMarkerItem(waypoint, context);
        if (!all_items.contains(item)) {
            item.InitMarker();
            this.addItem(item);
            this.all_items.add(item);
        }
    }
}

