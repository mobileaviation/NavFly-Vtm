package com.mobileaviationtools.nav_fly.Route;

import android.util.Log;

import com.example.aircraft.Aircraft;
import com.mobileaviationtools.airnavdata.AirnavDatabase;
import com.mobileaviationtools.airnavdata.AirnavRouteDatabase;
import com.mobileaviationtools.airnavdata.Entities.Airport;
import com.mobileaviationtools.airnavdata.Entities.Fix;
import com.mobileaviationtools.airnavdata.Entities.Navaid;
import com.mobileaviationtools.nav_fly.Classes.Helpers;
import com.mobileaviationtools.nav_fly.Classes.MarkerDragEvent;
import com.mobileaviationtools.nav_fly.GlobalVars;
import com.mobileaviationtools.nav_fly.Info.Cities;
import com.mobileaviationtools.nav_fly.Info.City;
import com.mobileaviationtools.nav_fly.Location.FspLocation;
import com.mobileaviationtools.nav_fly.Route.HeightMap.RoutePoints;

import org.oscim.backend.CanvasAdapter;
import org.oscim.backend.canvas.Canvas;
import org.oscim.core.GeoPoint;
import org.oscim.event.Gesture;
import org.oscim.event.MotionEvent;
import org.oscim.layers.marker.MarkerItem;
import org.oscim.map.Map;

import java.util.ArrayList;
import java.util.Date;

public class Route extends ArrayList<Waypoint> {
    public Route(String name, GlobalVars vars)
    {
        this.name = name;
        this.id = -1l;
        this.vars = vars;
        this.createdDate = new Date();
        this.aircraft = vars.aircraft;
        legs = new Legs();
    }

    private String TAG = "Route";

    public String name;
    public Long id;
    public Date createdDate;
    public Date modifiedDate;
    public String elevation_json;
    public GlobalVars vars;

    private Aircraft aircraft;
    public Aircraft getAircraft() { return  aircraft; }

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

    private double proposedAltitude = 3000;

    public double getProposedAltitude() { return proposedAltitude; }

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

    private Legs legs;

    public Legs getLegs() {
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
        setAirplaneStartLocation();

        if (routeEvents != null) routeEvents.NewRouteCreated(this);
    }

