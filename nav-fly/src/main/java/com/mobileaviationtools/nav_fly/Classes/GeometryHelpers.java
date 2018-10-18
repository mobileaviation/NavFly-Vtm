package com.mobileaviationtools.nav_fly.Classes;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKTReader;
import org.oscim.core.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public class GeometryHelpers {
    public static List<GeoPoint> getGeoPoints(Geometry geometry)
    {
        try {
            ArrayList<GeoPoint> points = new ArrayList<>();
            Coordinate[] coordinates = geometry.getCoordinates();
            for (Coordinate coordinate : coordinates)
            {
                points.add(new GeoPoint(coordinate.y, coordinate.x));
            }
            return points;
        }
        catch (Exception ee)
        {
            return null;
        }
    }

    public static GeoPoint midPoint(GeoPoint point1 ,GeoPoint point2){

        double dLon = Math.toRadians(point2.getLongitude() - point1.getLongitude());
        //convert to radians
        double lat1 = Math.toRadians(point1.getLatitude());
        double lat2 = Math.toRadians(point2.getLatitude());
        double lon1 = Math.toRadians(point1.getLongitude());

        double Bx = Math.cos(lat2) * Math.cos(dLon);
        double By = Math.cos(lat2) * Math.sin(dLon);
        double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2), Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By));
        double lon3 = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);

        return new GeoPoint(Math.toDegrees(lat3), Math.toDegrees(lon3));
    }
}
