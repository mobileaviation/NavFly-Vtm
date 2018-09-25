package com.mobileaviationtools.nav_fly.Route;

import android.content.Context;
import android.util.Log;
import com.mobileaviationtools.airnavdata.Entities.Airport;
import com.mobileaviationtools.nav_fly.Classes.MarkerDragEvent;
import org.oscim.backend.canvas.Color;
import org.oscim.core.GeoPoint;
import org.oscim.event.Gesture;
import org.oscim.event.MotionEvent;
import org.oscim.layers.marker.MarkerItem;
import org.oscim.layers.vector.PathLayer;
import org.oscim.map.Map;

import java.util.ArrayList;

public class Route extends ArrayList<Waypoint> {
    public Route(String name, Context context)
    {
        this.name = name;
        this.context = context;
        legs = new ArrayList<>();
    }

    private String TAG = "Route";

    public String name;
    public Context context;
    public Airport SelectedStartAirport;
    public Airport SelectedEndAirport;

    private ArrayList<Leg> legs;

    public void StartRoute()
    {
        Waypoint startWaypoint = Waypoint.CreateWaypoint(SelectedStartAirport);
        this.add(startWaypoint);

        Waypoint endWaypoint = Waypoint.CreateWaypoint(SelectedEndAirport);
        this.add(endWaypoint);

        Leg leg = new Leg(startWaypoint, endWaypoint);
        legs.add(leg);
    }

    private PathLayer routePathLayer;
    private WaypointLayer waypointLayer;
    private Map mMap;
    private MarkerDragEvent onWaypointDrag;


    public void DrawRoute(Map map)
    {
        mMap = map;
        if (size()>0) {
            if (routePathLayer == null) {
                createNewPathLayer(map);
                createNewMarkerLayer(map);
            }
            routePathLayer.clearPath();
            for (Waypoint w : this)
            {
                routePathLayer.addPoint(w.point);
                waypointLayer.PlaceMarker(w);
            }
            routePathLayer.update();
        }
    }

    private void createNewMarkerLayer(Map map)
    {
        waypointLayer = new WaypointLayer(map, null, context);
        map.layers().add(waypointLayer);
        setupWaypointDragging();
        waypointLayer.setMarkerDragEvent(onWaypointDrag);
    }

    private void setupWaypointDragging()
    {
        onWaypointDrag = new MarkerDragEvent() {
            @Override
            public void StartMarkerDrag(MarkerItem marker) {

            }

            @Override
            public void MarkerDragging(MarkerItem marker, GeoPoint newLocation) {

            }

            @Override
            public void EndMarkerDrag(MarkerItem markerItem, GeoPoint newLocation) {
                if (markerItem instanceof WaypointMarkerItem)
                {
                    WaypointMarkerItem item = (WaypointMarkerItem)markerItem;
                    item.UpdateWaypointLocation(newLocation);
                    DrawRoute(mMap);
                }
            }
        };
    }


    private void createNewPathLayer(Map map)
    {
        routePathLayer = new PathLayer(map, Color.BLUE, 10){
            @Override
            public boolean onGesture(Gesture g, MotionEvent e) {
                if (g instanceof Gesture.Tap) {
                    if (contains(e.getX(), e.getY())) {
                        Log.i(TAG, "Tapped on route, at: " + mMap.viewport().fromScreenPoint(e.getX(), e.getY()));
                        return true;
                    }
                }
                return false;
            }
        };

        map.layers().add(routePathLayer);
    }

    public void ClearRoute(Map map)
    {
        if (routePathLayer != null)
            routePathLayer.clearPath();
        if (waypointLayer != null) {
            waypointLayer.mItemList.clear();
            waypointLayer.populate();
        }
        SelectedStartAirport = null;
        SelectedEndAirport = null;
        legs.clear();
        this.clear();
    }
}
