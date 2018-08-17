package com.mobileaviationtools.nav_fly.Markers.Airport;

import android.content.Context;
import android.util.Log;

import com.mobileaviationtools.airnavdata.AirnavDatabase;
import com.mobileaviationtools.airnavdata.Entities.Airport;

import org.oscim.core.BoundingBox;
import org.oscim.layers.marker.ItemizedLayer;
import org.oscim.layers.marker.MarkerItem;
import org.oscim.layers.marker.MarkerSymbol;
import org.oscim.map.Map;

import java.util.List;

//import org.locationtech.jts.geom.

public class AirportMarkersLayer extends ItemizedLayer{ //} implements Map.InputListener {
    private final String TAG = "AirportMarkersLayer";
    private Context context;
    private AirnavDatabase db;
    private List<AirportMarkerItem>  items;

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


    public AirportMarkersLayer(Map map, MarkerSymbol defaultMarker) {
        super(map, defaultMarker);
    }

    public AirportMarkersLayer(Map map, MarkerSymbol defaultMarker, Context context)
    {
        this(map, defaultMarker);
        this.context = context;
        setOnItemGestureListener();
        db = AirnavDatabase.getInstance(context);
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
        // Check if this airport(Marker) is already added to the list of markers
        if (!mItemList.contains(markerItem)) {
            // If new marker, get the runways (and frequencies)
            // Initiate the Symbol
            // Add it to the Layer
            getRunways(airport);
            markerItem.InitMarker();
            this.addItem(markerItem);
        }
    }

    private Airport[] getAirports()
    {
        BoundingBox box = mMap.getBoundingBox(0);
        return db.getAirport().getAirportsWithinBounds(box.getMinLongitude(),
                box.getMaxLongitude(),
                box.getMaxLatitude(),
                box.getMinLatitude()
        );
    }

    private void getRunways (Airport airport)
    {
        airport.runways = db.getRunways().getRunwaysByAirport(airport.id);
    }

    public void UpdateAirports()
    {
        placeMarkers(getAirports());
        update();
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
