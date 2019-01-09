package com.mobileaviationtools.nav_fly.Route;

import android.util.Log;

import com.mobileaviationtools.weater_notam_data.Elevation.ElevationResponseEvent;
import com.mobileaviationtools.weater_notam_data.Elevation.elevation;
import com.mobileaviationtools.weater_notam_data.services.ElevationService;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.linearref.LengthIndexedLine;
import org.oscim.core.GeoPoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RoutePoints extends ArrayList<ExtCoordinate> {
    private Double pointsCount = 100d;
    private Geometry routeGeom;
    public String TAG = "RoutePoints";
    private elevation _elevation;

    public RoutePoints()
    {

    }

    public Boolean SetupPoints(Route route)
    {
        ExtCoordinate[] c = new ExtCoordinate[route.size()];
        Integer i=0;
        for (Waypoint p: route)
        {
            c[i++] = new ExtCoordinate(p.point.getLongitude(), p.point.getLatitude());
        }

        GeometryFactory factory = new GeometryFactory();
        if (calcRoutePoints(factory.createLineString(c))) {
            getElevationData(c, route.getProposedAltitude());
            return true;
        }
        else
            return false;
    }

    private void getElevationData(Coordinate[] points, final double altitude)
    {
        ElevationService service = new ElevationService();
        service.getElevationsByPolyline(points, Math.round(pointsCount), new ElevationResponseEvent() {
            @Override
            public void OnElevationResponse(elevation resp, String message) {
                _elevation = resp;
                Integer[] elv = _elevation.resourceSets[0].resources[0].elevations;
                Integer count = (RoutePoints.this.size()<elv.length) ? RoutePoints.this.size() : elv.length;
                for (Integer i=0; i<count; i++)
                {
                    ExtCoordinate cc = RoutePoints.this.get(i);
                    cc.elevation = elv[i];
                    cc.altitude = altitude;
                }
                Log.i(TAG, message);
            }

            @Override
            public void OnFailure(String message) {
                Log.e(TAG, message);
            }
        });
    }

    private void drawSideImage()
    {
        
    }

    private Boolean calcRoutePoints(Geometry routeGeom)
    {
        if (routeGeom.getLength()==0) return false;

        this.routeGeom = routeGeom;
        LengthIndexedLine lil = new LengthIndexedLine(routeGeom);
        Double lengthInc = routeGeom.getLength() / pointsCount;

        for (Integer i=0; i<pointsCount; i++) {
            this.add(new ExtCoordinate(lil.extractPoint(lengthInc * i)));
        }

        return true;

//        for (Coordinate c: this)
//        {
//            String cc = "(" + String.valueOf(c.y) + "," + String.valueOf(c.x) + ")";
//            routePointsStr = routePointsStr + cc + ",";
//        }
//        routePointsStr = routePointsStr.substring(0, routePointsStr.length()-1);
//
//        int i=0;
    }


}
