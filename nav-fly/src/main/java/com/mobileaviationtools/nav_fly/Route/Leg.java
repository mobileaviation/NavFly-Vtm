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
        startWaypoint.afterLeg = this;
        endWaypoint.beforeLeg = this;
        calculateLegVariables();

    }

    public Leg(Waypoint start, Waypoint end)
    {
        startWaypoint = start;
        endWaypoint = end;
        startWaypoint.afterLeg = this;
        endWaypoint.beforeLeg = this;
        calculateLegVariables();
    }

    public void setupRouteVariables(double indicatedAirspeed, double windSpeed, double windDirection)
    {
        this.indicatedAirspeed = indicatedAirspeed;
        this.windSpeed = windSpeed;
        this.windDirection = windDirection;
    }

    private void calculateLegVariables()
    {
        bearing = startWaypoint.point.bearingTo(endWaypoint.point);
        distance = startWaypoint.point.sphericalDistance(endWaypoint.point);

        // time calculation
    }

    public void UpdateLeg()
    {
        calculateLegVariables();
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
    private double meterToNMile = 0.000539956803d;
    public double getDistanceNM() {
        return distance * meterToNMile;
    }

    private double bearing;

    public double getBearing() {
        return bearing;
    }

    private double indicatedAirspeed = 100;

    private double windDirection = 0;

    private double windSpeed = 0;

    public String legName;
}
