package com.mobileaviationtools.nav_fly.Route;

import com.mobileaviationtools.airnavdata.Entities.Airport;
import com.mobileaviationtools.airnavdata.Entities.Navaid;

import org.locationtech.jts.geom.Coordinate;
import org.oscim.core.GeoPoint;

public class Leg {
    public Leg(GeoPoint start, GeoPoint end, Route route)
    {
        startWaypoint = new Waypoint(start);
        endWaypoint = new Waypoint(end);
        startWaypoint.afterLeg = this;
        endWaypoint.beforeLeg = this;
        setupRouteVariables(route);
    }

    public Leg(Waypoint start, Waypoint end, Route route)
    {
        startWaypoint = start;
        endWaypoint = end;
        startWaypoint.afterLeg = this;
        endWaypoint.beforeLeg = this;
        setupRouteVariables(route);
    }

    public void setupRouteVariables(Route route)
    {
        this.indicatedAirspeed = route.getIndicatedAirspeed();
        this.windSpeed = route.getWindSpeed();
        this.windDirection = route.getWindDirection();
        calculateLegVariables();
    }

    private void calculateLegVariables()
    {
        bearing = startWaypoint.point.bearingTo(endWaypoint.point);
        distance = startWaypoint.point.sphericalDistance(endWaypoint.point);
        distanceNm = distance * meterToNMile;

        // time calculation
        groundspeed = indicatedAirspeed;
        //TODO calculate the speed with the current winds
        timeMin = (getDistanceNM() / indicatedAirspeed) * 60;
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
    private double distanceNm;
    private double timeMin;
    private double totalDistanceNm;

    public double getTotalDistanceNm() {
        return totalDistanceNm;
    }

    public void setTotalDistanceNm(double totalDistanceNm) {
        this.totalDistanceNm = totalDistanceNm;
    }

    private double totalTimeMin;

    public double getTotalTimeMin() {
        return totalTimeMin;
    }

    public void setTotalTimeMin(double totalTimeMin) {
        this.totalTimeMin = totalTimeMin;
    }

    public double getDistance() {
        return distance;
    }
    private double meterToNMile = 0.000539956803d;
    public double getDistanceNM() {
        return distanceNm;
    }
    public double getLegTimeMinutes()
    {
        return timeMin;
    }

    private double bearing;

    public double getBearing() {
        return bearing;
    }

    private double indicatedAirspeed = 100;

    private double windDirection = 0;

    private double windSpeed = 0;

    private double groundspeed;

    public String legName;
}
