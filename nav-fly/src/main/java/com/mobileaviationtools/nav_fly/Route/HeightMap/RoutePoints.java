package com.mobileaviationtools.nav_fly.Route.HeightMap;

import com.badlogic.gdx.utils.GdxNativesLoader;
import com.mobileaviationtools.nav_fly.GlobalVars;
import com.mobileaviationtools.nav_fly.Location.FspLocation;
import com.mobileaviationtools.nav_fly.Route.HeightMap.Comparators.CompAltitude;
import com.mobileaviationtools.nav_fly.Route.HeightMap.Comparators.CompElevation;
import com.mobileaviationtools.nav_fly.Route.Route;
import com.mobileaviationtools.nav_fly.Route.Waypoint;
import com.mobileaviationtools.weater_notam_data.Elevation.elevation;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.linearref.LengthIndexedLine;
import org.locationtech.jts.linearref.LinearLocation;
import org.locationtech.jts.linearref.LocationIndexedLine;
import org.oscim.core.GeoPoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class RoutePoints extends ArrayList<ExtCoordinate> {
    public interface RoutePointsEvents
    {
        public void OnPointsLoaded(RoutePoints route);
    }

    private Double pointsCount = 100d;
    private Geometry routeGeom;
    public String TAG = "RoutePoints";
    private elevation _elevation;
    private RoutePointsEvents routePointsEvents;
    private GlobalVars vars;
    private double totalDistance_meter;
    private LengthIndexedLine lengthIndexedLine;
    private Route route;

    public RoutePoints(GlobalVars vars)
    {
        this.vars = vars;
    }

    public double getTotalDistance_meter() {
        return totalDistance_meter;
    }

    public Boolean SetupPoints(Route route, RoutePointsEvents routePointsEvents, Boolean parseFromService)
    {
        this.routePointsEvents = routePointsEvents;
        this.route = route;
        ExtCoordinate[] c = new ExtCoordinate[route.size()];
        Integer i=0;
        for (Waypoint p: route)
        {
            c[i++] = new ExtCoordinate(p.point.getLongitude(), p.point.getLatitude());
        }

        GeometryFactory factory = new GeometryFactory();
        if (calcRoutePoints(factory.createLineString(c))) {
            getElevationData(c, route, parseFromService);
            return true;
        }
        else
            return false;
    }

    private void getElevationData(Coordinate[] points, Route route, Boolean parseFromService)
    {
        ElevationRouteService elevationRouteService = new ElevationRouteService(route, pointsCount);
        elevationRouteService.startProcessElevation(points, routePointsEvents, parseFromService );
    }


    private Boolean calcRoutePoints(Geometry routeGeom)
    {
        if (routeGeom.getLength()==0) return false;

        this.routeGeom = routeGeom;
        lengthIndexedLine = new LengthIndexedLine(routeGeom);
        //LocationIndexedLine locil = new LocationIndexedLine(routeGeom);
        Double totalLength = routeGeom.getLength();
        Double lengthInc = totalLength / pointsCount;
        this.totalDistance_meter = 0;


        for (Integer i=1; i<pointsCount; i++) {
            ExtCoordinate c1 = new ExtCoordinate(lengthIndexedLine.extractPoint(lengthInc * (i-1)));
            ExtCoordinate c2 = new ExtCoordinate(lengthIndexedLine.extractPoint(lengthInc * i));

            GeoPoint p1 = new GeoPoint(c1.y, c1.x);
            GeoPoint p2 = new GeoPoint(c2.y, c2.x);

            c1.distanceToNext_meter = p1.sphericalDistance(p2);
            this.totalDistance_meter = this.totalDistance_meter + c1.distanceToNext_meter;
            c1.index = lengthIndexedLine.indexOf(c1);

            this.add(c1);
        }

        return true;

    }

    public Double getIndexForLocation(FspLocation location)
    {
        Coordinate n = new Coordinate(location.getLongitude(), location.getLatitude());
        return lengthIndexedLine.indexOf(n);
    }

    public Double[] getWaypointIndexes()
    {
        if (route != null) {
            Double[] waypointIndexes = new Double[route.size()];
            int i=0;
            for (Waypoint w : route) {
                Coordinate n = new Coordinate(w.point.getLongitude(), w.point.getLatitude());
                waypointIndexes[i++] = lengthIndexedLine.indexOf(n);
            }
            return waypointIndexes;
        }
        else
             return null;
    }

    public ExtCoordinate findPointNearestTo(Double index)
    {
        ExtCoordinate c = this.get(0);
        for (ExtCoordinate c1: this)
        {
            double dif = Math.abs(c1.index-index);
            if (dif < Math.abs(c.index-index))
                c = c1;
        }

        return c;
    }

    public Double getStartIndex()
    {
        return lengthIndexedLine.getStartIndex();
    }

    public Double getEndIndex()
    {
        return lengthIndexedLine.getEndIndex();
    }

    public ArrayList<GeoPoint> getRoutePoints()
    {
        ArrayList<GeoPoint> points = new ArrayList<>();
        for (ExtCoordinate c : this)
        {
            GeoPoint p = new GeoPoint(c.y, c.x);
            points.add(p);
        }

        return points;
    }

    public ExtCoordinate getMaxElevation()
    {
        return Collections.max(this, new CompElevation());
    }

    public ExtCoordinate getMinElevation()
    {
        return Collections.min(this, new CompElevation());
    }

    public ExtCoordinate getMaxAltitude()
    {
        return Collections.max(this, new CompAltitude());
    }
}
