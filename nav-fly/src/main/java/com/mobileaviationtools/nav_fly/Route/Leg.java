package com.mobileaviationtools.nav_fly.Route;

import com.mobileaviationtools.airnavdata.Entities.Airport;
import com.mobileaviationtools.airnavdata.Entities.Navaid;

import org.oscim.core.GeoPoint;

public class Leg {
    public Leg(GeoPoint start, GeoPoint end)
    {
        startWaypoint = new Waypoint(start);
        endWaypoint = new Waypoint(end);
    }

    public Leg(Airport start, Airport end)
    {
        startWaypoint = new Waypoint(new GeoPoint(start.latitude_deg, start.longitude_deg));
        startWaypoint.name = start.name;
        endWaypoint = new Waypoint(new GeoPoint(end.latitude_deg, end.longitude_deg));
        endWaypoint.name = end.name;
    }

    public Waypoint startWaypoint;
    public Waypoint endWaypoint;

    public String legName;
}
