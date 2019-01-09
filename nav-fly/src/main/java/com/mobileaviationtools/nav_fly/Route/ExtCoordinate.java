package com.mobileaviationtools.nav_fly.Route;

import com.mobileaviationtools.airnavdata.Entities.Country;

import org.locationtech.jts.geom.Coordinate;

public class ExtCoordinate extends Coordinate {
    public ExtCoordinate(double x, double y)
    {
        super(x,y);
    }

    public ExtCoordinate(Coordinate c)
    {
        super(c);
    }

    public double elevation;
    public double altitude;
}