    private void setEndAirport()
    {
        Waypoint endWaypoint = Waypoint.CreateWaypoint(SelectedEndAirport);
        this.add(endWaypoint);

        createLegs();
        setupRouteVariables();
        DrawRoute(vars.map);

        updateLegData();
        vars.dashboardFragment.setLocation(vars.airplaneLocation, indicatedAirspeed);

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
    private LegBufferLayer legBufferLayer;
    private Map mMap;
    private MarkerDragEvent onWaypointDrag;


    public void DrawRoute(Map map)
    {
        mMap = map;
        if (size()>0) {
            if (routePathLayer == null) {
                createNewPathLayer(map);
                createNewMarkerLayer(map);
                createNewBufferLayer();
            }
            routePathLayer.clearPath();
            for (Waypoint w : this)
            {
                routePathLayer.AddWaypoint(w);
                waypointLayer.PlaceMarker(w);
            }

            //legBufferLayer.ShowRouteBuffers();
            routePathLayer.update();
        }
    }

    private RoutePoints routePoints;
    public RoutePoints getRoutePoints() { return routePoints; }
    public void setupRoutePoints(RoutePoints.RoutePointsEvents routePointsEvents, Boolean parseFromService)
    {
        routePoints = new RoutePoints(vars);
        routePoints.SetupPoints(this, routePointsEvents, parseFromService);
    }

    public void drawRoutePoints()
    {
        legBufferLayer.showRoutePoints();
    }


    private void createNewBufferLayer()
    {
        legBufferLayer = new LegBufferLayer(vars, this, true);
    }

    private void createNewMarkerLayer(Map map)
    {
        waypointLayer = new WaypointLayer(map, null, vars.context);
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
                    item.getWaypoint().name = waypointName(newLocation);
                    updateLegs(item.getWaypoint());
                    setupRouteVariables();
                    DrawRoute(mMap);
                    setDeviationLineStartLocation(newLocation);
                    updateLegData();
                    vars.dashboardFragment.setLocation(vars.airplaneLocation, indicatedAirspeed);

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

    public void setAirplaneLocation(FspLocation location)
    {
        Leg l = legs.getActiveLeg(location);
        if (l != null) {
            routePathLayer.SelectLeg(l);
        }
        vars.airplaneLocation.SetDistanceRemaining(legs.remainingDistanceNM(location));
    }

    private void createNewPathLayer(Map map)
    {
        routePathLayer = new RoutePathLayer(map, 0xFF84e900, CanvasAdapter.getScale() * 5, vars){
            @Override
            public boolean onGesture(Gesture g, MotionEvent e) {
                if (g instanceof Gesture.Tap) {
                    if (contains(e.getX(), e.getY())) {
                        Log.i(TAG, "Tapped on route, at: " + mMap.viewport().fromScreenPoint(e.getX(), e.getY()));
                        GeoPoint point = mMap.viewport().fromScreenPoint(e.getX(), e.getY());

                        Leg selectedLeg = legs.findLeg(point);
                        if (selectedLeg != null)
                        {
                            // Test
                            //routePathLayer.SelectLeg(selectedLeg);


                            Waypoint newWaypoint = InsertnewWaypoint(point, selectedLeg);
                            createLegs();
                            setupRouteVariables();
                            clearPathLayer();
                            DrawRoute(mMap);
                            setDeviationLineStartLocation(point);
                            updateLegData();
                            vars.dashboardFragment.setLocation(vars.airplaneLocation, indicatedAirspeed);

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
        newWaypoint.name = waypointName(point);
        newWaypoint.type = WaypointType.waypoint;
        Integer index = indexOf(selectedLeg.endWaypoint);
        Route.this.add(index, newWaypoint);

        return  newWaypoint;
    }

    private String waypointName(GeoPoint point)
    {
        Cities cities = new Cities(vars.context, point);
        String name = "LON"+point.longitudeE6 + " LAT"+ point.latitudeE6;
        if (cities.size()>0)
        {
            City nearestCity = cities.get(cities.getNearestCity());
            name = nearestCity.name + " " + nearestCity.getDistanceNMStr() + ", " + nearestCity.getBearingStr();
        }

//        for (City c : cities)
//        {
//            Log.i(TAG, "Found City : " + c.name + " distance: " + c.distance.toString());
//        }
        return name;
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

    private void clearLegBufferLayer()
    {
        if (legBufferLayer != null)
            legBufferLayer.ClearLayer();
    }

    public void ClearRoute(Map map)
    {
        clearPathLayer();
        clearMarkerLayer();
        clearLegBufferLayer();
        SelectedStartAirport = null;
        SelectedEndAirport = null;
        legs.clear();
        this.clear();
    }

    public com.mobileaviationtools.airnavdata.Entities.Route getRouteEntity()
    {
        com.mobileaviationtools.airnavdata.Entities.Route routeEntity = new
                com.mobileaviationtools.airnavdata.Entities.Route();
        routeEntity.name = name;
        this.modifiedDate = new Date();
        routeEntity.modifiedDate = this.modifiedDate.getTime();
        routeEntity.createdDate = this.createdDate.getTime();
        routeEntity.elevation_json = this.elevation_json;
        return routeEntity;
    }

    public void saveRoute(String name)
    {
        AirnavRouteDatabase db = AirnavRouteDatabase.getInstance(vars.context);

        this.name = name;
        com.mobileaviationtools.airnavdata.Entities.Route routeEntity =
                getRouteEntity();

        if (this.id>0)
        {
            this.modifiedDate = new Date();
            db.getRoute().UpdateRoute(this.id, this.modifiedDate.getTime());
            db.getWaypoint().DeleteWaypointsByRouteID(this.id);
        }
        else
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
        AirnavDatabase a_db = AirnavDatabase.getInstance(vars.context);
        AirnavRouteDatabase db = AirnavRouteDatabase.getInstance(vars.context);
        com.mobileaviationtools.airnavdata.Entities.Route routeEntity = db.getRoute().getRouteById(routeId);
        this.id = routeEntity.id;
        this.name = routeEntity.name;
        this.createdDate = new Date(routeEntity.createdDate);
        this.modifiedDate = new Date(routeEntity.modifiedDate);
        this.elevation_json = routeEntity.elevation_json;

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
        setAirplaneStartLocation();

        if (routeEvents != null) routeEvents.RouteOpened(this);
    }

    private void setAirplaneStartLocation()
    {
        vars.airplaneLocation.setGeopoint(this.get(0).point);
        updateLegData();
        vars.mAircraftLocationLayer.UpdateLocation(vars.airplaneLocation);
        vars.dashboardFragment.setLocation(vars.airplaneLocation, indicatedAirspeed);

        setDeviationLineStartLocation(this.get(0).point);
    }

    private void updateLegData()
    {
        Leg l = legs.getActiveLeg(vars.airplaneLocation);
        if (l != null) {
            routePathLayer.SelectLeg(l);
            vars.airplaneLocation.SetDistanceRemaining(legs.remainingDistanceNM(vars.airplaneLocation));
        }
    }

    private void setDeviationLineStartLocation(GeoPoint point)
    {
        vars.doDeviationLineFromLocation.setGeopoint(point);
        vars.deviationLineLayer.drawDeviationLine(vars.doDeviationLineFromLocation, vars.mapCenterLocation);
    }

}
