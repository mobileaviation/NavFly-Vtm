package com.mobileaviationtools.nav_fly.Route;

import android.content.Context;

import com.mobileaviationtools.airnavdata.Entities.Airport;
import com.mobileaviationtools.airnavdata.Entities.Navaid;
import com.mobileaviationtools.nav_fly.Markers.Route.RouteLegSymbol;

import org.locationtech.jts.geom.Coordinate;
import org.oscim.core.GeoPoint;

public class Leg {
    private Context context;

    public Leg(GeoPoint start, GeoPoint end, Context context)
    {
        startWaypoint = new Waypoint(start);
        endWaypoint = new Waypoint(end);
        startWaypoint.afterLeg = this;
        endWaypoint.beforeLeg = this;
        this.context = context;
        setupRouteVariables(null);
    }

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
        if (route != null) {
            this.indicatedAirspeed = route.getIndicatedAirspeed();
            this.windSpeed = route.getWindSpeed();
            this.windDirection = route.getWindDirection();
            this.context = route.vars.context;
        }
        calculateLegVariables();
        createRouteSymbol();
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

    private void createRouteSymbol()
    {
        symbol = RouteLegSymbol.GetRouteLegSymbol(this, context );
    }

    public void UpdateLeg()
    {
        calculateLegVariables();
    }

    public Waypoint startWaypoint;
    public Waypoint endWaypoint;
    public RouteLegSymbol symbol;

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
