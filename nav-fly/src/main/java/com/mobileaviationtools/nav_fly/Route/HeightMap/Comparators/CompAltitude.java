package com.mobileaviationtools.nav_fly.Route.HeightMap.Comparators;

import com.mobileaviationtools.nav_fly.Route.HeightMap.ExtCoordinate;

import java.util.Comparator;

public class CompAltitude implements Comparator<ExtCoordinate>
{
    public int compare(ExtCoordinate a, ExtCoordinate b)
    {
        if (a.altitude < b.altitude) return -1;
        if (a.altitude == b.altitude) return 0;
        return 1;
    }
}
