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

    public Leg(Waypoint start, Waypoint end)
    {
        startWaypoint = start;
        endWaypoint = end;
    }

    public Waypoint startWaypoint;
    public Waypoint endWaypoint;

    public String legName;
}
