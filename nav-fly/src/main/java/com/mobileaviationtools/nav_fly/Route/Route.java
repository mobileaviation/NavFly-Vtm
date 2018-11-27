package com.mobileaviationtools.nav_fly.Route;

import android.content.Context;
import android.util.Log;

import com.mobileaviationtools.airnavdata.AirnavDatabase;
import com.mobileaviationtools.airnavdata.AirnavRouteDatabase;
import com.mobileaviationtools.airnavdata.Entities.Airport;
import com.mobileaviationtools.airnavdata.Entities.Fix;
import com.mobileaviationtools.airnavdata.Entities.Navaid;
import com.mobileaviationtools.nav_fly.Classes.MarkerDragEvent;
import com.mobileaviationtools.nav_fly.Location.FspLocation;
import com.mobileaviationtools.nav_fly.R;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.oscim.android.canvas.AndroidGraphics;
import org.oscim.backend.canvas.Color;
import org.oscim.core.GeoPoint;
import org.oscim.event.Gesture;
import org.oscim.event.MotionEvent;
import org.oscim.layers.marker.MarkerItem;
import org.oscim.layers.vector.PathLayer;
import org.oscim.layers.vector.geometries.Style;
import org.oscim.map.Map;
import org.oscim.renderer.bucket.TextureItem;
import org.oscim.theme.styles.LineStyle;

import java.util.ArrayList;
import java.util.Date;

public class Route extends ArrayList<Waypoint> {
    public Route(String name, Context context)
    {
        this.name = name;
        this.id = -1l;
        this.context = context;
        this.createdDate = new Date();
        legs = new ArrayList<>();
    }

    private String TAG = "Route";

    public String name;
    public Long id;
    public Date createdDate;
    public Date modifiedDate;
    public Context context;

    private Airport SelectedStartAirport;

    public void setSelectedStartAirport(Airport selectedStartAirport) {
        SelectedStartAirport = selectedStartAirport;
        setStartAirport();
    }
    public boolean isStartAirportSet()
    {
        return (SelectedStartAirport != null);
    }

    private Airport SelectedEndAirport;

    public void setSelectedEndAirport(Airport selectedEndAirport) {
        SelectedEndAirport = selectedEndAirport;
        setEndAirport();
    }

    public boolean isEndAirportSet()
    {
        return (SelectedEndAirport != null);
    }

    private double indicatedAirspeed = 100;

    public double getWindSpeed() {
        return windSpeed;
    }

    private double windDirection = 0;

    public double getWindDirection() {
        return windDirection;
    }

    private double windSpeed = 0;

    public double getIndicatedAirspeed() {
        return indicatedAirspeed;
    }

    public void setWindAndAirspeed(double windDirection, double windSpeed, double indicatedAirspeed)
    {
        this.windDirection = windDirection;
        this.windSpeed = windSpeed;
        this.indicatedAirspeed = indicatedAirspeed;
        setupRouteVariables();
        if (routeEvents != null) routeEvents.RouteUpdated(this);
    }

    private void setupRouteVariables()
    {
        double distance = 0;
        double time = 0;
        for (Leg l: legs)
        {
            l.setupRouteVariables(this);
            distance = distance + l.getDistanceNM();
            l.setTotalDistanceNm(distance);
            time = time + l.getLegTimeMinutes();
            l.setTotalTimeMin(time);
        }
    }

    private ArrayList<Leg> legs;

    public ArrayList<Leg> getLegs() {
        return legs;
    }

    public Leg getLeg(Integer index)
    {
        return legs.get(index);
    }

    private RouteEvents routeEvents;

    public void setRouteEvents(RouteEvents routeEvents) {
        this.routeEvents = routeEvents;
    }

    private void setStartAirport()
    {
        Waypoint startWaypoint = Waypoint.CreateWaypoint(SelectedStartAirport);
        this.add(startWaypoint);

        if (routeEvents != null) routeEvents.NewRouteCreated(this);
    }

    private void setEndAirport()
    {
        Waypoint endWaypoint = Waypoint.CreateWaypoint(SelectedEndAirport);
        this.add(endWaypoint);

        createLegs();
        setupRouteVariables();

        if (routeEvents != null) routeEvents.RouteUpdated(this);
    }

    public void createLegs()
    {
        legs.clear();
        for (int i = 1; i<this.size(); i++)
        {
            Waypoint start = get(i-1);
            Waypoint end = get(i);
            Leg leg = new Leg(start, end, this);
            legs.add(leg);
        }
    }

