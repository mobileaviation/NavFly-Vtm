package com.mobileaviationtools.nav_fly.Route;

import com.mobileaviationtools.airnavdata.Entities.Airport;

import org.oscim.core.GeoPoint;
import org.oscim.tiling.source.mapfile.Way;

public class Waypoint {
    public Waypoint(GeoPoint point)
    {
        this.point = point;
    }

    public Waypoint(double lat, double lon)
    {
        this.point = new GeoPoint(lat, lon);
    }

    public String name;
    public GeoPoint point;
    public WaypointMarkerItem marker;
    public WaypointType type;
    public Object ref;
    public Leg beforeLeg;
    public Leg afterLeg;

    public static Waypoint CreateWaypoint(Airport airport)
    {
        Waypoint waypoint = new Waypoint(new GeoPoint(airport.latitude_deg, airport.longitude_deg));
        waypoint.name = airport.name;
        waypoint.type = WaypointType.airport;
        waypoint.ref = airport;
        return waypoint;
    }
}
