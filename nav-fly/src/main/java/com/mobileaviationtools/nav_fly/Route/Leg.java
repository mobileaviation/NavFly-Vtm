package com.mobileaviationtools.nav_fly.Route;

import android.content.Context;

import com.mobileaviationtools.airnavdata.Entities.Airport;
import com.mobileaviationtools.airnavdata.Entities.Navaid;
import com.mobileaviationtools.nav_fly.Markers.Route.RouteLegSymbol;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.operation.buffer.BufferOp;
import org.locationtech.jts.operation.buffer.BufferParameters;
import org.oscim.core.GeoPoint;

public class Leg {
    private Context context;
    public Geometry legBuffer;

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
        calculateLegBuffer();
    }

    private void calculateLegVariables()
    {
        bearing = startWaypoint.point.bearingTo(endWaypoint.point);
        distance = startWaypoint.point.sphericalDistance(endWaypoint.point);
        distanceNm = distance * meterToNMile;

        // time calculation
        groundspeed = indicatedAirspeed;
        heading = bearing;

        double windrad = Math.toRadians(windDirection);
        double dirrad = Math.toRadians(bearing);
        double zijwind = Math.sin(windrad-dirrad) * windSpeed;
        double langswind = Math.cos(windrad-dirrad) * windSpeed;
        double opstuurhoek = (zijwind/indicatedAirspeed) * 60;
        heading = bearing + opstuurhoek;
        if (heading<0) heading = 360 + heading;
        if (heading>359) heading = heading - 360;

        double gs = indicatedAirspeed - langswind;

        //TODO calculate the speed with the current winds
        timeMin = (getDistanceNM() / gs) * 60;
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

    private void calculateLegBuffer()
    {
        GeometryFactory factory = new GeometryFactory();
        Geometry leg = factory.createLineString(getLegCoordinates());
        legBuffer = leg.buffer(0.175d, 10, BufferParameters.CAP_ROUND);
    }

    public Geometry getLegBuffer()
    {
        return legBuffer;
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

    private double bearing;   // True to magnetic
    private double heading;   // To Fly, corrected for the wind

    public double getBearing() {
        return bearing;
    }
    public double getHeading() { return heading; }

    private double indicatedAirspeed = 100;

    private double windDirection = 0;

    private double windSpeed = 0;

    private double groundspeed;

    public String legName;
}