    private RoutePathLayer routePathLayer;
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
                routePathLayer.AddWaypoint(w);
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
                    setupRouteVariables();
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
        routePathLayer = new RoutePathLayer(map, 0xFF84e900, 10){
            @Override
            public boolean onGesture(Gesture g, MotionEvent e) {
                if (g instanceof Gesture.Tap) {
                    if (contains(e.getX(), e.getY())) {
                        Log.i(TAG, "Tapped on route, at: " + mMap.viewport().fromScreenPoint(e.getX(), e.getY()));
                        GeoPoint point = mMap.viewport().fromScreenPoint(e.getX(), e.getY());

                        Leg selectedLeg = findLeg(point);
                        if (selectedLeg != null)
                        {
                            // Test
                            //routePathLayer.SelectLeg(selectedLeg);

                            Waypoint newWaypoint = InsertnewWaypoint(point, selectedLeg);
                            createLegs();
                            setupRouteVariables();
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

        routePathLayer.AddLayer(map, this);
        //map.layers().add(routePathLayer);
    }

    private Waypoint InsertnewWaypoint(GeoPoint point, Leg selectedLeg)
    {
        Waypoint newWaypoint = new Waypoint(point);
        newWaypoint.name = "LON"+point.longitudeE6 + " LAT"+ point.latitudeE6;
        newWaypoint.type = WaypointType.waypoint;
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

    public void saveRoute(String name)
    {
        AirnavRouteDatabase db = AirnavRouteDatabase.getInstance(context);

        com.mobileaviationtools.airnavdata.Entities.Route routeEntity = new
                com.mobileaviationtools.airnavdata.Entities.Route();
        routeEntity.name = name;
        this.modifiedDate = new Date();
        routeEntity.modifiedDate = this.modifiedDate.getTime();
        routeEntity.createdDate = this.createdDate.getTime();

        this.id = db.getRoute().InsertRoute(routeEntity);

        for (Waypoint p: this)
        {
            com.mobileaviationtools.airnavdata.Entities.Waypoint waypointEntity = new
                    com.mobileaviationtools.airnavdata.Entities.Waypoint();
            waypointEntity.index = this.indexOf(p);
            waypointEntity.latitude = p.point.getLatitude();
            waypointEntity.longitude = p.point.getLongitude();
            waypointEntity.type = p.type.toString();
            waypointEntity.name = p.name;
            waypointEntity.route_id = id;

            switch (p.type)
            {
                case airport:{
                    if (p.ref instanceof Airport)
                        waypointEntity.ref = ((Airport)p.ref).id.longValue();
                    break;
                }
                case fix:{
                    if (p.ref instanceof Fix)
                        waypointEntity.ref = ((Fix)p.ref).id.longValue();
                    break;
                }
                case navaid:{
                    if (p.ref instanceof Navaid)
                        waypointEntity.ref = ((Navaid)p.ref).id.longValue();
                    break;
                }
                case waypoint:{
                    waypointEntity.ref = -1l;
                    break;
                }
            }

            db.getWaypoint().InsertWaypoint(waypointEntity);
        }


    }

    public void openRoute(Long routeId, Map map)
    {
        mMap = map;
        AirnavDatabase a_db = AirnavDatabase.getInstance(context);
        AirnavRouteDatabase db = AirnavRouteDatabase.getInstance(context);
        com.mobileaviationtools.airnavdata.Entities.Waypoint[] waypoints = db.getWaypoint().GetWaypointsByRouteID(routeId);

        for (com.mobileaviationtools.airnavdata.Entities.Waypoint db_waypoint : waypoints)
        {
            Waypoint newWaypoint = new Waypoint(db_waypoint.latitude, db_waypoint.longitude);
            newWaypoint.type = WaypointType.valueOf(db_waypoint.type);

            switch (newWaypoint.type)
            {
                case airport:{
                    Airport a = a_db.getAirport().getAirportByID(db_waypoint.ref);
                    a.runways = a_db.getRunways().getRunwaysByAirport(a.id);
                    newWaypoint.ref = a;
                    newWaypoint.name = a.name;
                    break;
                }
                case fix:{
                    Fix f = a_db.getFixes().getFixesByID(db_waypoint.ref);
                    newWaypoint.ref = f;
                    newWaypoint.name = f.name;
                    break;
                }
                case navaid:{
                    Navaid n = a_db.getNavaids().getNavaidByID(db_waypoint.ref);
                    newWaypoint.ref = n;
                    newWaypoint.name = n.name;
                    break;
                }
                case waypoint:{
                    newWaypoint.name = db_waypoint.name;
                    break;
                }
            }

            this.add(newWaypoint);
        }

        if (this.get(0).ref instanceof Airport)
            SelectedStartAirport = (Airport)this.get(0).ref;
        if (this.get(this.size()-1).ref instanceof Airport)
            SelectedEndAirport = (Airport)this.get(this.size()-1).ref;

        createLegs();
        setupRouteVariables();
        DrawRoute(mMap);

        if (routeEvents != null) routeEvents.RouteUpdated(this);
    }

    public Long GetTotalDistanceFromLocation(FspLocation location)
    {
        // Find the nearest waypoint
        // Check the coarse to the waypoint
            // may not differ more than 180 compared to current flight heading
            // if os check the second nearest waypoint and do the same heading check
        // calculate the distance to the found waypoint
        // add the distance from the found waypoint up to the destibation
        return  0l;
    }
}
