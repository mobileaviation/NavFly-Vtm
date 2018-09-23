package com.mobileaviationtools.nav_fly.Route;

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
}
