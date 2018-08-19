package com.mobileaviationtools.nav_fly.Markers.Airport;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.mobileaviationtools.airnavdata.AirnavDatabase;
import com.mobileaviationtools.airnavdata.Classes.AirportType;
import com.mobileaviationtools.airnavdata.Entities.Airport;

import org.oscim.core.BoundingBox;
import org.oscim.core.MapPosition;
import org.oscim.layers.marker.ItemizedLayer;
import org.oscim.layers.marker.MarkerItem;
import org.oscim.layers.marker.MarkerSymbol;
import org.oscim.map.Map;

import java.util.ArrayList;
import java.util.List;

//import org.locationtech.jts.geom.

public class AirportMarkersLayer extends ItemizedLayer{ //} implements Map.InputListener {
    private final String TAG = "AirportMarkersLayer";
    private Context context;
    private AirnavDatabase db;

    class CombinedMarkerItem
    {
        public CombinedMarkerItem(AirportMarkerItem item, AirportInfoMarkerItem infoItem)
        {
            this.item = item;
            this.infoItem = infoItem;
        }
        public AirportMarkerItem item;
        public AirportInfoMarkerItem infoItem;

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof CombinedMarkerItem) {
                return (((CombinedMarkerItem)obj).item.getAirport().ident.equals(this.item.getAirport().ident));
            }
            else
                return false;
        }
    }

    private List<CombinedMarkerItem>  all_items;
    private List<String> visibleTypes;

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


    private AirportMarkersLayer(Map map, MarkerSymbol defaultMarker) {
        super(map, defaultMarker);
    }

    public AirportMarkersLayer(Map map, MarkerSymbol defaultMarker, Context context)
    {
        this(map, defaultMarker);
        this.context = context;
        all_items = new ArrayList<>();
        setOnItemGestureListener();
        db = AirnavDatabase.getInstance(context);
    }

    private void initVisibleTypes(int zoomlevel)
    {
        visibleTypes = new ArrayList<>();
        visibleTypes.add(AirportType.large_airport.toString());
        if (zoomlevel>7) visibleTypes.add(AirportType.medium_airport.toString());
        if (zoomlevel>8) visibleTypes.add(AirportType.small_airport.toString());
        //visibleTypes.add(AirportType.balloonport);
        //visibleTypes.add(AirportType.heliport);
        //visibleTypes.add(AirportType.seaplane_base);
        //visibleTypes.add(AirportType.closed);
    }

    private void placeMarkers(Airport[] airports)
    {
        for(Airport airport:airports)
        {
            placeMarker(airport);
        }
    }

    private void placeMarker(Airport airport)
    {
        // Create an empty MarkerItem with the correspoding airport
        AirportMarkerItem markerItem = new AirportMarkerItem(airport, context);
        AirportInfoMarkerItem markerInfoItem = new AirportInfoMarkerItem(airport, context);
        CombinedMarkerItem combinedMarkerItem = new CombinedMarkerItem(markerItem, markerInfoItem);
        // Check if this airport(Marker) is already added to the list of markers
        if (!all_items.contains(combinedMarkerItem)) {
            // If new marker, get the runways (and frequencies)
            // Initiate the Symbol
            // Add it to the Layer
            getRunways(airport);
            markerItem.InitMarker();
            markerInfoItem.InitMarker();
            //this.addItem(markerItem);
            this.all_items.add(combinedMarkerItem);
        }
    }

    private Airport[] getAirports()
    {
        BoundingBox box = mMap.getBoundingBox(0);
        return db.getAirport().getAirportsWithinBoundsByTypes(box.getMinLongitude(),
                box.getMaxLongitude(),
                box.getMaxLatitude(),
                box.getMinLatitude(),
                visibleTypes
        );
    }

    private void getRunways (Airport airport)
    {
        airport.runways = db.getRunways().getRunwaysByAirport(airport.id);
    }

    private void updateLayer()
    {
        // zoom check =>10 add text marker
        // zoom check =9 small,medium, large
        // zoom level check =8 medium,arge
        // zoom lever check =<7 large

        BoundingBox mapBox = mMap.getBoundingBox(0);
        MapPosition mapPos = mMap.getMapPosition();

        for (CombinedMarkerItem combinedItem : all_items)
        {
            if (combinedItem.item.WithinBounds(mapBox))
            {
                if (!this.mItemList.contains(combinedItem.item))
                {
                    boolean text = (mapPos.zoomLevel > 8);
                    switch (combinedItem.item.getAirportType())
                    {
                        case small_airport: if (mapPos.zoomLevel > 8) AddMarkerItem(combinedItem, text); break;
                        case medium_airport: if (mapPos.zoomLevel > 7) AddMarkerItem(combinedItem, text); break;
                        case large_airport: AddMarkerItem(combinedItem, text); break;
                    }
                }
                else
                {
                    switch (combinedItem.item.getAirportType())
                    {
                        case small_airport: if (mapPos.zoomLevel < 9) RemoveMarkerItem(combinedItem); break;
                        case medium_airport: if (mapPos.zoomLevel < 8) RemoveMarkerItem(combinedItem); break;
                    }

                    boolean text = (mapPos.zoomLevel > 8);
                    if (!this.mItemList.contains(combinedItem.infoItem))
                    {
                        if (text) this.addItem(combinedItem.infoItem);
                    }

                }
            }
        }
    }

    private void AddMarkerItem(CombinedMarkerItem combinedMarkerItem, boolean text)
    {
        this.addItem(combinedMarkerItem.item);
        if (text) this.addItem(combinedMarkerItem.infoItem);
        else this.removeItem(combinedMarkerItem.infoItem);
    }

    private void RemoveMarkerItem(CombinedMarkerItem combinedMarkerItem)
    {
        this.removeItem(combinedMarkerItem.item);
        this.removeItem(combinedMarkerItem.infoItem);
    }

    public void UpdateAirports()
    {
        class UpdateMapAsync extends AsyncTask<Void, Void, Void>
        {
            @Override
            protected Void doInBackground(Void... voids) {
                initVisibleTypes(mMap.getMapPosition().zoomLevel);
                placeMarkers(getAirports());
                updateLayer();
                update();
                return null;
            }
        }

        new UpdateMapAsync().execute();
    }

