package com.mobileaviationtools.nav_fly.Classes;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.util.GeometricShapeFactory;
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

    public static Coordinate getCoordinate(GeoPoint point)
    {
        return new Coordinate(point.getLongitude(), point.getLatitude());
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

    public static Geometry getPoint(GeoPoint p)
    {
        GeometryFactory factory = new GeometryFactory();
        return factory.createPoint(new Coordinate(p.getLongitude(), p.getLatitude()));
    }


    public static Geometry getCircle(GeoPoint p, double diameter)
    {
        double latitude = p.getLatitude();
        double longitude = p.getLongitude();
        double diameterInMeters = diameter; // meter

        GeometricShapeFactory shapeFactory = new GeometricShapeFactory();
        shapeFactory.setNumPoints(50); // adjustable
        shapeFactory.setCentre(new Coordinate(longitude, latitude));
        // Length in meters of 1° of latitude = always 111.32 km
        shapeFactory.setWidth(diameterInMeters/111320d);
        // Length in meters of 1° of longitude = 40075 km * cos( latitude ) / 360
        shapeFactory.setHeight(diameterInMeters / (40075000 * Math.cos(Math.toRadians(latitude)) / 360));

        return shapeFactory.createEllipse();
    }


}
