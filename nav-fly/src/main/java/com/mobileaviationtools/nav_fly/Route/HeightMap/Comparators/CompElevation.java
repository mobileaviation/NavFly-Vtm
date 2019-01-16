package com.mobileaviationtools.nav_fly.Route.HeightMap.Comparators;

import com.mobileaviationtools.nav_fly.Route.HeightMap.ExtCoordinate;

import java.util.Comparator;

public class CompElevation implements Comparator<ExtCoordinate>
{
    public int compare(ExtCoordinate a, ExtCoordinate b)
    {
        if (a.elevation < b.elevation) return -1;
        if (a.elevation == b.elevation) return 0;
        return 1;
    }
}
