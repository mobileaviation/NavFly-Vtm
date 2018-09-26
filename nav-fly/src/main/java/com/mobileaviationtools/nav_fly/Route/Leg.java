package com.mobileaviationtools.nav_fly.Route;

import com.mobileaviationtools.airnavdata.Entities.Airport;
import com.mobileaviationtools.airnavdata.Entities.Navaid;

import org.locationtech.jts.geom.Coordinate;
import org.oscim.core.GeoPoint;

public class Leg {
    public Leg(GeoPoint start, GeoPoint end)
    {
        startWaypoint = new Waypoint(start);
        endWaypoint = new Waypoint(end);
        calculateDistanceBearing();

    }

    public Leg(Waypoint start, Waypoint end)
    {
        startWaypoint = start;
        endWaypoint = end;
        calculateDistanceBearing();
    }

    private void calculateDistanceBearing()
    {
        bearing = startWaypoint.point.bearingTo(endWaypoint.point);
        distance = startWaypoint.point.distance(endWaypoint.point);
    }

    public Waypoint startWaypoint;
    public Waypoint endWaypoint;

    public Coordinate[] getLegCoordinates()
    {
        Coordinate[] coordinates = new Coordinate[2];
        coordinates[0] = new Coordinate(startWaypoint.point.getLongitude(), startWaypoint.point.getLatitude());
        coordinates[1] = new Coordinate(endWaypoint.point.getLongitude(), endWaypoint.point.getLatitude());
        return coordinates;
    }

    private double distance;

    public double getDistance() {
        return distance;
    }

    private double bearing;

    public double getBearing() {
        return bearing;
    }

    public String legName;
}
