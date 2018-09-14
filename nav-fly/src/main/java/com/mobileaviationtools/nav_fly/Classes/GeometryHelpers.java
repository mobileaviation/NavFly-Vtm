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
}
