package com.mobileaviationtools.nav_fly.Markers.Navaids;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.mobileaviationtools.airnavdata.AirnavDatabase;
import com.mobileaviationtools.airnavdata.Entities.Navaid;
import com.mobileaviationtools.nav_fly.Markers.Airport.AirportMarkerItem;

import org.oscim.core.BoundingBox;
import org.oscim.core.MapPosition;
import org.oscim.layers.marker.ItemizedLayer;
import org.oscim.layers.marker.MarkerInterface;
import org.oscim.layers.marker.MarkerItem;
import org.oscim.layers.marker.MarkerSymbol;
import org.oscim.map.Map;

import java.util.ArrayList;

public class NaviadMarkersLayer extends ItemizedLayer {
    private final String TAG = "AirportMarkersLayer";
    private Context context;
    private AirnavDatabase db;

    class CombinedMarkerItem
    {
        public CombinedMarkerItem(NavaidMarkerItem item, NavaidNameMarkerItem nameItem)//, AirportNameMarkerItem nameItem)
        {
            this.item = item;
            this.nameItem = nameItem;
        }
        public NavaidMarkerItem item;
        public NavaidNameMarkerItem nameItem;

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof CombinedMarkerItem) {
                return (((CombinedMarkerItem)obj).item.getHavaid().ident.equals(this.item.getHavaid().ident));
            }
            else
                return false;
        }
    }

    private ArrayList<CombinedMarkerItem> all_items;

    public NaviadMarkersLayer(Map map, MarkerSymbol defaultMarker) {
        super(map, defaultMarker);
    }

    public NaviadMarkersLayer(Map map, MarkerSymbol defaultMarker, Context context)
    {
        this(map, defaultMarker);
        this.context = context;
        all_items = new ArrayList<>();
        setOnItemGestureListener();
        db = AirnavDatabase.getInstance(context);
    }

    private OnItemGestureListener<MarkerInterface> onItemGestureListener;
    private void setOnItemGestureListener()
    {
        onItemGestureListener = new OnItemGestureListener<MarkerInterface>() {
            @Override
            public boolean onItemSingleTapUp(int index, MarkerInterface item) {
                Log.i(TAG,"Single Tab on : " + item.toString() + " : " + ((NavaidMarkerItem) item).title );
                return false;
            }

            @Override
            public boolean onItemLongPress(int index, MarkerInterface item) {
                return false;
            }
        };

        this.setOnItemGestureListener(onItemGestureListener);
    }

    private void placeMarkers(Navaid[] navaids)
    {
        for(Navaid navaid : navaids)
        {
            placeMarker(navaid);
        }
    }

    private void placeMarker(Navaid navaid)
    {
        // Create an empty MarkerItem with the correspoding airport
        NavaidMarkerItem markerItem = new NavaidMarkerItem(navaid, context);
        NavaidNameMarkerItem markerNameItem = new NavaidNameMarkerItem(navaid, context);
        //AirportMarkersLayer.CombinedMarkerItem combinedMarkerItem = new AirportMarkersLayer.CombinedMarkerItem(markerItem, markerInfoItem);
        NaviadMarkersLayer.CombinedMarkerItem combinedMarkerItem = new NaviadMarkersLayer.CombinedMarkerItem(markerItem, markerNameItem);
        // Check if this airport(Marker) is already added to the list of markers
        if (!all_items.contains(combinedMarkerItem)) {
            // If new marker, get the runways (and frequencies)
            // Initiate the Symbol
            // Add it to the Layer
            markerItem.InitMarker();
            markerNameItem.InitMarker();
            this.all_items.add(combinedMarkerItem);
        }
    }

    private Navaid[] getNavaids()
    {
        BoundingBox box = mMap.getBoundingBox(0);
        return db.getNavaids().getNavaidsWithinBoundsByTypes(box.getMinLongitude(),
                box.getMaxLongitude(),
                box.getMaxLatitude(),
                box.getMinLatitude()
        );
    }

    private void updateLayer()
    {
        // zoom check =>10 add text marker
        // zoom check =9 small,medium, large
        // zoom level check =8 medium,arge
        // zoom lever check =<7 large

        BoundingBox mapBox = mMap.getBoundingBox(0);
        MapPosition mapPos = mMap.getMapPosition();


        for (CombinedMarkerItem combinedMarkerItem : all_items) {
            if (combinedMarkerItem.item.WithinBounds(mapBox)) {
                // TODO: check on Power of the navaid, highpower stays visible longer than lowpower
                if (!this.mItemList.contains(combinedMarkerItem.item)) {
                    if (mapPos.zoomLevel > 8) AddMarkerItem(combinedMarkerItem);
                }
                else
                {
                    if (mapPos.zoomLevel < 9) RemoveMarkerItem(combinedMarkerItem);
                }
            }
        }
    }

    private void AddMarkerItem(CombinedMarkerItem combinedMarkerItem)
    {
        this.addItem(combinedMarkerItem.item);
        this.addItem(combinedMarkerItem.nameItem);
    }

    private void RemoveMarkerItem(CombinedMarkerItem combinedMarkerItem)
    {
        this.removeItem(combinedMarkerItem.item);
        this.removeItem(combinedMarkerItem.nameItem);
    }

    public void UpdateNavaids()
    {
        class UpdateMapAsync extends AsyncTask<Void, Void, Void>
        {
            @Override
            protected Void doInBackground(Void... voids) {
                placeMarkers(getNavaids());
                updateLayer();
                update();
                return null;
            }
        }

        new UpdateMapAsync().execute();
        //new UpdateMapAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
    }
}
