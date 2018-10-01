package com.mobileaviationtools.nav_fly.Route;

import android.content.Context;
import android.util.Log;
import com.mobileaviationtools.airnavdata.Entities.Airport;
import com.mobileaviationtools.nav_fly.Classes.MarkerDragEvent;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
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

    private double indicatedAirspeed = 100;
    private double windDirection = 0;
    private double windSpeed = 0;

    public void setIndicatedAirspeed(double indicatedAirspeed)
    {
        this.indicatedAirspeed = indicatedAirspeed;
    }

    public void setWind(double windDirection, double windSpeed)
    {
        this.windDirection = windDirection;
        this.windSpeed = windSpeed;
    }

    private void setupRouteVariables()
    {
        for (Leg l: legs)
        {
            l.setupRouteVariables(this.indicatedAirspeed, this.windSpeed, this.windDirection);
        }
        if (routeEvents != null) routeEvents.RouteUpdated(this);
    }

    private ArrayList<Leg> legs;
    public Leg getLeg(Integer index)
    {
        return legs.get(index);
    }

    private RouteEvents routeEvents;

    public void setRouteEvents(RouteEvents routeEvents) {
        this.routeEvents = routeEvents;
    }

    public void StartRoute()
    {
        Waypoint startWaypoint = Waypoint.CreateWaypoint(SelectedStartAirport);
        this.add(startWaypoint);

        Waypoint endWaypoint = Waypoint.CreateWaypoint(SelectedEndAirport);
        this.add(endWaypoint);

        createLegs();

        if (routeEvents != null) routeEvents.NewRouteCreated(this);
    }

    public void createLegs()
    {
        legs.clear();
        for (int i = 1; i<this.size(); i++)
        {
            Waypoint start = get(i-1);
            Waypoint end = get(i);
            Leg leg = new Leg(start, end);
            legs.add(leg);
        }
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
                    updateLegs(item.getWaypoint());
                    DrawRoute(mMap);

                    if (routeEvents != null) routeEvents.WaypointUpdated(Route.this, item.getWaypoint());
                }
            }
        };
    }

    private void updateLegs(Waypoint waypoint)
    {
        if (waypoint.beforeLeg != null) waypoint.beforeLeg.UpdateLeg();
        if (waypoint.afterLeg != null) waypoint.afterLeg.UpdateLeg();
    }


    private void createNewPathLayer(Map map)
    {
        routePathLayer = new PathLayer(map, Color.BLUE, 10){
            @Override
            public boolean onGesture(Gesture g, MotionEvent e) {
                if (g instanceof Gesture.Tap) {
                    if (contains(e.getX(), e.getY())) {
                        Log.i(TAG, "Tapped on route, at: " + mMap.viewport().fromScreenPoint(e.getX(), e.getY()));
                        GeoPoint point = mMap.viewport().fromScreenPoint(e.getX(), e.getY());

                        Leg selectedLeg = findLeg(point);
                        if (selectedLeg != null)
                        {
                            Waypoint newWaypoint = InsertnewWaypoint(point, selectedLeg);
                            createLegs();
                            clearPathLayer();
                            DrawRoute(mMap);
                            if (routeEvents != null) routeEvents.NewWaypointInserted(Route.this, newWaypoint);
                        }

                        return true;
                    }
                }
                return false;
            }
        };

        map.layers().add(routePathLayer);
    }

    private Waypoint InsertnewWaypoint(GeoPoint point, Leg selectedLeg)
    {
        Waypoint newWaypoint = new Waypoint(point);
        newWaypoint.name = "LON"+point.longitudeE6 + " LAT"+ point.latitudeE6;
        Integer index = indexOf(selectedLeg.endWaypoint);
        Route.this.add(index, newWaypoint);
        return  newWaypoint;
    }

    private Leg findLeg(GeoPoint point)
    {
        Leg return_leg = null;
        for (Leg leg: legs)
        {
            GeometryFactory geometryFactory = new GeometryFactory();
            Geometry leg_line = geometryFactory.createLineString(leg.getLegCoordinates());
            Geometry buffer = leg_line.buffer(0.01);
            Geometry gpoint = geometryFactory.createPoint(new Coordinate(point.getLongitude(), point.getLatitude()));
            if (buffer.contains(gpoint))
            {
                return_leg = leg;
            }
        }
        return return_leg;
    }

    private void clearPathLayer() {
        if (routePathLayer != null)
            routePathLayer.clearPath();
    }

    private void clearMarkerLayer(){
        if (waypointLayer != null) {
            waypointLayer.mItemList.clear();
            waypointLayer.populate();
        }
    }

    public void ClearRoute(Map map)
    {
        clearPathLayer();
        clearMarkerLayer();
        SelectedStartAirport = null;
        SelectedEndAirport = null;
        legs.clear();
        this.clear();
    }
}