//    @Override
//    public boolean onGesture(Gesture g, MotionEvent e) {
//        if (g instanceof Gesture.Tap)
//            return false;
//
//        if (g instanceof Gesture.LongPress)
//            return false;
//
//        Log.i(TAG, "Gesture: " + g.toString());
//        Log.i(TAG, "MotionEvent: " + e.toString());

//        return false;
//    }

//    @Override
//    public void onInputEvent(Event e, MotionEvent motionEvent) {
//        Log.i(TAG, "MotionEvent: " + motionEvent.getAction());
//        if (motionEvent.getAction()==2) {
            //Log.i(TAG, "MotionEvent: " + motionEvent.getAction());
//            MapPosition pos = mMap.getMapPosition();
//            Log.i(TAG, "Position X: " + pos.getLatitude() + " Y: " + pos.getLongitude() + " z: " + pos.getZoom());
//            BoundingBox box = mMap.getBoundingBox(0);
//            Log.i(TAG, "Box: W: " + box.getMinLongitude() + " E: " + box.getMaxLongitude() + " N: " + box.getMaxLatitude() + " S: " + box.getMinLatitude());
//
//            Airport[] airports = db.getAirport().getAirportsWithinBounds(box.getMinLongitude(),
//                    box.getMaxLongitude(),
//                    box.getMaxLatitude(),
//                    box.getMinLatitude()
//                    );
//            placeMarkers(airports);
//
//            Airport airport = db.getAirport().getLelystad();
//
//            Log.i(TAG, "Test" + airport.ident);

//            placeMarkers(getAirports());
//        }

//    }
}
