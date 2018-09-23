package com.mobileaviationtools.nav_fly.Route;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.mobileaviationtools.airnavdata.Entities.Airport;
import com.mobileaviationtools.nav_fly.MainActivity;

import org.oscim.backend.canvas.Color;
import org.oscim.core.GeoPoint;
import org.oscim.event.Gesture;
import org.oscim.event.MotionEvent;
import org.oscim.layers.vector.PathLayer;
import org.oscim.map.Map;

import java.util.ArrayList;

public class Route extends ArrayList<Leg> {
    public Route(String name, Context context)
    {
        this.name = name;
    }

    private String TAG = "Route";

    public String name;
    public Context context;
    public Airport SelectedStartAirport;
    public Airport SelectedEndAirport;

    public void StartRoute()
    {
        Leg leg = new Leg(SelectedStartAirport, SelectedEndAirport);
        this.add(leg);
    }

    private PathLayer routePathLayer;
    private WaypointLayer waypointLayer;
    private Map mMap;
    public void DrawRoute(Map map)
    {
        mMap = map;
        if (size()>0) {
            if (routePathLayer == null) {
                createNewPathLayer(map);
                createNewMarkerLayer(map);
            }
            routePathLayer.clearPath();
            for (Leg l : this)
            {
                routePathLayer.addPoint(l.startWaypoint.point);
                waypointLayer.PlaceMarker(l.startWaypoint);
            }
            routePathLayer.addPoint(this.get(this.size()-1).endWaypoint.point);
            waypointLayer.PlaceMarker(this.get(this.size()-1).endWaypoint);
            routePathLayer.update();
        }
    }

    private void createNewMarkerLayer(Map map)
    {
        waypointLayer = new WaypointLayer(map, null, context);
        map.layers().add(waypointLayer);
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
        SelectedStartAirport = null;
        SelectedEndAirport = null;
        this.clear();
    }
}
