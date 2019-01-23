package com.mobileaviationtools.nav_fly.Route.HeightMap;

import com.example.aircraft.Enums.FlightStage;
import com.mobileaviationtools.airnavdata.Entities.Country;

import org.locationtech.jts.geom.Coordinate;

import java.util.Comparator;
import java.util.Date;

public class ExtCoordinate extends Coordinate {
    public ExtCoordinate(double x, double y)
    {
        super(x,y);
        stage = FlightStage.platform;
        distanceToNext_meter = 0;
    }

    public ExtCoordinate(Coordinate c)
    {
        super(c);
        stage = FlightStage.platform;
        distanceToNext_meter = 0;
    }

    public double elevation;
    public double altitude;
    public double getAltitudeFt()
    {
        return altitude * 3.2808399f;
    }
    public double distanceToNext_meter;

    public Date date;

    public FlightStage stage;

}
